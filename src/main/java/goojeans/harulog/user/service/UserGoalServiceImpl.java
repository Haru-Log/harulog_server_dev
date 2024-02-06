package goojeans.harulog.user.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.UpdateUserGoalsRequest;
import goojeans.harulog.user.domain.dto.response.UserGoalResponse;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.repository.UserGoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserGoalServiceImpl implements UserGoalService {

    private final UserGoalRepository userGoalRepository;

    @Override
    public Response<List<UserGoalResponse>> findUserGoalsByUserId(Long id) {

        List<UserGoal> findUserGoals = userGoalRepository.findUserGoalsByUserId(id);

        if (findUserGoals.isEmpty()) throw new BusinessException(ResponseCode.USER_GOAL_NOT_FOUND);

        List<UserGoalResponse> responseList = findUserGoals.stream()
                .map(userGoal ->
                        UserGoalResponse.of(userGoal.getCategory().getCategoryName(), userGoal.getGoal()))
                .toList();

        return Response.ok(responseList);
    }

    @Override
    public Response<Void> updateUserGoal(UpdateUserGoalsRequest request) {

        List<UserGoal> findUserGoals = userGoalRepository.findUserGoalsByUserId(request.getUserId());

        findUserGoals.forEach(userGoal ->
                request.getUpdateGoalsList().stream()
                        .filter(updateGoalsDto ->
                                updateGoalsDto.getCategoryName().equals(userGoal.getCategory().getCategoryName()))
                        .findAny()
                        .ifPresent(updateGoalsDto -> userGoal.updateGoal(updateGoalsDto.getGoal()))
                );

        return Response.ok();
    }
}
