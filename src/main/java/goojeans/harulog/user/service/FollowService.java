package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.FollowRequest;
import goojeans.harulog.user.domain.dto.response.FollowInfo;

import java.util.List;

public interface FollowService {

    Response<List<FollowInfo>> getFollowerList(String nickname);

    Response<List<FollowInfo>> getMyFollowerList();

    Response<List<FollowInfo>> getFollowingList(String nickname);

    Response<List<FollowInfo>> getMyFollowingList();

    Response<Void> follow(FollowRequest request);

    Response<Void> followingDelete(String nickname);

    Response<Void> followerDelete(String nickname);

}
