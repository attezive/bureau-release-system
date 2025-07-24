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
    private String status;
    private List<Long> contentIds;

    public ReleaseDto(Release release) {
        this.id = release.getId();
        this.name = release.getName();
        this.releaseDate = release.getReleaseDate();
        this.ociName = release.getOciName();
        this.status = release.getStatus().getName();
    }

    public ReleaseDto(Release release, List<Long> contentIds) {
        this.id = release.getId();
        this.name = release.getName();
        this.releaseDate = release.getReleaseDate();
        this.ociName = release.getOciName();
        this.status = release.getStatus().getName();
        this.contentIds = contentIds;
    }
}
