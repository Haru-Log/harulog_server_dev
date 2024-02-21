package goojeans.harulog.challenge.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.challenge.domain.dto.request.ChallengeJoinRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeLeaveRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeAllResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.repository.ChallengeRepository;
import goojeans.harulog.challenge.repository.ChallengeUserRepository;
import goojeans.harulog.challenge.util.ChallengeRole;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.service.ChatRoomServiceImpl;
import goojeans.harulog.chat.service.ChatRoomUserServiceImpl;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.post.repository.PostRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock private PostRepository postRepository;
    @Mock private ChatRoomServiceImpl chatRoomService;
    @Mock private ChatRoomUserServiceImpl chatRoomUserService;

    private Users user;
    private Category category;
    private Challenge challenge;
    private final Long challengeId = 1L;
    private final Long userId = 1L;
    private final Long categoryId = 1L;

    @BeforeEach
    void beforeEach() {

        user = Users.builder()
                .id(userId)
                .email("test@test.com")
                .password("test")
                .userName("test")
                .nickname("tester")
                .socialType(SocialType.HARU)
                .build();

        category = Category.builder()
                .categoryId(categoryId)
                .categoryName("운동")
                .build();

        challenge = Challenge.builder()
                .challengeId(challengeId)
                .challengeTitle("test challenge")
                .challengeContent("test")
                .challengeGoal(3)
                .submission("test")
                .imageUrl("test image")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .chatroom(ChatRoom.createChallenge("test challenge", null))
                .category(category)
                .build();
    }

    @Test
    @DisplayName("새 챌린지 생성 후 첫 참여하기")
    void createChallenge() {
        ChallengeRequest request = new ChallengeRequest("tester", "test challenge", 3, "test", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "운동");
        List<Post> posts = new ArrayList<>();

        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challengeUser.updateRole();

        ChatRoom room = ChatRoom.builder().id(UUID.randomUUID().toString()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryName("운동")).thenReturn(Optional.of(category));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);
        when(postRepository.findByUserIdAndToday(userId, LocalDate.now().atStartOfDay())).thenReturn(posts);
        when(chatRoomService.createChallengeChatRoom(request.getChallengeTitle(), "image/challenge/default.png")).thenReturn(room);

        Response<ChallengeResponse> response = challengeService.registerChallenge(user.getId(), request);

        Assertions.assertThat(response.getData().getChallengeUserList()).hasSize(1);
        Assertions.assertThat(response.getData().getChallengeUserList().get(0).getRole()).isEqualTo(ChallengeRole.LEADER);
        log.info("user.getChallengeUsers().size()) = {}", user.getChallengeUsers().size());
    }

    @Test
    @DisplayName("이미 참여하고 있는 카테고리의 챌린지를 생성하면 에러 발생")
    void createChallengeWithError() {

        ChallengeRequest request = new ChallengeRequest("tester", "test challenge", 3, "test", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "운동");
        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList(challenge));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryName("운동")).thenReturn(Optional.of(category));
        when(challengeRepository.findAllByUserId(userId)).thenReturn(userChallenges);

        Assertions.assertThatThrownBy(() -> challengeService.registerChallenge(user.getId(), request)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하는 챌린지에 참여하기")
    void joinChallenge() {

        ChallengeJoinRequest request = new ChallengeJoinRequest(challengeId);
        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList());
        List<Post> posts = new ArrayList<>();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));
        when(challengeRepository.findAllByUserId(userId)).thenReturn(userChallenges);
        when(postRepository.findByUserIdAndToday(userId, LocalDate.now().atStartOfDay())).thenReturn(posts);

        Response<ChallengeResponse> response = challengeService.joinChallenge(user.getId(), request);
        Assertions.assertThat(response.getData().getChallengeUserList().get(0).getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("이미 참여하고 있는 카테고리의 챌린지에 참여하면 에러 발생")
    void joinChallengeWithError() {

        ChallengeJoinRequest request = new ChallengeJoinRequest(challengeId);
        List<Challenge> userChallenges = new ArrayList<>(Arrays.asList(challenge));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));
        when(challengeRepository.findAllByUserId(userId)).thenReturn(userChallenges);

        Assertions.assertThatThrownBy(() -> challengeService.joinChallenge(user.getId(), request)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("참여하고 있는 챌린지 나가기")
    void leaveChallenge() {

        ChallengeLeaveRequest request = new ChallengeLeaveRequest(challengeId);
        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));
        when(challengeUserRepository.findChallengeUserByUserAndChallenge(userId, challengeId)).thenReturn(Optional.of(challengeUser));

        challenge.addChallengeUser(challengeUser);

        Response<Void> response = challengeService.leaveChallenge(user.getId(), request);
        challenge.removeChallengeUser(challengeUser);

        Assertions.assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("챌린지 단건 조회")
    void getChallenge() {
        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);

        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));
        when(challengeUserRepository.findChallengeUserByUserAndChallenge(userId, challengeId)).thenReturn(Optional.of(challengeUser));

        Response<ChallengeResponse> response = challengeService.getChallenge(userId, challenge.getChallengeId());
        Assertions.assertThat(response.getData().getChallengeTitle()).isEqualTo("test challenge");
    }

    @Test
    @DisplayName("챌린지 모두 조회")
    void getAllChallenge() {

        List<Challenge> challenges = new ArrayList<>(Arrays.asList(challenge));

        when(challengeRepository.findAll()).thenReturn(challenges);

        Response<List<ChallengeAllResponse>> response = challengeService.getAllChallenge();
        Assertions.assertThat(response.getData()).hasSize(1);
    }

    @Test
    @DisplayName("한 사용자가 참여하는 챌린지 모두 조회")
    void getUserChallenge() {

        List<Challenge> challenges = new ArrayList<>(Arrays.asList(challenge));

        when(challengeRepository.findAllByUserId(userId)).thenReturn(challenges);

        Response<List<ChallengeAllResponse>> response = challengeService.getUserChallenge(user.getId());
        Assertions.assertThat(response.getData()).hasSize(1);
    }

    @Test
    @DisplayName("챌린지 삭제하기")
    void deleteChallenge() {
        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challengeUser.updateRole();
        challenge.getChallengeUserList().add(challengeUser);

        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));
        when(challengeUserRepository.findChallengeUserByUserAndChallenge(userId, challengeId)).thenReturn(Optional.of(challengeUser));

        Response<Void> response = challengeService.deleteChallenge(userId, challengeId);
        Assertions.assertThat(response.getStatus()).isEqualTo(200);
        verify(challengeRepository).delete(challenge);
    }

    @Test
    @DisplayName("리더가 아니라면 챌린지 삭제할 때 에러 발생")
    void deleteChallengeWithError() {
        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challenge.getChallengeUserList().add(challengeUser);

        when(challengeRepository.findByChallengeId(challengeId)).thenReturn(Optional.of(challenge));

        Assertions.assertThatThrownBy(() -> challengeService.deleteChallenge(userId, challengeId)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("챌린지 4개 이상일 때 랜덤 조회")
    void findRandomChallengeLimitFour() {

        List<Challenge> challenges = new ArrayList<>();
        challenges.add(challenge);

        for (long i = 2L; i <= 4L; i++) {
            Challenge newChallenge = Challenge.builder()
                    .challengeId(i)
                    .challengeTitle("test challenge" + "i")
                    .challengeContent("test")
                    .challengeGoal(3)
                    .submission("test")
                    .imageUrl("test image")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(1))
                    .chatroom(ChatRoom.createChallenge("test challenge", null))
                    .category(category)
                    .build();

            challenges.add(newChallenge);
        }

        when(challengeRepository.count()).thenReturn(4L);
        when(challengeRepository.findRandomLimitFour()).thenReturn(challenges);

        Response<List<ChallengeAllResponse>> response = challengeService.getRandomChallenge();

        Assertions.assertThat(response.getData()).hasSize(4);
        verify(challengeRepository, times(1)).findRandomLimitFour();
        verify(challengeRepository, never()).findAll();
    }

    @Test
    @DisplayName("챌린지 4개 미만일 때 랜덤 조회")
    void findRandomChallengeLessThanFour() {

        Challenge Challenge2 = Challenge.builder()
                .challengeId(2L)
                .challengeTitle("test challenge" + "i")
                .challengeContent("test")
                .challengeGoal(3)
                .submission("test")
                .imageUrl("test image")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .chatroom(ChatRoom.createChallenge("test challenge", null))
                .category(category)
                .build();

        List<Challenge> challenges = new ArrayList<>();
        challenges.add(challenge);
        challenges.add(Challenge2);

        when(challengeRepository.count()).thenReturn(2L);
        when(challengeRepository.findAll()).thenReturn(challenges);

        Response<List<ChallengeAllResponse>> response = challengeService.getRandomChallenge();

        Assertions.assertThat(response.getData()).hasSize(2);
        verify(challengeRepository, times(1)).findAll();
        verify(challengeRepository, never()).findRandomLimitFour();
    }
}