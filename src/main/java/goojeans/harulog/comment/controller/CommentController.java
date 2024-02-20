package goojeans.harulog.comment.controller;

import goojeans.harulog.comment.domain.dto.CommentRequestDto;
import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.comment.service.CommentService;
import goojeans.harulog.domain.dto.Response;
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
    public ResponseEntity<Response<CommentResponseDto>> createComment(@Validated @PathVariable Long id, @PathVariable(required = false) Long comment_id,
                                                                      @RequestBody CommentRequestDto requestDto){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(commentService.createComment(id, comment_id, requestDto, userId)));
    }
    @PutMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> updateComment(@Validated @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(commentService.updateComment(id, requestDto, userId)));
    }

    @DeleteMapping("comment/{id}")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable  Long id){
    Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(commentService.deleteComment(id, userId));
    }

    //comment 상세
    @GetMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(Response.ok(commentService.getComment(id)));


    }
}
