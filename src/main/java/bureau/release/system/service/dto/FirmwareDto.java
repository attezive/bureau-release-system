package bureau.release.system.service.dto;

import bureau.release.system.model.Firmware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FirmwareDto {
    private long id;
    private String name;
    private String type;
    private String ociName;
    private Set<Long> hardwareIds;
    private List<Long> referenceIds;

    public FirmwareDto(Firmware firmware) {
        this.id = firmware.getId();
        this.name = firmware.getName();
        this.type = firmware.getType().getName();
        this.ociName = firmware.getOciName();
    }

    public FirmwareDto(Firmware firmware,  Set<Long> hardwareIds) {
        this.id = firmware.getId();
        this.name = firmware.getName();
        this.type = firmware.getType().getName();
        this.ociName = firmware.getOciName();
        this.hardwareIds = hardwareIds;
    }
}
