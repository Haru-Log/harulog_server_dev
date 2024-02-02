package goojeans.harulog.challenge.controller;

import goojeans.harulog.challenge.domain.dto.request.ChallengeJoinRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeLeaveRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRegisterRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeAllResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.service.ChallengeService;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final SecurityUtils securityUtils;

    @PostMapping("/challenge/register")
    public ResponseEntity<Response<ChallengeResponse>> registerChallenge(@RequestBody ChallengeRegisterRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<ChallengeResponse> response = challengeService.registerChallenge(userId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/challenge/join")
    public ResponseEntity<Response<ChallengeResponse>> joinChallenge(@RequestBody ChallengeJoinRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<ChallengeResponse> response = challengeService.joinChallenge(userId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/challenge/leave")
    public ResponseEntity<Response<Void>> leaveChallenge(@RequestBody ChallengeLeaveRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<Void> response = challengeService.leaveChallenge(userId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<Response<Void>> deleteChallenge(@PathVariable("challengeId") Long challengeId) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<Void> response = challengeService.deleteChallenge(userId, challengeId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<Response<ChallengeResponse>> getChallenge(@PathVariable("challengeId") Long challengeId) {
        Response<ChallengeResponse> response = challengeService.getChallenge(challengeId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/challenge")
    public ResponseEntity<Response<List<ChallengeAllResponse>>> getAllChallenge() {
        Response<List<ChallengeAllResponse>> response = challengeService.getAllChallenge();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
