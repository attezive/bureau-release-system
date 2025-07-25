package bureau.release.system.service.dto;

import bureau.release.system.model.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReleaseStatusDto {
    private int id;
    private String name;

    public ReleaseStatusDto(ReleaseStatus releaseStatus) {
        this.id = releaseStatus.getId();
        this.name = releaseStatus.getName();
    }
}
