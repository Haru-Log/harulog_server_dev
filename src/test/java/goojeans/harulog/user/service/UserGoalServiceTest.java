package goojeans.harulog.user.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.UpdateGoalsDto;
import goojeans.harulog.user.domain.dto.request.UpdateUserGoalsRequest;
import goojeans.harulog.user.domain.dto.response.UserGoalResponse;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserGoalRepository;
import goojeans.harulog.user.util.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserGoalServiceTest {

    @Spy
    @InjectMocks
    UserGoalServiceImpl service;
    @Mock
    UserGoalRepository userGoalRepository;

    private final String testString = "testString";
    private final String categoryName1 = "category1";
    private final String categoryName2 = "category2";
    private final Long testId1 = 1L;
    private final Long testId2 = 2L;
    private final Integer testGoal = 60;
    private final Users testUser = Users.builder()
            .id(testId1)
            .email(testString)
            .nickname(testString)
            .socialType(SocialType.HARU)
            .build();

    private final Category testCategory1 = Category.builder()
            .categoryId(testId1)
            .categoryName(categoryName1)
            .build();

    private final Category testCategory2 = Category.builder()
            .categoryId(testId2)
            .categoryName(categoryName2)
            .build();

    private UserGoal testUserGoal1;
    private UserGoal testUserGoal2;

    @BeforeEach
    void beforeEach() {
        testUserGoal1 = UserGoal.builder()
                .goal(testGoal)
                .user(testUser)
                .category(testCategory1)
                .build();
        testUserGoal2 = UserGoal.builder()
                .goal(testGoal)
                .user(testUser)
                .category(testCategory2)
                .build();
    }

    @Test
    void findUserGoalsWithId() {
        // Given
        // doReturn() 은 실제 메서드를 호출하지 않음
        // when() 은 실제 메서드를 호출함
        doReturn(List.of(testUserGoal1, testUserGoal2)).when(userGoalRepository).findUserGoalsByUserId(testId1);

        // When
        Response<List<UserGoalResponse>> findGoals = service.findUserGoalsByUserId(testId1);

        // Then
        assertThat(findGoals.getData()).hasSize(2);
        verify(userGoalRepository, times(1)).findUserGoalsByUserId(testId1);

    }

    @Test
    void updateUserGoals() {

        // Given
        Integer newGoal = 80;

        UserGoal newUserGoal = UserGoal.builder()
                .goal(newGoal)
                .user(testUser)
                .category(testCategory1)
                .build();

        doReturn(List.of(testUserGoal1, testUserGoal2)).when(userGoalRepository).findUserGoalsByUserId(testId1);

        UpdateUserGoalsRequest updateDto = UpdateUserGoalsRequest.builder()
                .updateGoalsList(List.of(UpdateGoalsDto.of(testCategory1.getCategoryName(), newGoal)))
                .userId(testId1)
                .build();

        // When
        Response<List<UserGoalResponse>> response = service.updateUserGoal(updateDto);

        // Then
        assertThat(testUserGoal1.getGoal()).isEqualTo(newUserGoal.getGoal());
        assertThat(testUserGoal2.getGoal()).isEqualTo(testGoal);

        assertThat(response.getData()).hasSize(2);
        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());

        verify(userGoalRepository, times(1)).findUserGoalsByUserId(testId1);

    }


}
