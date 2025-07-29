package bureau.release.system.service.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArtifactTag {
    Integer id;
    String name;
    @JsonAlias("artifact_id")
    Integer artifactId;
    @JsonAlias("push_time")
    LocalDateTime pushTime;
}
