package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Сущность репозитория вебхук-ивента")
public class EventRepository {
    @Schema(description = "Название репозитория")
    String name;

    @Schema(description = "Название проекта")
    String namespace;

    @Schema(description = "Полное название OCI репозитоия")
    @JsonAlias("repo_full_name")
    String repoFullName;
}
