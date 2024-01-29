package goojeans.harulog.challenge.service;

import goojeans.harulog.challenge.domain.dto.request.ChallengeRegisterRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface ChallengeService {

    //새 챌린지 생성 후 첫 참여
    Response<ChallengeResponse> registerChallenge(Long userId, ChallengeRegisterRequest request);

    //기존 챌린지에 참여
    Response<ChallengeResponse> joinChallenge(Long userId, Long challengeId);

    //챌린지 나가기
    Response<Void> leaveChallenge(Long userId, Long challengeId);

    //챌린지 단건 조회
    Response<ChallengeResponse> getChallenge(Long ChallengeId);

    //모든 챌린지 조회
    Response<List<ChallengeResponse>> getAllChallenge();

    //한 사용자가 참여하는 모든 챌린지 조회
    Response<List<ChallengeResponse>> getUserChallenge(Long userId);
}
