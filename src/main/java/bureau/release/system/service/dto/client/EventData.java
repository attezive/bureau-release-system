package bureau.release.system.service.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность информации вебхук-ивента")
public class EventData {
    @Schema(description = "Список ресурсов вебхука")
    List<EventResource> resources;

    @Schema(description = "Информация о репозитории, взаимодействующим с вебхуком")
    EventRepository repository;
}
