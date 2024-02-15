package goojeans.harulog.user.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.request.FollowRequest;
import goojeans.harulog.user.domain.dto.response.FollowInfo;
import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.FollowId;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.FollowRepository;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final SecurityUtils securityUtils;

    private final static Integer PAGE_SIZE = 10;

    @Override
    public Response<List<FollowInfo>> getFollowerList(String nickname) {

        Users user = userRepository.findByNickname(nickname).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        List<FollowInfo> list = user.getFollowers().stream()
                .map(follow -> FollowInfo.entityToResponse(follow.getFollowing()))
                .toList();

        return Response.ok(list);

    }

    @Override
    public Response<List<FollowInfo>> getMyFollowerList(Integer pageNumber) {

        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);

        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        List<FollowInfo> list = followRepository.findFollowerByUserId(currentUserInfo.getId(), pageRequest).stream()
                .map(follow -> FollowInfo.entityToResponse(follow.getFollower()))
                .toList();

        return Response.ok(list);

    }

    @Override
    public Response<List<FollowInfo>> getFollowingList(String nickname) {

        Users user = userRepository.findByNickname(nickname).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        List<FollowInfo> list = user.getFollowings().stream()
                .map(follow -> FollowInfo.entityToResponse(follow.getFollower()))
                .toList();

        return Response.ok(list);

    }

    @Override
    public Response<List<FollowInfo>> getMyFollowingList(Integer pageNumber) {

        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);

        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        List<FollowInfo> list = followRepository.findFollowingByUserId(currentUserInfo.getId(), pageRequest).stream()
                .map(follow -> FollowInfo.entityToResponse(follow.getFollowing()))
                .toList();

        return Response.ok(list);

    }

    // 성능 개선 필요 -> join 이 2번 있는 쿼리가 2번 나감 : 총 4번의 join
    @Override
    public Response<Void> follow(FollowRequest request) {

        String nickname = securityUtils.getCurrentUserInfo().getNickname();

        Users follower = userRepository.findByNickname(nickname).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        Users following = userRepository.findByNickname(request.getTo()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        follower.addFollowing(follow);
        following.addFollower(follow);

        return Response.ok();

    }

    @Override
    public Response<Void> followingDelete(String nickname) {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Users following = userRepository.findUsersByNickname(nickname).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        followRepository.deleteFollow(new FollowId(currentUserInfo.getId(), following.getId()));

        return Response.ok();

    }

    @Override
    public Response<Void> followerDelete(String nickname) {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Users follower = userRepository.findUsersByNickname(nickname).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        followRepository.deleteFollow(new FollowId(follower.getId(), currentUserInfo.getId()));

        return Response.ok();

    }
}
