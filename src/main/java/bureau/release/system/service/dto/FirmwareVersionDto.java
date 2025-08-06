package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность использованной версии прошивки в релизе")
public class FirmwareVersionDto {
    @Schema(description = "Уникальный идентификатор версии прошивки", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Наименование версии", example = "v1")
    @NotBlank
    private String firmwareVersion;

    @Schema(description = "Идентификатор прошивки", example = "1")
    @Positive
    private Long firmwareId;

    @Schema(description = "Идентификатор релиза", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long releaseId;

    @Schema(description = "Идентификатор девайса", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long hardwareId;
}
