package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность типа прошивки")
public class FirmwareTypeDto {
    @Schema(description = "Уникальный идентификатор типа прошивки", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    @Schema(description = "Наименование типа прошивки", example = "FPGA")
    private String name;
}
