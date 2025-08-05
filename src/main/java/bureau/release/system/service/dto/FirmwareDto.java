package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность прошивки")
public class FirmwareDto {
    @Schema(description = "Уникальный идентификатор прошивки", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Наименование прошивки", example = "astra_firm")
    private String name;

    @Schema(description = "Тип прошивки", allowableValues = {"FPGA", "APPLICATION"})
    private String type;

    @Schema(description = "Наименование репозитория oci", example = "project/repo")
    private String ociName;

    @Schema(description = "Список id девайсов, использующих прошивку", example = "[1, 2]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> hardwareIds;
}
