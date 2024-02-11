package goojeans.harulog.user.service;


import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.DeleteUserRequest;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.dto.request.UpdatePasswordRequest;
import goojeans.harulog.user.domain.dto.request.UpdateUserInfoRequest;
import goojeans.harulog.user.domain.dto.response.MyPageInfoResponse;
import goojeans.harulog.user.domain.dto.response.UserInfoEditResponse;

public interface UserService {

    Response<Void> signUp(SignUpRequest request);

    Response<UserInfoEditResponse> getUserInfoForEdit();

    String updateUserInfo(UpdateUserInfoRequest request);

    Response<Void> updatePassword(UpdatePasswordRequest request);

    Response<Void> delete(DeleteUserRequest request);

    Response<MyPageInfoResponse> getMyPageUserInfo(String nickname);

}
