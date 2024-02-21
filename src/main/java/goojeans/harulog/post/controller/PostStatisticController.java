package goojeans.harulog.post.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.dto.response.DailyPostDto;
import goojeans.harulog.post.domain.dto.response.YearlyPostDto;
import goojeans.harulog.post.service.PostStatisticService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostStatisticController {

    private final SecurityUtils securityUtils;
    private final PostStatisticService postStatisticService;

    @GetMapping("/grow/yearly")
    public ResponseEntity<Response<List<YearlyPostDto>>> getYearlyStatistics() {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<List<YearlyPostDto>> response = postStatisticService.getYearlyStatistics(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/grow/yearly/{nickname}")
    public ResponseEntity<Response<List<YearlyPostDto>>> getOthersYearlyStatistics(@PathVariable("nickname") String nickname) {
        Response<List<YearlyPostDto>> response = postStatisticService.getOthersYearlyStatistics(nickname);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/grow/daily")
    public ResponseEntity<Response<List<DailyPostDto>>> getDailyStatistics() {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        Response<List<DailyPostDto>> response = postStatisticService.getDailyStatistics(userId);

        return ResponseEntity.ok(response);
    }
}