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
    private String digest;
    private ReleaseStatusDto status;
    private Long originId;
    private int missionId;
    private List<ReleaseContentDto> releaseContent;
}
