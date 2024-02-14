package goojeans.harulog.challenge.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.challenge.domain.dto.request.ChallengeJoinRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeLeaveRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeAllResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeUsersResponse;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.repository.ChallengeRepository;
import goojeans.harulog.challenge.repository.ChallengeUserRepository;
import goojeans.harulog.challenge.util.ChallengeRole;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.service.ChatRoomService;
import goojeans.harulog.chat.service.ChatRoomUserService;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.post.repository.PostRepository;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    // 채팅
    private final ChatRoomService chatRoomService;
    private final ChatRoomUserService chatRoomUserService;



    @Override
    public Response<ChallengeResponse> registerChallenge(Long userId, ChallengeRequest request) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Category category = categoryRepository.findByCategoryName(request.getCategoryName()).orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));

        if (!canJoinChallenge(user, request.getCategoryName())) {
            throw new BusinessException(ResponseCode.CHALLENGE_CAT_ALREADY_PARTICIPATE);
        }

        //Challenge 생성에 필요한 ChatRoom 생성
        // todo: 채팅방 이미지 넣기
        ChatRoom chatRoom = chatRoomService.createChallengeChatRoom(request.getChallengeTitle(), null);

        // 채팅방에 리더 추가
        chatRoomUserService.addUser(chatRoom, user);

        //Challenge 생성
        Challenge challenge = Challenge.builder()
                .challengeTitle(request.getChallengeTitle())
                .challengeContent(request.getChallengeContent())
                .challengeGoal(request.getChallengeGoal())
                .submission(request.getSubmission())
