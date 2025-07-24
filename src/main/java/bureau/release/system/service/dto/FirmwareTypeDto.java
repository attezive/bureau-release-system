package bureau.release.system.service.dto;

import bureau.release.system.model.FirmwareType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FirmwareTypeDto {
    private int id;
    private String name;
    private List<Long> firmwareIds;

    public FirmwareTypeDto(FirmwareType firmwareType) {
        this.id = firmwareType.getId();
        this.name = firmwareType.getName();
    }

    public FirmwareTypeDto(FirmwareType firmwareType, List<Long> firmwareIds) {
        this.id = firmwareType.getId();
        this.name = firmwareType.getName();
        this.firmwareIds = firmwareIds;
    }
}
