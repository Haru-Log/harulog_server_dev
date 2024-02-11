package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.FollowRequest;
import goojeans.harulog.user.domain.dto.response.FollowInfo;
import goojeans.harulog.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/followers/{nickname}")
    ResponseEntity<Response<List<FollowInfo>>> getUserFollowers(@PathVariable String nickname) {

        return ResponseEntity.ok(followService.getFollowerList(nickname));
    }

    @GetMapping("/followers")
    ResponseEntity<Response<List<FollowInfo>>> getCurrentUserFollower() {

        return ResponseEntity.ok(followService.getMyFollowerList());
    }

    @GetMapping("/followings/{nickname}")
    ResponseEntity<Response<List<FollowInfo>>> getUserFollowings(@PathVariable String nickname) {

        return ResponseEntity.ok(followService.getFollowingList(nickname));
    }

    @GetMapping("/followings")
    ResponseEntity<Response<List<FollowInfo>>> getCurrentUserFollowings() {

        return ResponseEntity.ok(followService.getMyFollowingList());
    }

    @PostMapping("/follow")
    ResponseEntity<Response<Void>> follow(@Validated @RequestBody FollowRequest request) {

        return ResponseEntity.ok(followService.follow(request));
    }

    @DeleteMapping("/following/{nickname}")
    ResponseEntity<Response<Void>> followCancel(@PathVariable String nickname) {

        return ResponseEntity.ok(followService.followingDelete(nickname));
    }

    @DeleteMapping("/follower/{nickname}")
    ResponseEntity<Response<Void>> followerDelete(@PathVariable String nickname) {

        return ResponseEntity.ok(followService.followerDelete(nickname));
    }


}
