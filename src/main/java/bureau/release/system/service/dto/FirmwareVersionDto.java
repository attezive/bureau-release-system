package bureau.release.system.service.dto;

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
}
