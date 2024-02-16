package goojeans.harulog.admin.service;

import goojeans.harulog.admin.dto.response.AdminPostResponse;
import goojeans.harulog.admin.dto.response.AdminUserResponse;
import goojeans.harulog.domain.dto.Response;


public interface AdminService {

    Response<AdminPostResponse> findAllPost(Integer pageNumber);

    Response<AdminUserResponse> findAllUsers(Integer pageNumber, String nickname);

    Response<Void> deletePost(Long id);

    Response<Void> deleteUser(Long id);

}
