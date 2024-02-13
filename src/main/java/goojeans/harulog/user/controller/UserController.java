package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.DeleteUserRequest;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.dto.request.UpdatePasswordRequest;
import goojeans.harulog.user.domain.dto.request.UpdateUserInfoRequest;
import goojeans.harulog.user.domain.dto.response.MyPageInfoResponse;
import goojeans.harulog.user.domain.dto.response.UserInfoEditResponse;
import goojeans.harulog.user.service.UserService;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @PostMapping("/sign-up")
    ResponseEntity<Response<Void>> signUp(@Validated @RequestBody SignUpRequest request) {

        Response<Void> response = userService.signUp(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit")
    ResponseEntity<Response<UserInfoEditResponse>> getUserInfoForEdit() {
        return ResponseEntity.ok(userService.getUserInfoForEdit());
    }

    @PutMapping("/edit/update")
    ResponseEntity<Response<Void>> updateUserInfo(@Validated @RequestBody UpdateUserInfoRequest request, HttpServletResponse response) {
        String accessToken = userService.updateUserInfo(request);

        response.setHeader("Authorization", accessToken);

        return ResponseEntity.ok(Response.ok());
    }

    @PutMapping("/edit/password")
    ResponseEntity<Response<Void>> updatePassword(@Validated @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PostMapping("/edit/delete")
    ResponseEntity<Response<Void>> deleteUser(@Validated @RequestBody DeleteUserRequest request) {
        return ResponseEntity.ok(userService.delete(request));
    }

    @GetMapping("/profile")
    ResponseEntity<Response<MyPageInfoResponse>> getMyPage() {

        String nickname = securityUtils.getCurrentUserInfo().getNickname();

        return ResponseEntity.ok(userService.getMyPageUserInfo(nickname));
    }

    @GetMapping("/profile/{nickname}")
    ResponseEntity<Response<MyPageInfoResponse>> getUserMyPageInfo(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.getMyPageUserInfo(nickname));
    }

    @PutMapping("/logout")
    ResponseEntity<Response<Void>> logout() {
        return ResponseEntity.ok(userService.logout());
    }

}
