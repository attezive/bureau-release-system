package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность девайса")
public class HardwareDto {
    @Schema(description = "Уникальный идентификатор девайса", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Наименование девайса", example = "astra")
    private String name;

    @Schema(description = "Список id миссий, использующих девайс", example = "[1, 2]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Integer> missionsIds;

    @Schema(description = "Список id прошивок, используемых девайсом", example = "[1, 2]")
    private List<Long> firmwareIds;
}
