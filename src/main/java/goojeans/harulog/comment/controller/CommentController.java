package goojeans.harulog.comment.controller;

import goojeans.harulog.comment.domain.dto.CommentRequestDto;
import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.comment.service.CommentService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/{id}/comment/{comment_id}") // 뒤쪽 comment_id는 부모 댓글일 경우 0으로 입력
    public ResponseEntity<CommentResponseDto> createComment(@Validated @PathVariable Long id, @PathVariable(required = false) Long comment_id,
                                                            @RequestBody CommentRequestDto requestDto){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(commentService.createComment(id, comment_id, requestDto, userId));
    }
    @PutMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@Validated @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(commentService.updateComment(id, requestDto, userId));
    }

    @DeleteMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> deleteComment(@PathVariable  Long id){
    Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(commentService.deleteComment(id, userId));
    }

    //comment 상세
    @GetMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));


    }
}
