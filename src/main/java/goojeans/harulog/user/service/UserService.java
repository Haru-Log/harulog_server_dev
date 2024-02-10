package goojeans.harulog.user.service;


import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.DeleteUserRequest;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.dto.request.UpdatePasswordRequest;
import goojeans.harulog.user.domain.dto.request.UpdateUserInfoRequest;
import goojeans.harulog.user.domain.dto.response.UserInfoEditResponse;

public interface UserService {

    public Response<Void> signUp(SignUpRequest request);

    public Response<UserInfoEditResponse> getUserInfoForEdit();

    public String updateUserInfo(UpdateUserInfoRequest request);

    public Response<Void> updatePassword(UpdatePasswordRequest request);

    public Response<Void> delete(DeleteUserRequest request);

}
