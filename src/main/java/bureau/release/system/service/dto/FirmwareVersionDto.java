package bureau.release.system.service.dto;

import bureau.release.system.model.FirmwareVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FirmwareVersionDto {
    private Long id;
    private String firmwareVersion;
    private Long firmwareId;
    private Long releaseId;
    private Long hardwareId;

    public FirmwareVersionDto(FirmwareVersion firmwareVersion) {
        this.id = firmwareVersion.getId();
        this.firmwareVersion = firmwareVersion.getFirmwareVersion();
        this.firmwareId = firmwareVersion.getFirmware().getId();
        this.releaseId = firmwareVersion.getRelease().getId();
        this.hardwareId = firmwareVersion.getHardware().getId();
    }
}
