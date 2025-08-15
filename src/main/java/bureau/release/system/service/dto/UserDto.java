package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность пользователя")
public class UserDto {
    @Schema(description = "Имя пользователя", example = "user")
    @NotBlank
    private String username;

    @Schema(description = "Пароль пользователя")
    @NotBlank
    private String password;

    @Schema(description = "Состояние")
    private boolean enabled;

    @Schema(description = "Список доступов", example = "[\"admin\", \"user\"]")
    @NotEmpty
    private List<String> authorities;
}
