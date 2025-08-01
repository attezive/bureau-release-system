package bureau.release.system.service.dto;

import bureau.release.system.model.Release;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReleaseDto {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String ociName;
    private String reference;
    private ReleaseStatusDto status;
    private Long originId;
    private int missionId;
    private List<FirmwareVersionDto> firmwareVersions;

    public ReleaseDto(Release release) {
        this.id = release.getId();
        this.name = release.getName();
        this.releaseDate = release.getReleaseDate();
        this.ociName = release.getOciName();
        this.status = ReleaseStatusDto.valueOf(release.getStatus().getName());
        this.missionId = release.getMission().getId();
    }

    public ReleaseDto(Release release, List<FirmwareVersionDto> firmwareVersions) {
        this.id = release.getId();
        this.name = release.getName();
        this.releaseDate = release.getReleaseDate();
        this.ociName = release.getOciName();
        this.status = ReleaseStatusDto.valueOf(release.getStatus().getName());
        this.missionId = release.getMission().getId();
        this.firmwareVersions = firmwareVersions;
    }
}
