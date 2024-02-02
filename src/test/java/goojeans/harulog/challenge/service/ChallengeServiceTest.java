package goojeans.harulog.challenge.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRegisterRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.repository.ChallengeRepository;
import goojeans.harulog.challenge.repository.ChallengeUserRepository;
import goojeans.harulog.challenge.util.ChallengeRole;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Spy
    @InjectMocks
    private ChallengeServiceImpl challengeService;

    @Mock private ChallengeRepository challengeRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ChallengeUserRepository challengeUserRepository;

    private Users user;
    private Category category;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {

        user = Users.builder()
                .id(1L)
                .email("test@test.com")
                .password("test")
                .userName("test")
                .nickname("tester")
                .socialType(SocialType.HARU)
                .build();

        category = Category.builder()
                .categoryId(1L)
                .categoryName("운동")
                .build();

        challenge = Challenge.builder()
                .challengeId(1L)
                .challengeTitle("test challenge")
                .challengeContent("test")
                .challengeGoal(3)
                .submission("test")
                .imageUrl("test image")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .chatroom(ChatRoom.createChallenge("test challenge",null))
                .category(category)
                .build();
    }

    @Test
    @DisplayName("새 챌린지 생성 후 첫 참여하기")
    void createChallenge() {

        ChallengeRegisterRequest request = new ChallengeRegisterRequest("tester", "test challenge", 3, "test", "test", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "운동");

        when(userRepository.findUsersById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryName("운동")).thenReturn(Optional.of(category));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

        Response<ChallengeResponse> response = challengeService.registerChallenge(user.getId(), request);

        Assertions.assertThat(response.getData().getChallengeUserList()).hasSize(1);
        Assertions.assertThat(response.getData().getChallengeUserList().get(0).getRole()).isEqualTo(ChallengeRole.LEADER);
        log.info("user.getChallengeUsers().size()) = {}", user.getChallengeUsers().size());
    }

    @Test
    @DisplayName("이미 참여하고 있는 카테고리의 챌린지를 생성하면 에러 발생")
    void createChallengeWithError() {

        ChallengeRegisterRequest request = new ChallengeRegisterRequest("tester", "test challenge", 3, "test", "test", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "운동");
        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList(challenge));

        when(userRepository.findUsersById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryName("운동")).thenReturn(Optional.of(category));
        when(challengeRepository.findAllByUserId(1L)).thenReturn(userChallenges);

        Assertions.assertThatThrownBy(() -> challengeService.registerChallenge(user.getId(), request)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하는 챌린지에 참여하기")
    void joinChallenge() {

        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList());

        when(userRepository.findUsersById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(1L)).thenReturn(Optional.of(challenge));
        when(challengeRepository.findAllByUserId(1L)).thenReturn(userChallenges);

        Response<ChallengeResponse> response = challengeService.joinChallenge(user.getId(), challenge.getChallengeId());
        Assertions.assertThat(response.getData().getChallengeUserList().get(0).getUser().getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("이미 참여하고 있는 카테고리의 챌린지에 참여하면 에러 발생")
    void joinChallengeWithError() {
        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList(challenge));

        when(userRepository.findUsersById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(1L)).thenReturn(Optional.of(challenge));
        when(challengeRepository.findAllByUserId(1L)).thenReturn(userChallenges);

        Assertions.assertThatThrownBy(() -> challengeService.joinChallenge(user.getId(), challenge.getChallengeId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("참여하고 있는 챌린지 나가기")
    void leaveChallenge() {

        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);

        when(userRepository.findUsersById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(1L)).thenReturn(Optional.of(challenge));
        when(challengeUserRepository.findChallengeUserByUserAndChallenge(1L, 1L)).thenReturn(Optional.of(challengeUser));

        challenge.addChallengeUser(challengeUser);

        Response<Void> response = challengeService.leaveChallenge(user.getId(), challenge.getChallengeId());
        challenge.removeChallengeUser(challengeUser);

        Assertions.assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("챌린지 단건 조회")
    void getChallenge() {

        when(challengeRepository.findByChallengeId(1L)).thenReturn(Optional.of(challenge));

        Response<ChallengeResponse> response = challengeService.getChallenge(challenge.getChallengeId());
        Assertions.assertThat(response.getData().getChallengeTitle()).isEqualTo("test challenge");
    }

    @Test
    @DisplayName("챌린지 모두 조회")
    void getAllChallenge() {

        List<Challenge> challenges = new ArrayList<>(Arrays.asList(challenge));

        when(challengeRepository.findAll()).thenReturn(challenges);

        Response<List<ChallengeResponse>> response = challengeService.getAllChallenge();
        Assertions.assertThat(response.getData()).hasSize(1);
    }

    @Test
    @DisplayName("한 사용자가 참여하는 챌린지 모두 조회")
    void getUserChallenge() {

        List<Challenge> challenges = new ArrayList<>(Arrays.asList(challenge));

        when(challengeRepository.findAllByUserId(1L)).thenReturn(challenges);

        Response<List<ChallengeResponse>> response = challengeService.getUserChallenge(user.getId());
        Assertions.assertThat(response.getData()).hasSize(1);
    }
}