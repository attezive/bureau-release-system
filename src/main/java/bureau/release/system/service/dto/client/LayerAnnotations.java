package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность аннотаций слоя")
public class LayerAnnotations {
    @Schema(description = "Наименование файла", example = "astra.bin")
    @JsonAlias("org.opencontainers.image.title")
    String title;
}
