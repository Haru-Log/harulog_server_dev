package goojeans.harulog.user.service;


import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;

public interface UserService {

    public Response<Void> signUp(SignUpRequest request);

}
