package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LayerAnnotation {
    @JsonAlias("org.opencontainers.image.title")
    String title;
}
