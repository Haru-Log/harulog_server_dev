package goojeans.harulog.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {

    private PageInfo pageInfo;
    private List<FollowInfo> contents;

    public static SearchResponse of(PageInfo pageInfo, List<FollowInfo> contents){
        return new SearchResponse(pageInfo, contents);
    }
}
