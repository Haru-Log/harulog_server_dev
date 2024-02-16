package goojeans.harulog.admin;

import goojeans.harulog.admin.dto.response.AdminPostResponse;
import goojeans.harulog.admin.dto.response.AdminUserResponse;
import goojeans.harulog.admin.service.AdminService;
import goojeans.harulog.domain.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    ResponseEntity<Response<AdminUserResponse>> searchAllUsers(@RequestParam(defaultValue = "") String nickname,
                                                               @RequestParam(defaultValue = "0") Integer pageNumber) {
        return ResponseEntity.ok(adminService.findAllUsers(pageNumber, nickname));
    }

    @GetMapping("/posts")
    ResponseEntity<Response<AdminPostResponse>> getAllPosts(@RequestParam(defaultValue = "0") Integer pageNumber) {

        return ResponseEntity.ok(adminService.findAllPost(pageNumber));
    }

    @DeleteMapping("/user/{id}")
    ResponseEntity<Response<Void>> deleteUserById(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    @DeleteMapping("/post/{id}")
    ResponseEntity<Response<Void>> deletePostById(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.deletePost(id));
    }

}
