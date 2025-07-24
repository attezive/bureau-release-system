package bureau.release.system.service.dto;

import bureau.release.system.model.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReleaseStatusDto {
    private int id;
    private String name;
    private List<Long> releaseIds;

    public ReleaseStatusDto(ReleaseStatus releaseStatus) {
        this.id = releaseStatus.getId();
        this.name = releaseStatus.getName();
    }

    public ReleaseStatusDto(ReleaseStatus releaseStatus, List<Long> releaseIds) {
        this.id = releaseStatus.getId();
        this.name = releaseStatus.getName();
        this.releaseIds = releaseIds;
    }
}
