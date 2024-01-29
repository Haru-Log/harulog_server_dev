package goojeans.harulog.comment.controller;

import goojeans.harulog.comment.domain.dto.CommentRequestDto;

import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{id}/comment/{comment_id}") // 뒤쪽 comment_id는 부모 댓글일 경우 0으로 입력
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long id, @PathVariable(required = false) Long comment_id,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @RequestParam Long userId){
        return ResponseEntity.ok(commentService.createComment(id, comment_id, requestDto, userId));
    }
    @PutMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                                            @RequestParam Long userId){
        return ResponseEntity.ok(commentService.updateComment(id, requestDto, userId));
    }

    @DeleteMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> deleteComment(@PathVariable  Long id,
                                                            @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.deleteComment(id, userId));
    }



}
