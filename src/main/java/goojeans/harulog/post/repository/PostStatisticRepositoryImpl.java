package goojeans.harulog.post.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goojeans.harulog.category.domain.entity.QCategory;
import goojeans.harulog.post.domain.dto.response.DailyPostDto;
import goojeans.harulog.post.domain.dto.response.YearlyPostDto;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.post.domain.entity.QPost;
import goojeans.harulog.user.domain.entity.QUserGoal;
import goojeans.harulog.user.domain.entity.UserGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostStatisticRepositoryImpl implements PostStatisticRepository {

    private final JPAQueryFactory queryFactory;

    public List<YearlyPostDto> findYearlyPost(Long userId) {

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        QPost post = QPost.post;
        QCategory category = QCategory.category;

        return queryFactory.select(Projections.constructor(YearlyPostDto.class,
                        post.createdAt.as("date"),
                        category.categoryName.as("categoryName"),
                        post.activityTime
                ))
                .from(post)
                .join(post.category, category)
                .where(post.createdAt.goe(oneYearAgo.atStartOfDay())
                        .and(post.user.id.eq(userId)))
                .fetch();
    }

    @Override
    public List<DailyPostDto> findDailyPost(Long userId) {

        QPost Qpost = QPost.post;
        QUserGoal QuserGoal = QUserGoal.userGoal;

        //userGoal 불러오기
        Map<String, UserGoal> userGoalMap = queryFactory
                .select(QuserGoal)
                .from(QuserGoal)
                .where(QuserGoal.user.id.eq(userId))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        userGoal -> userGoal.getCategory().getCategoryName(),
                        userGoal -> userGoal
                ));

        //오늘 올린 포스트
        List<Post> todayPosts = queryFactory.select(Qpost)
                .from(Qpost)
                .where(Qpost.user.id.eq(userId)
                        .and(Qpost.createdAt.goe(LocalDate.now().atStartOfDay())))
                .fetch();

        List<DailyPostDto> dailyPostDtoList = new ArrayList<>();

        //기상 카테고리인 경우, 목표를 새로 업데이트 한 날짜부터 오늘까지 성공일 수 계산
        long wakeUpSuccessDays = 0;
        UserGoal wakeupGoal = userGoalMap.get("기상");


        List<Post> wakeupPosts = queryFactory.select(Qpost)
                .from(Qpost)
                .where(Qpost.user.id.eq(userId)
                        .and(Qpost.category.categoryName.eq("기상"))
                        .and(Qpost.createdAt.goe(wakeupGoal.getUpdatedAt().toLocalDate().atStartOfDay())))
                .fetch();

            wakeUpSuccessDays = wakeupPosts.stream()
                    .filter(post -> post.getActivityTime() <= wakeupGoal.getGoal())
                    .count();

            dailyPostDtoList.add(new DailyPostDto(
                    "기상",
                    ChronoUnit.DAYS.between(wakeupGoal.getUpdatedAt().toLocalDate(), LocalDate.now().plusDays(1)),
                    wakeupGoal.getUpdatedAt(),
                    wakeUpSuccessDays
            ));

        //기상 제외 다른 카테고리 처리
        userGoalMap.forEach((categoryName, userGoal) -> {
            if (!categoryName.equals("기상")) {
                dailyPostDtoList.add(new DailyPostDto(
                        categoryName,
                        (long) userGoal.getGoal(),
                        userGoal.getUpdatedAt(),
                        0L // 초기 달성치는 0으로 설정
                ));
            }
        });

        //해당 카테고리 이름을 가진 DailyPostDto 객체를 찾아 달성치를 업데이트
        todayPosts.stream()
                .filter(post -> !post.getCategory().getCategoryName().equals("기상"))
                .collect(Collectors.groupingBy(post -> post.getCategory().getCategoryName()))
                .forEach((categoryName, posts) -> {
                    Long achievement = (long) posts.stream().mapToInt(Post::getActivityTime).sum();

                    dailyPostDtoList.stream()
                            .filter(dailyPostDto -> dailyPostDto.getCategoryName().equals(categoryName))
                            .findFirst()
                            .ifPresent(dailyPostDto -> dailyPostDto.updateAchievement(achievement));
                });

        return dailyPostDtoList;
    }
}
