package bureau.release.system.service.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Сущность валидационной ошибки")
public record ValidationErrorResponse(
        @Schema(description = "Ошибочные атрибуты")
        List<Violation> violations
) {}
