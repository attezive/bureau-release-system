package bureau.release.system.service.dto;

import bureau.release.system.model.ReleaseContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReleaseContentDto {
    private long id;
    private String reference;
    private Long firmwareId;
    private Long releaseId;

    public ReleaseContentDto(ReleaseContent releaseContent) {
        this.id = releaseContent.getId();
        this.reference = releaseContent.getReference();
    }

    public ReleaseContentDto(ReleaseContent releaseContent, Long firmwareIds, Long releaseIds) {
        this.id = releaseContent.getId();
        this.reference = releaseContent.getReference();
        this.firmwareId = firmwareIds;
        this.releaseId = releaseIds;
    }

    public ReleaseContentDto(Long firmwareId, String reference) {
        this.firmwareId = firmwareId;
        this.reference = reference;
    }
}
