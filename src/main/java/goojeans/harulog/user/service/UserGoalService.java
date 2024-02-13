package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.UpdateUserGoalsRequest;
import goojeans.harulog.user.domain.dto.response.UserGoalResponse;
import goojeans.harulog.user.domain.entity.UserGoal;

import java.util.List;

public interface UserGoalService {

    public Response<List<UserGoalResponse>> findUserGoalsByUserId(Long id);

    public Response<List<UserGoalResponse>> updateUserGoal(UpdateUserGoalsRequest request);

}
