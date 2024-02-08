package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    ResponseEntity<Response<Void>> signUp(@RequestBody SignUpRequest request) {
        Response<Void> response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }

}