//                .imageUrl(request.getImageUrl())
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

        List<ChallengeUsersResponse> challengeUserList = createChallengeUserList(challenge);
        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge, challengeUserList);
        return Response.ok(challengeResponse);
    }

    @Override
    public Response<ChallengeResponse> joinChallenge(Long userId, ChallengeJoinRequest request) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findByChallengeId(request.getChallengeId()).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        if (isAlreadyJoin(userId, challenge.getChallengeId())) {
            throw new BusinessException(ResponseCode.CHALLENGE_ALREADY_JOIN);
        }

        if (!canJoinChallenge(user, challenge.getCategory().getCategoryName())) {
            throw new BusinessException(ResponseCode.CHALLENGE_CAT_ALREADY_PARTICIPATE);
        }

        // 챌린지 참여자 추가
        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challenge.addChallengeUser(challengeUser);

        // 채팅방 참여자 추가
        chatRoomUserService.addUser(challenge.getChatroom(), user);

        List<ChallengeUsersResponse> challengeUserList = createChallengeUserList(challenge);
        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge, challengeUserList);
        return Response.ok(challengeResponse);
    }

    @Override
    public Response<Void> leaveChallenge(Long userId, ChallengeLeaveRequest request) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findByChallengeId(request.getChallengeId()).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));
        ChallengeUser challengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(user.getId(), challenge.getChallengeId()).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NO_PERMISSION));

        // 채팅방 퇴장 (챌린지 삭제 전)
        chatRoomUserService.deleteUser(challenge.getChatroom().getId(), user.getNickname());

        challenge.removeChallengeUser(challengeUser);
        challengeUserRepository.delete(challengeUser);

        //참여자가 0명이 됐다면 챌린지 자체도 삭제한다.
        if (challenge.getChallengeUserList().size() == 0) {
            challengeRepository.delete(challenge);
        }

        return Response.ok();
    }

    @Override
    public Response<Void> deleteChallenge(Long userId, Long ChallengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(ChallengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        //챌린지 리더인지 확인
        boolean isChallengeLeader = challenge.getChallengeUserList().stream()
                .anyMatch(challengeUser -> challengeUser.getChallengeUserPK().getUserId().equals(userId)
                        && challengeUser.getRole().equals(ChallengeRole.LEADER));

        if (isChallengeLeader) {
            challengeRepository.delete(challenge);
            chatRoomService.deleteChatRoom(challenge.getChatroom().getId()); // soft-delete 때문에 채팅방을 챌린지 이후에 삭제

            return Response.ok();
        } else {
            throw new BusinessException(ResponseCode.CHALLENGE_UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public Response<ChallengeResponse> getChallenge(Long ChallengeId) {

        Challenge challenge = challengeRepository.findByChallengeId(ChallengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        List<ChallengeUsersResponse> challengeUserList = createChallengeUserList(challenge);
        ChallengeResponse challengeResponse = ChallengeResponse.of(challenge, challengeUserList);

        return Response.ok(challengeResponse);
    }

    @Override
    public Response<List<ChallengeAllResponse>> getAllChallenge() {

        List<Challenge> challenges = challengeRepository.findAll();

        List<ChallengeAllResponse> challengeResponse = challenges.stream()
                .map(challenge -> new ChallengeAllResponse(
                        challenge.getChallengeId(),
                        challenge.getChallengeTitle(),
                        challenge.getCategory().getCategoryName(),
                        challenge.getChallengeUserList().size(),
                        challenge.getImageUrl()
                )).collect(Collectors.toList());

        return Response.ok(challengeResponse);
    }

    @Override
    public Response<List<ChallengeAllResponse>> getUserChallenge(Long userId) {

        List<Challenge> challenges = challengeRepository.findAllByUserId(userId);

        List<ChallengeAllResponse> challengeResponse = challenges.stream()
                .map(challenge -> new ChallengeAllResponse(
                        challenge.getChallengeId(),
                        challenge.getChallengeTitle(),
                        challenge.getCategory().getCategoryName(),
                        challenge.getChallengeUserList().size(),
                        challenge.getImageUrl()
                )).collect(Collectors.toList());

        return Response.ok(challengeResponse);
    }

    @Override
    public Response<ChallengeResponse> updateChallenge(Long userId, Long challengeId, ChallengeRequest request) {

        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        //챌린지 리더인지 확인
        ChallengeUser challengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(userId, challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NO_PERMISSION));
        boolean isChallengeLeader = challengeUser.getRole().equals(ChallengeRole.LEADER);

        if (isChallengeLeader) {
            challenge.updateChallengeTitle(request.getChallengeTitle());
            challenge.updateChallengeContent(request.getChallengeContent());
            challenge.updateChallengeGoal(request.getChallengeGoal());
            challenge.updateSubmission(request.getSubmission());
            challenge.updateStartDate(request.getStartDate());
            challenge.updateEndDate(request.getEndDate());

            Challenge updatedChallenge = challengeRepository.save(challenge);

            List<ChallengeUsersResponse> challengeUserList = createChallengeUserList(updatedChallenge);
            ChallengeResponse challengeResponse = ChallengeResponse.of(challenge, challengeUserList);

            return Response.ok(challengeResponse);
        } else {
            throw new BusinessException(ResponseCode.CHALLENGE_UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public Response<List<ChallengeAllResponse>> getRandomChallenge() {

        List<Challenge> challenges;

        //존재하는 챌린지가 4개 미만이라면 findAll 해준다.
        if (challengeRepository.count() < 4) {
            challenges = challengeRepository.findAll();

        } else {
            challenges = challengeRepository.findRandomLimitFour();
        }

        List<ChallengeAllResponse> challengeResponse = challenges.stream().map(challenge -> new ChallengeAllResponse(challenge.getChallengeId(),
                challenge.getChallengeTitle(),
                challenge.getCategory().getCategoryName(),
                challenge.getChallengeUserList().size(),
                challenge.getImageUrl())).collect(Collectors.toList());

        return Response.ok(challengeResponse);
    }

    @Override
    public Response<Void> kickout(Long userId, Long challengeId, String nickname) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NOT_FOUND));

        //챌린지 리더인지 확인
        ChallengeUser challengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(userId, challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NO_PERMISSION));
        boolean isChallengeLeader = challengeUser.getRole().equals(ChallengeRole.LEADER);

        if (isChallengeLeader) {

            Users kickoutUser = userRepository.findByNickname(nickname).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            //자신을 강퇴할 시 에러 발생
            if (kickoutUser.getId().equals(userId)) {
                throw new BusinessException(ResponseCode.CHALLENGE_CANNOT_KICKOUT_SELF);
            }

            ChallengeUser kickoutChallengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(kickoutUser.getId(), challengeId).orElseThrow(() -> new BusinessException(ResponseCode.CHALLENGE_NO_PERMISSION));

            challenge.removeChallengeUser(kickoutChallengeUser);
            challengeUserRepository.delete(kickoutChallengeUser);

            // 채팅방에서도 강퇴
            chatRoomUserService.deleteUser(challenge.getChatroom().getId(), kickoutUser.getNickname());

            return Response.ok();
        } else {
            throw new BusinessException(ResponseCode.CHALLENGE_UNAUTHORIZED_ACCESS);
        }
    }

    public boolean isAlreadyJoin(Long userId, Long ChallengeId) {
        Optional<ChallengeUser> challengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(userId, ChallengeId);

        return challengeUser.isPresent();
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

    //ChallengeUser 정보를 담은 ChallengeUsersResponse 리스트 생성
    public List<ChallengeUsersResponse> createChallengeUserList(Challenge challenge) {
        return challenge.getChallengeUserList().stream().map(challengeUser -> {
            Users user = challengeUser.getUser();
                    return new ChallengeUsersResponse(
                    user.getId(),
                    user.getNickname(),
                    user.getImageUrl(),
                    challengeUser.getRole(),
                    isSuccess(challengeUser, challenge)

            );
        })
        .collect(Collectors.toList());
    }

    public boolean isSuccess(ChallengeUser challengeUser, Challenge challenge) {
        LocalDate today = LocalDate.now();
        List<Post> posts = postRepository.findByUserIdAndToday(challengeUser.getUser().getId(), today.atStartOfDay());

        boolean isSuccess = false;

        if (challenge.getCategory().getCategoryName().equals("기상")) {
            isSuccess = posts.stream()
                    .filter(post -> post.getCategory().getCategoryName().equals("기상"))
                    .anyMatch(post -> post.getActivityTime() <= challenge.getChallengeGoal());
        } else {
            int totalActivityTime = posts.stream()
                    .filter(post -> post.getCategory().getCategoryName().equals(challenge.getCategory().getCategoryName()))
                    .mapToInt(Post::getActivityTime).sum();

            isSuccess = totalActivityTime >= challenge.getChallengeGoal();
        }

        return isSuccess;
    }
}
