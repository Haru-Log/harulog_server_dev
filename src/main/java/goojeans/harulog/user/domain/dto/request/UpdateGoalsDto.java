package goojeans.harulog.user.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGoalsDto {

    @NotNull
    private String categoryName;
    @NotNull
    private Integer goal;

    public static UpdateGoalsDto of(String categoryName, Integer goal) {
        return new UpdateGoalsDto(categoryName, goal);
    }

}
