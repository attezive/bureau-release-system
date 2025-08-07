package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.FirmwareVersionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FirmwareVersionMapperTest {

    private final FirmwareVersionMapper firmwareVersionMapper = Mappers.getMapper(FirmwareVersionMapper.class);

    private FirmwareVersion firmwareVersion;
    private FirmwareVersionDto firmwareVersionDto;
    private Release release;
    private Firmware firmware;
    private Hardware hardware;

    @BeforeEach
    void setUp() {
        firmware = Firmware.builder().id(1L).hardwareList(new ArrayList<>()).build();
        hardware = Hardware.builder().id(1L).firmwareList(List.of(firmware)).build();
        firmware.getHardwareList().add(hardware);
        release = Release.builder().id(1L).build();
        firmwareVersion = new FirmwareVersion(1L, firmware, hardware, release, "firmwareVersion");

        firmwareVersionDto = new FirmwareVersionDto(1L, "firmwareVersion", 1L, 1L, 1L);
    }

    @Test
    void toDto() {
        FirmwareVersionDto checkedFirmwareVersionDto = firmwareVersionMapper.toDto(firmwareVersion);

        assertEquals(firmwareVersionDto.getId(), checkedFirmwareVersionDto.getId());
        assertEquals(firmwareVersionDto.getFirmwareId(), checkedFirmwareVersionDto.getId());
        assertEquals(firmwareVersionDto.getFirmwareVersion(), checkedFirmwareVersionDto.getFirmwareVersion());
        assertEquals(firmwareVersionDto.getReleaseId(), checkedFirmwareVersionDto.getReleaseId());
        assertEquals(firmwareVersionDto.getHardwareId(), checkedFirmwareVersionDto.getHardwareId());
    }

    @Test
    void toEntity() {
        FirmwareVersion checkedFirmwareVersionEntity = firmwareVersionMapper.toEntity(
                firmwareVersionDto, firmware, hardware, release);

        assertEquals(firmwareVersion.getFirmwareVersion(), checkedFirmwareVersionEntity.getFirmwareVersion());
        assertEquals(firmwareVersion.getFirmware().getId(), checkedFirmwareVersionEntity.getFirmware().getId());
        assertEquals(firmwareVersion.getRelease().getId(), checkedFirmwareVersionEntity.getRelease().getId());
        assertEquals(firmwareVersion.getHardware().getId(), checkedFirmwareVersionEntity.getHardware().getId());
    }
}