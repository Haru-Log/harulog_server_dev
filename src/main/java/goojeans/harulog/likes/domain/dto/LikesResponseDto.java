package goojeans.harulog.likes.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikesResponseDto {
    private int likeCount;


    public LikesResponseDto(int likeCount){
        this.likeCount = likeCount;
    }
}
