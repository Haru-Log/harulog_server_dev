package goojeans.harulog.user.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotNull
    private String beforePassword;
    @NotNull
    private String afterPassword;

}
