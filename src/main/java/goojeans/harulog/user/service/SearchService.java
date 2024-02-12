package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.response.SearchResponse;

public interface SearchService {

    Response<SearchResponse> searchOnAll(String content, Integer pageNumber);

    Response<SearchResponse> searchOnFollowers(String content, Integer pageNumber);

    Response<SearchResponse> searchOnFollowings(String content, Integer pageNumber);

}
