package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.request.UpdateUserGoalsRequest;
import goojeans.harulog.user.domain.dto.response.UserGoalResponse;
import goojeans.harulog.user.service.UserGoalService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;
    private final SecurityUtils contextUtils;

    @GetMapping("/user-goal")
    public ResponseEntity<Response<List<UserGoalResponse>>> getUserGoals() {
        JwtUserDetail currentUserInfo = contextUtils.getCurrentUserInfo();

        Response<List<UserGoalResponse>> response = userGoalService.findUserGoalsByUserId(currentUserInfo.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/user-goal/update")
    public ResponseEntity<Response<Void>> updateUserGoal(@Validated @RequestBody UpdateUserGoalsRequest request) {

        JwtUserDetail currentUserInfo = contextUtils.getCurrentUserInfo();
        request.setUserId(currentUserInfo.getId());

        Response<Void> response = userGoalService.updateUserGoal(request);

        return ResponseEntity.ok(response);
    }
}
