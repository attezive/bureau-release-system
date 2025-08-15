package bureau.release.system.service.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(description = "Сущность ошибки")
public class ErrorDto {
    @Schema(description = "Сообщение ошибки", example = "Some error info")
    private String message;
}
