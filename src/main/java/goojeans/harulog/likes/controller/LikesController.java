package goojeans.harulog.likes.controller;


import goojeans.harulog.likes.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LikesController {

    private final LikesService likesService;

    @Autowired
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @PostMapping("/post/likes/{postId}")
    public ResponseEntity<String> likes(@PathVariable Long postId, @RequestParam Long userId){
        likesService.saveLikes(postId, userId);
        return ResponseEntity.ok().body("좋아요성공");
    }

    @DeleteMapping("/post/likes/{postId}")
    public ResponseEntity<String> unLikes(@PathVariable Long postId, @RequestParam Long userId){
        likesService.deleteLikes(postId, userId);
        return ResponseEntity.ok().body("좋아요해제성공");
    }
}
