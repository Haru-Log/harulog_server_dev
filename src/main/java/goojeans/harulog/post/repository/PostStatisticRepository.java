package goojeans.harulog.post.repository;

import goojeans.harulog.post.domain.dto.response.DailyPostDto;
import goojeans.harulog.post.domain.dto.response.YearlyPostDto;

import java.util.List;

public interface PostStatisticRepository {

    //사용자의 1년 포스트 통계(잔디밭)
    public List<YearlyPostDto> findYearlyPost(Long userId);

    //사용자의 하루 통계
    public List<DailyPostDto> findDailyPost(Long userId);
}
