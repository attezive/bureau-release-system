package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;

    @Schema(description = "Тип прошивки", allowableValues = {"FPGA", "APPLICATION"})
    @NotBlank
    private String type;

    @Schema(description = "Наименование репозитория oci", example = "project/repo")
    @NotBlank
    private String ociName;

    @Schema(description = "Список id девайсов, использующих прошивку", example = "[1, 2]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> hardwareIds;
}
