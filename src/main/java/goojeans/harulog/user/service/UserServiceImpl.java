package goojeans.harulog.user.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.dto.request.UpdatePasswordRequest;
import goojeans.harulog.user.domain.dto.request.UpdateUserInfoRequest;
import goojeans.harulog.user.domain.dto.response.UserInfoEditResponse;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

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

    @Override
    public Response<UserInfoEditResponse> getUserInfoForEdit() {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        LocalDateTime createdAtLocalTime = currentUserInfo.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String createdAtFormat = createdAtLocalTime.format(formatter);

        UserInfoEditResponse response = UserInfoEditResponse.builder()
                .email(currentUserInfo.getEmail())
                .userName(currentUserInfo.getUsername())
                .createdAt(createdAtFormat)
                .introduction(currentUserInfo.getIntroduction())
                .nickname(currentUserInfo.getNickname())
                .contactNumber(currentUserInfo.getContactNumber())
                .imageUrl(currentUserInfo.getImageUrl())
                .build();

        return Response.ok(response);
    }

    @Override
    public Response<Void> updateUserInfo(UpdateUserInfoRequest request) {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Users users = userRepository.findUsersById(currentUserInfo.getId()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        users.updateNickname(request.getNickname());
        users.updateIntroduction(request.getIntroduction());
        users.updateContactNumber(request.getIntroduction());

        return Response.ok();
    }

    @Override
    public Response<Void> updatePassword(UpdatePasswordRequest request) {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        if (!request.getBeforePassword().equals(currentUserInfo.getPassword())) {
            throw new BusinessException(ResponseCode.USER_PASSWORD_NOT_MATCH);
        }

        Users user = userRepository.findUsersById(currentUserInfo.getId()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        user.updatePassword(request.getAfterPassword());

        return Response.ok();
    }

    // confirm string is email
    @Override
    public Response<Void> delete(String confirmString) {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        if (!confirmString.equals(currentUserInfo.getEmail())) {
            throw new BusinessException(ResponseCode.USER_DELETE_STRING_NOT_MATCH);
        }

        Users user = userRepository.findUsersById(currentUserInfo.getId()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        userRepository.delete(user);

        return Response.ok();
    }

    private boolean checkNicknameDuplication(String nickname) {
        return userRepository.findUsersByNickname(nickname).isPresent();
    }

    private boolean checkEmailDuplication(String email) {
        return userRepository.findUsersByEmail(email).isPresent();
    }
}
