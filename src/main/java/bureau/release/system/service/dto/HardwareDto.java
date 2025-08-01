package bureau.release.system.service.dto;

import bureau.release.system.model.Hardware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HardwareDto {
    private long id;
    private String name;
    private List<Integer> missionsIds;
    private List<Long> firmwareIds;

    public HardwareDto(Hardware hardware, List<Long> firmwareIds) {
        this.id = hardware.getId();
        this.name = hardware.getName();
        this.missionsIds = new ArrayList<>();
        this.firmwareIds = firmwareIds;
    }

    public HardwareDto(Hardware hardware, List<Long> firmwareIds, List<Integer> missionIds) {
        this.id = hardware.getId();
        this.name = hardware.getName();
        this.missionsIds = missionIds;
        this.firmwareIds = firmwareIds;
    }
}
