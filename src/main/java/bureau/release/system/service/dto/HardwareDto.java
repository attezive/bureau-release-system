package bureau.release.system.service.dto;

import bureau.release.system.model.Hardware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HardwareDto {
    private long id;
    private String name;
    private Set<Integer> missionsIds;
    private Set<Long> firmwareIds;

    public HardwareDto(Hardware hardware) {
        this.id = hardware.getId();
        this.name = hardware.getName();
    }

    public HardwareDto(Hardware hardware, Set<Integer> missionIds) {
        this.id = hardware.getId();
        this.name = hardware.getName();
        this.missionsIds = missionIds;
    }

    public HardwareDto(long id, String name, Set<Long> firmwareIds) {
        this.id = id;
        this.name = name;
        this.firmwareIds = firmwareIds;
    }

    public HardwareDto(Hardware hardware, Set<Integer> missionIds, Set<Long> firmwareIds) {
        this.id = hardware.getId();
        this.name = hardware.getName();
        this.missionsIds = missionIds;
        this.firmwareIds = firmwareIds;
    }
}
