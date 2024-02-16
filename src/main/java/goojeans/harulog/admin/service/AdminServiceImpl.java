package goojeans.harulog.admin.service;

import goojeans.harulog.admin.dto.response.AdminPostResponse;
import goojeans.harulog.admin.dto.response.AdminUserResponse;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.post.repository.PostRepository;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import goojeans.harulog.user.util.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final SecurityUtils securityUtils;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private Integer pageSize = 10;

    @Override
    public Response<AdminPostResponse> findAllPost(Integer pageNumber) {

        authorityCheck();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Post> page = postRepository.findAllWithCategoryAndUser(pageRequest);

        return Response.ok(AdminPostResponse.from(page));
    }



    @Override
    public Response<AdminUserResponse> findAllUsers(Integer pageNumber, String nickname) {

        authorityCheck();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Users> page = userRepository.findByNicknameStartingWith(nickname, pageRequest);

        return Response.ok(AdminUserResponse.from(page));
    }

    @Override
    public Response<Void> deletePost(Long id) {

        authorityCheck();

        postRepository.deleteById(id);

        return Response.ok();
    }

    @Override
    public Response<Void> deleteUser(Long id) {

        authorityCheck();

        if (id.equals(securityUtils.getCurrentUserInfo().getId())){
            throw new BusinessException(ResponseCode.ADMIN_CANNOT_DELETE);
        }

        userRepository.deleteById(id);

        return Response.ok();
    }


    private void authorityCheck() {
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();
        if (!currentUserInfo.getRoles().equals(UserRole.ADMIN)){
            throw new BusinessException(ResponseCode.ONLY_ADMIN_CAN_ACCESS);
        }
    }
}
