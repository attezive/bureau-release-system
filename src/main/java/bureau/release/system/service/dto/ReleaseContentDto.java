package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Сущность содержания релиза относительно девайса")
public class ReleaseContentDto {
    @Schema(description = "Идентификатор девайса", example = "1")
    @Positive
    private Long hardwareId;

    @Schema(description = "Список используемых версий прошивок")
    @NotEmpty
    @Valid
    private List<FirmwareVersionDto> firmwareVersions;
}
