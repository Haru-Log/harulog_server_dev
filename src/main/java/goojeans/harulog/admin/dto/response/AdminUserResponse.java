package goojeans.harulog.admin.dto.response;

import goojeans.harulog.user.domain.dto.response.PageInfo;
import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private PageInfo pageInfo;
    private List<UserInfos> content;

    public static AdminUserResponse from(Page<Users> page) {

        PageInfo pageInfo = PageInfo.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .build();

        List<UserInfos> list = page.getContent().stream()
                .map(UserInfos::from)
                .toList();

        return new AdminUserResponse(pageInfo, list);

    }

}
