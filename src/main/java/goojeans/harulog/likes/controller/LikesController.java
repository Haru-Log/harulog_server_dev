package goojeans.harulog.likes.controller;


import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.likes.domain.dto.LikesResponseDto;
import goojeans.harulog.likes.service.LikesService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikesController {

    private final LikesService likesService;
    private final SecurityUtils securityUtils;


    @PostMapping("/post/likes/{postId}")
    public ResponseEntity<Response<LikesResponseDto>> likes(@PathVariable Long postId){
        Long userId = securityUtils.getCurrentUserInfo().getId();

        return ResponseEntity.ok(Response.ok(likesService.saveLikes(postId, userId)));
    }

    @DeleteMapping("/post/likes/{postId}")
    public ResponseEntity<Response<LikesResponseDto>> unLikes(@PathVariable Long postId){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(likesService.deleteLikes(postId,userId)));
    }
}
