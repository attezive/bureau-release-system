package bureau.release.system.service.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность ошибочного атрибута")
public record Violation(
        @Schema(description = "Название поля", example = "someField")
        String fieldName,

        @Schema(description = "Сообщение о валидационной ошибке", example = "должно быть больше 0")
        String message
) {}
