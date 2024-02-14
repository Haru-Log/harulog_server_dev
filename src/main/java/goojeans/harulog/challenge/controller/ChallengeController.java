package goojeans.harulog.challenge.controller;

import goojeans.harulog.challenge.domain.dto.request.ChallengeJoinRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeLeaveRequest;
import goojeans.harulog.challenge.domain.dto.request.ChallengeRequest;
import goojeans.harulog.challenge.domain.dto.response.ChallengeAllResponse;
import goojeans.harulog.challenge.domain.dto.response.ChallengeResponse;
import goojeans.harulog.challenge.service.ChallengeService;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final SecurityUtils securityUtils;

    @PostMapping("/challenge/register")
    public ResponseEntity<Response<ChallengeResponse>> registerChallenge(@Validated @RequestBody ChallengeRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<ChallengeResponse> response = challengeService.registerChallenge(userId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/challenge/join")
    public ResponseEntity<Response<ChallengeResponse>> joinChallenge(@Validated @RequestBody ChallengeJoinRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<ChallengeResponse> response = challengeService.joinChallenge(userId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/challenge/leave")
    public ResponseEntity<Response<Void>> leaveChallenge(@Validated @RequestBody ChallengeLeaveRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<Void> response = challengeService.leaveChallenge(userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<Response<Void>> deleteChallenge(@PathVariable("challengeId") Long challengeId) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<Void> response = challengeService.deleteChallenge(userId, challengeId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<Response<ChallengeResponse>> getChallenge(@PathVariable("challengeId") Long challengeId) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<ChallengeResponse> response = challengeService.getChallenge(userId, challengeId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/challenge")
    public ResponseEntity<Response<List<ChallengeAllResponse>>> getAllChallenge() {
        Response<List<ChallengeAllResponse>> response = challengeService.getAllChallenge();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/challenge/{challengeId}")
    public ResponseEntity<Response<ChallengeResponse>> updateChallenge(@PathVariable("challengeId") Long challengeId,@Validated @RequestBody ChallengeRequest request) {
        Long userId = securityUtils.getCurrentUserInfo().getId();

        Response<ChallengeResponse> response = challengeService.updateChallenge(userId, challengeId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/challenge/kickout/{challengeId}/{nickname}")
    public ResponseEntity<Response<Void>> kickoutChallengeUser(@PathVariable("challengeId") Long challengeId, @PathVariable("nickname") String nickname) {
        Long userId = securityUtils.getCurrentUserInfo().getId();

        Response<Void> response = challengeService.kickout(userId, challengeId, nickname);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/main/challenge")
    public ResponseEntity<Response<List<ChallengeAllResponse>>> getRandomChallenge() {
        Response<List<ChallengeAllResponse>> response = challengeService.getRandomChallenge();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/challenge")
    public ResponseEntity<Response<List<ChallengeAllResponse>>> getUserChallenge() {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<List<ChallengeAllResponse>> response = challengeService.getUserChallenge(userId);

        return ResponseEntity.ok(response);
    }
}
