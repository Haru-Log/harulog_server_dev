package goojeans.harulog.user.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Response<Void> signUp(SignUpRequest request) {

        boolean nicknameDuplication = checkNicknameDuplication(request.getNickname());
        boolean emailDuplication = checkEmailDuplication(request.getEmail());

        if (nicknameDuplication && emailDuplication) {
            throw new BusinessException(ResponseCode.USER_NICKNAME_EMAIL_DUPLICATION);
        } else if (nicknameDuplication) {
            throw new BusinessException(ResponseCode.USER_NICKNAME_DUPLICATION);
        } else if (emailDuplication) {
            throw new BusinessException(ResponseCode.USER_EMAIL_DUPLICATION);
        }

        Users entity = request.toEntity();

        userRepository.save(entity);

        return Response.ok();
    }

    private boolean checkNicknameDuplication(String nickname) {
        return userRepository.findUsersByNickname(nickname).isPresent();
    }

    private boolean checkEmailDuplication(String email) {
        return userRepository.findUsersByEmail(email).isPresent();
    }
}
