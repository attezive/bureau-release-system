package bureau.release.system.service.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность манифеста артефакта")
public class Manifest {
    @Schema(description = "Наименование артефакта", example = "astra_firm")
    String name;

    @Schema(description = "Тэг артефакта", example = "v1")
    String reference;

    @Schema(description = "Слои манифеста")
    List<ManifestLayer> layers;

    @Schema(description = "Аннотации манифеста")
    ManifestAnnotation annotations;
}
