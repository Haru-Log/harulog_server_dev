package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.response.SearchResponse;
import goojeans.harulog.user.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/all")
    ResponseEntity<Response<SearchResponse>> searchOnAll(@RequestParam String content,
                                                         @RequestParam(defaultValue = "0") Integer pageNumber) {

        return ResponseEntity.ok(searchService.searchOnAll(content, pageNumber));
    }

    @GetMapping("/followers")
    ResponseEntity<Response<SearchResponse>> searchOnFollowers(@RequestParam String content,
                                                         @RequestParam(defaultValue = "0") Integer pageNumber) {

        return ResponseEntity.ok(searchService.searchOnFollowers(content, pageNumber));
    }

    @GetMapping("/followings")
    ResponseEntity<Response<SearchResponse>> searchOnFollowings(@RequestParam String content,
                                                         @RequestParam(defaultValue = "0") Integer pageNumber) {

        return ResponseEntity.ok(searchService.searchOnFollowings(content, pageNumber));
    }

}
