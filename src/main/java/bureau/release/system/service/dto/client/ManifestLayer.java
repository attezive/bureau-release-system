package bureau.release.system.service.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность слоя манифеста")
public class ManifestLayer {
    @Schema(description = "Тип файла", example = "application/vnd.oci.image.layer.v1.tar")
    String mediaType;

    @Schema(description = "digest файла в виде sha256", example = "sha256:8...4")
    String digest;

    @Schema(description = "Размер файла в байтах", example = "8")
    Integer size;

    @Schema(description = "Аннотации слоя")
    LayerAnnotations annotations;
}
