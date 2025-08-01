package bureau.release.system.service.dto;

import bureau.release.system.model.Firmware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FirmwareDto {
    private long id;
    private String name;
    private String type;
    private String ociName;
    private List<Long> hardwareIds;

    public FirmwareDto(Firmware firmware) {
        this.id = firmware.getId();
        this.name = firmware.getName();
        this.type = firmware.getType().getName();
        this.ociName = firmware.getOciName();
        this.hardwareIds = new ArrayList<>();
    }

    public FirmwareDto(Firmware firmware, List<Long> hardwareIds) {
        this.id = firmware.getId();
        this.name = firmware.getName();
        this.type = firmware.getType().getName();
        this.ociName = firmware.getOciName();
        this.hardwareIds = hardwareIds;
    }
}
