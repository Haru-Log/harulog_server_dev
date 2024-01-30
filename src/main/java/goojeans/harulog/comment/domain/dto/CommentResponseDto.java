package goojeans.harulog.comment.domain.dto;

import goojeans.harulog.comment.domain.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CommentResponseDto extends CommentRequestDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private String nickname;
    private Long parentId;
    private int depth;
    private List<CommentResponseDto> children= new ArrayList<>();

    // Entity -> DTO
    public CommentResponseDto(Comment comment, List<CommentResponseDto> commentResponseDtoList){
        this.id             = comment.getId();
        this.nickname       = comment.getNickname();
        this.content        = comment.getContent();
        this.depth          = comment.getDepth();
        this.createdAt      = comment.getCreatedAt();
        this.children       = commentResponseDtoList;
    }

    public CommentResponseDto(Comment comment, Long id){
        this.id             = comment.getId();
        this.nickname       = comment.getNickname();
        this.content        = comment.getContent();
        this.parentId       = id;
        this.depth          = comment.getDepth();
        this.createdAt      = comment.getCreatedAt();

    }

    public CommentResponseDto(Comment comment){
        this.id             = comment.getId();
        this.nickname       = comment.getNickname();
        this.content        = comment.getContent();
        this.depth          = comment.getDepth();
        this.createdAt      = comment.getCreatedAt();

    }

}