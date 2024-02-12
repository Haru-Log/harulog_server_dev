package goojeans.harulog.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo {

    private Integer totalPages;
    private Long totalElements;
    private Integer number;
    private Integer size;
}
