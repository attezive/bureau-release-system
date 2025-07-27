package bureau.release.system.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Artifact {
    @JsonAlias("repository_name")
    String repositoryName;
    @JsonAlias("repository_id")
    Integer repositoryId;
    @JsonAlias("artifact_type")
    String artifactType;
    String digest;
    @JsonAlias("push_time")
    LocalDateTime pushTime;
    Integer size;
    List<ArtifactTag> tags;
}
