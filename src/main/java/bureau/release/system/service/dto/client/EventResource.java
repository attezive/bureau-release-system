package bureau.release.system.service.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность ресурса вебхук-ивента")
public class EventResource {
    @Schema(description = "Тег артефакта")
    String tag;
}
