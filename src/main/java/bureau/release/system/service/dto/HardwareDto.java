package bureau.release.system.service.dto;

import bureau.release.system.model.Hardware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HardwareDto {
    private long id;
    private String name;
    private List<Integer> missionsIds;
    private List<Integer> firmwareIds;

    public HardwareDto(Hardware hardware) {
        this.id = hardware.getId();
        this.name = hardware.getName();
    }

    public HardwareDto(Hardware hardware, List<Integer> missionIds) {
        this.id = hardware.getId();
        this.name = hardware.getName();
        this.missionsIds = missionIds;
    }

    public HardwareDto(String name, List<Integer> firmwareIds) {
        this.name = name;
        this.firmwareIds = firmwareIds;
    }

    public Hardware toEntity() {
        return Hardware.builder().name(name).build();
    }
}
