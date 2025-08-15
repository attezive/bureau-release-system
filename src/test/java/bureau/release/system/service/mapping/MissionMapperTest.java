package bureau.release.system.service.mapping;

import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.MissionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionMapperTest {

    private final MissionMapper missionMapper = Mappers.getMapper(MissionMapper.class);

    private MissionDto missionDto;
    private Mission mission;
    private Hardware hardware;

    @BeforeEach
    void setUp() {
        hardware = Hardware.builder().id(1L).build();
        mission = new Mission(1, "mission", List.of(hardware));
        hardware.setMissions(List.of(mission));

        missionDto = new MissionDto(1, "mission", List.of(1L));
    }

    @Test
    void toDto() {
        MissionDto checkedMissionDto = missionMapper.toDto(mission);

        assertEquals(missionDto.getId(), checkedMissionDto.getId());
        assertEquals(missionDto.getName(), checkedMissionDto.getName());
        assertEquals(missionDto.getHardwareIds(), checkedMissionDto.getHardwareIds());
    }

    @Test
    void toEntity() {
        Mission checkedMissionEntity = missionMapper.toEntity(missionDto, List.of(hardware));

        assertEquals(mission.getName(), checkedMissionEntity.getName());
        assertEquals(mission.getHardwareList().size(), checkedMissionEntity.getHardwareList().size());
        assertEquals(mission.getHardwareList().getFirst().getId(), checkedMissionEntity.getHardwareList().getFirst().getId());
    }
}