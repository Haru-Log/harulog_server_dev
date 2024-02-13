package goojeans.harulog.post.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.dto.response.DailyPostDto;
import goojeans.harulog.post.domain.dto.response.YearlyPostDto;

import java.util.List;

public interface PostStatisticService {

    Response<List<YearlyPostDto>> getYearlyStatistics(Long userId);

    Response<List<DailyPostDto>> getDailyStatistics(Long userId);
}
