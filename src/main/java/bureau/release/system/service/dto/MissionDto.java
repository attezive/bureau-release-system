package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность миссии")
public class MissionDto {
    @Schema(description = "Уникальный идентификатор миссии", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    @Schema(description = "Наименование миссии", example = "R1")
    @NotBlank
    private String name;

    @Schema(description = "Список id девайсов, используемых в миссии", example = "[1, 2]")
    @NotEmpty
    private List<Long> hardwareIds;
}
