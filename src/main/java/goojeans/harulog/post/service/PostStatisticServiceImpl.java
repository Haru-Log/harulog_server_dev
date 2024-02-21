package goojeans.harulog.post.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.dto.response.DailyPostDto;
import goojeans.harulog.post.domain.dto.response.YearlyPostDto;
import goojeans.harulog.post.repository.PostStatisticRepositoryImpl;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostStatisticServiceImpl implements PostStatisticService {

    private final PostStatisticRepositoryImpl postStatisticRepository;
    private final UserRepository userRepository;

    @Override
    public Response<List<YearlyPostDto>> getYearlyStatistics(Long userId) {
        List<YearlyPostDto> yearlyPostList = postStatisticRepository.findYearlyPost(userId);

        return Response.ok(yearlyPostList);
    }

    @Override
    public Response<List<YearlyPostDto>> getOthersYearlyStatistics(String nickname) {
        Users user = userRepository.findByNickname(nickname).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        List<YearlyPostDto> yearlyPostList = postStatisticRepository.findYearlyPost(user.getId());

        return Response.ok(yearlyPostList);
    }

    @Override
    public Response<List<DailyPostDto>> getDailyStatistics(Long userId) {
        List<DailyPostDto> dailyPostList = postStatisticRepository.findDailyPost(userId);

        return Response.ok(dailyPostList);
    }
}
