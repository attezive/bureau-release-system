package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность аннотаций манифеста")
public class ManifestAnnotation {
    @Schema(description = "Тип прошивки", example = "astra.bin")
    @JsonAlias("firmware.type")
    String type;

    @Schema(description = "Дата создания", example = "2025-11-11T11:11:11Z")
    @JsonAlias("org.opencontainers.image.created")
    ZonedDateTime createdTime;
}
