package goojeans.harulog.challenge.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRegisterRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.repository.ChallengeRepository;
import goojeans.harulog.challenge.repository.ChallengeUserRepository;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    private ChallengeRepository challengeRepository;
    private ChallengeUserRepository challengeUserRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    @Override
    public Response<ChallengeResponse> registerChallenge(Long userId, ChallengeRegisterRequest request) {

        Users user = userRepository.findUsersById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Category category = categoryRepository.findByCategoryName(request.getCategoryName()).orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));

        if (!canJoinChallenge(user, request.getCategoryName())) {
            throw new BusinessException(ResponseCode.CHALLENGE_ALREADY_JOIN);
        }

        //Challenge 생성에 필요한 ChatRoom 생성
        ChatRoom chatRoom = ChatRoom.create(request.getChallengeTitle());

        //Challenge 생성
        Challenge challenge = Challenge.builder()
                .challengeTitle(request.getChallengeTitle())
                .challengeContent(request.getChallengeContent())
                .challengeGoal(request.getChallengeGoal())
                .submission(request.getSubmission())
                .imageUrl(request.getImageUrl())
                .chatroom(chatRoom)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(category)
                .build();

        challengeRepository.save(challenge);

        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challenge.addChallengeUser(challengeUser);

        //첫 참여자라면 리더로 역할을 바꿔준다. 챌린지 생성자 = 방장
        if (challenge.getChallengeUserList().size() == 1) {
            challengeUser.updateRole();
        }

        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge);
        return Response.ok(challengeResponse);
    }

    @Override
    public Response<ChallengeResponse> joinChallenge(Long userId, Long challengeId) {

        Users user = userRepository.findUsersById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        if (!canJoinChallenge(user, challenge.getCategory().getCategoryName())) {
            throw new IllegalStateException("이미 해당 카테고리의 챌린지에 참여 중입니다.");
        }

        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challenge.addChallengeUser(challengeUser);

        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge);
        return Response.ok(challengeResponse);

    }

    @Override
    public Response<Void> leaveChallenge(Long userId, Long challengeId) {

        Users user = userRepository.findUsersById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));
        ChallengeUser challengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(user.getId(), challenge.getChallengeId()).orElseThrow(() -> new NoSuchElementException("해당 챌린지 유저가 존재하지 않습니다."));

        //참여자가 나가고자 하는 1명뿐이라면 챌린지 자체도 삭제한다.
        if (challenge.getChallengeUserList().size() == 1) {
            challengeRepository.delete(challenge);
        } else {
            challengeUserRepository.delete(challengeUser);
            challenge.removeChallengeUser(challengeUser);
        }

        return Response.ok();
    }

    @Override
    public Response<ChallengeResponse> getChallenge(Long ChallengeId) {

        Challenge challenge = challengeRepository.findByChallengeId(ChallengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge);
        return Response.ok(challengeResponse);
    }

    @Override
    public Response<List<ChallengeResponse>> getAllChallenge() {

        List<Challenge> challenges = challengeRepository.findAll();

        List<ChallengeResponse> challengeResponses = challenges.stream()
                .map(ChallengeResponse::of)
                .collect(Collectors.toList());

        return Response.ok(challengeResponses);
    }

    @Override
    public Response<List<ChallengeResponse>> getUserChallenge(Long userId) {

        List<Challenge> challenges = challengeRepository.findAllByUserId(userId);

        List<ChallengeResponse> challengeResponses = challenges.stream()
                .map(ChallengeResponse::of)
                .collect(Collectors.toList());

        return Response.ok(challengeResponses);
    }

    //한 사용자는 한 카테고리에 한 챌린지만 참여 가능하다.
    public boolean canJoinChallenge(Users user, String categoryName) {
        List<Challenge> userChallenges = challengeRepository.findAllByUserId(user.getId());

        for (Challenge challenge : userChallenges) {
            if (challenge.getCategory().getCategoryName().equals(categoryName)) {
                return false;
            }
        }
        return true;
    }
}
