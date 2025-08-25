package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Schema(description = "Сущность списка имен пользователей")
public class UsernameListDto {
    @Schema(description = "Список имен пользователей", example = "[\"user1\", \"user2\"]")
    private List<String> usernames;
}
