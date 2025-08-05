package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long hardwareId;

    @Schema(description = "Список используемых версий прошивок")
    private List<FirmwareVersionDto> firmwareVersions;
}
