package goojeans.harulog.likes.controller;


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
    public ResponseEntity<String> likes(@PathVariable Long postId){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        likesService.saveLikes(postId, userId);
        return ResponseEntity.ok().body("좋아요성공");
    }

    @DeleteMapping("/post/likes/{postId}")
    public ResponseEntity<String> unLikes(@PathVariable Long postId){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        likesService.deleteLikes(postId, userId);
        return ResponseEntity.ok().body("좋아요해제성공");
    }
}
