package goojeans.harulog.comment.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentRequestDto {
    @NotNull
    private String content;
}
