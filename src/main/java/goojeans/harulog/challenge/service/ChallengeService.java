package goojeans.harulog.challenge.service;

import goojeans.harulog.challenge.domain.dto.request.ChallengeJoinRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeLeaveRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeAllResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface ChallengeService {

    //새 챌린지 생성 후 첫 참여
    Response<ChallengeResponse> registerChallenge(Long userId, ChallengeRequest request);

    //기존 챌린지에 참여
    Response<ChallengeResponse> joinChallenge(Long userId, ChallengeJoinRequest request);

    //챌린지 나가기
    Response<Void> leaveChallenge(Long userId, ChallengeLeaveRequest request);

    //챌린지 삭제하기
    Response<Void> deleteChallenge(Long userId, Long ChallengeId);

    //챌린지 단건 조회
    Response<ChallengeResponse> getChallenge(Long userId, Long ChallengeId);

    //모든 챌린지 조회
    Response<List<ChallengeAllResponse>> getAllChallenge();

    //한 사용자가 참여하는 모든 챌린지 조회
    Response<List<ChallengeAllResponse>> getUserChallenge(Long userId);

    //챌린지 수정
    Response<ChallengeResponse> updateChallenge(Long userId, Long challengeId, ChallengeRequest request);

    //랜덤으로 4개의 챌린지 조회
    Response<List<ChallengeAllResponse>> getRandomChallenge();

    //챌린지 강퇴하기
    Response<Void> kickout(Long userId, Long challengeId, String nickname);
}
