package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность вебхука артефакта")
public class ArtifactWebhook {
    @Schema(description = "Тип вебхука")
    String type;

    @Schema(description = "Информация вебхука")
    @JsonAlias("event_data")
    EventData eventData;
}
