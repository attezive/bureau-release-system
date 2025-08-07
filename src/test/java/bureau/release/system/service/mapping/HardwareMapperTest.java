package bureau.release.system.service.mapping;

import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HardwareMapperTest {

    private final HardwareMapper hardwareMapper = Mappers.getMapper(HardwareMapper.class);

    private Hardware hardware;
    private HardwareDto hardwareDto;
    private Firmware firmware;
    private Mission mission;

    @BeforeEach
    void setUp() {
        mission = Mission.builder().id(1).build();
        firmware = Firmware.builder().id(1L).build();
        hardware = new Hardware(1L, "Hardware", List.of(mission), List.of(firmware));
        mission.setHardwareList(List.of(hardware));
        firmware.setHardwareList(List.of(hardware));

        hardwareDto = new HardwareDto(1L, "Hardware", List.of(1), List.of(1L));
    }

    @Test
    void toDto() {
        HardwareDto checkedHardwareDto = hardwareMapper.toDto(hardware);

        assertEquals(hardwareDto.getId(), checkedHardwareDto.getId());
        assertEquals(hardwareDto.getName(), checkedHardwareDto.getName());
        assertEquals(hardwareDto.getMissionsIds(), checkedHardwareDto.getMissionsIds());
        assertEquals(hardwareDto.getFirmwareIds(), checkedHardwareDto.getFirmwareIds());
    }

    @Test
    void toEntity() {
        Hardware checkedHardwareEntity = hardwareMapper.toEntity(hardwareDto, List.of(firmware), List.of(mission));

        assertEquals(hardware.getName(), checkedHardwareEntity.getName());
        assertEquals(hardware.getFirmwareList().size(), checkedHardwareEntity.getFirmwareList().size());
        assertEquals(hardware.getFirmwareList().getFirst().getId(), checkedHardwareEntity.getFirmwareList().getFirst().getId());
    }
}