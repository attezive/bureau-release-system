package bureau.release.system.service.dto;

import bureau.release.system.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MissionDto {
    private int id;
    private String name;
    private List<Long> hardwareIds;

    public MissionDto(Mission mission) {
        this.id = mission.getId();
        this.name = mission.getName();
    }

    public MissionDto(Mission mission, List<Long> hardwareIds) {
        this.id = mission.getId();
        this.name = mission.getName();
        this.hardwareIds = hardwareIds;
    }

    public MissionDto(String name, List<Long> hardwareIds) {
        this.name = name;
        this.hardwareIds = hardwareIds;
    }
}
