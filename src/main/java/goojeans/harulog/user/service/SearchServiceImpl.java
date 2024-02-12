package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.response.FollowInfo;
import goojeans.harulog.user.domain.dto.response.PageInfo;
import goojeans.harulog.user.domain.dto.response.SearchResponse;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final Integer pageSize = 10;

    @Override
    public Response<SearchResponse> searchOnAll(String content, Integer pageNumber) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Users> result = userRepository.findByNicknameStartingWith(content, pageRequest);

        return Response.ok(pageToSearchResponse(result));
    }



    @Override
    public Response<SearchResponse> searchOnFollowers(String content, Integer pageNumber) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Page<Users> result = userRepository.findUserOnFollowers(currentUserInfo.getId(), content, pageRequest);

        return Response.ok(pageToSearchResponse(result));
    }

    @Override
    public Response<SearchResponse> searchOnFollowings(String content, Integer pageNumber) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Page<Users> result = userRepository.findUserOnFollowings(currentUserInfo.getId(), content, pageRequest);

        return Response.ok(pageToSearchResponse(result));
    }

    private SearchResponse pageToSearchResponse(Page<Users> result) {
        PageInfo pageInfo = PageInfo.builder()
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .number(result.getNumber())
                .build();

        List<FollowInfo> contents = result.getContent().stream()
                .map(FollowInfo::entityToResponse)
                .toList();
        return SearchResponse.of(pageInfo, contents);
    }
}
