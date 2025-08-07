package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.FirmwareDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FirmwareMapperTest {

    private final FirmwareMapper firmwareMapper = Mappers.getMapper(FirmwareMapper.class);

    private Firmware firmware;
    private FirmwareDto firmwareDto;
    private FirmwareType firmwareType;

    @BeforeEach
    void setUp() {
        firmwareType = new FirmwareType(1, "FPGA");
        firmware = new Firmware(1L, "firmware", "ociName",
                firmwareType, new ArrayList<>());
        Hardware hardware = Hardware.builder().id(1L).firmwareList(List.of(firmware)).build();
        firmware.getHardwareList().add(hardware);

        firmwareDto = new FirmwareDto(1L, "firmware", "FPGA", "ociName", List.of(1L));
    }

    @Test
    void toDto() {
        FirmwareDto checkedFirmwareDto = firmwareMapper.toDto(firmware);

        assertEquals(firmwareDto.getId(), checkedFirmwareDto.getId());
        assertEquals(firmwareDto.getName(), checkedFirmwareDto.getName());
        assertEquals(firmwareDto.getType(), checkedFirmwareDto.getType());
        assertEquals(firmwareDto.getOciName(), checkedFirmwareDto.getOciName());
        assertEquals(firmwareDto.getHardwareIds(), checkedFirmwareDto.getHardwareIds());
    }

    @Test
    void toEntity() {
        Firmware checkedFirmwareEntity = firmwareMapper.toEntity(firmwareDto, firmwareType);

        assertEquals(firmware.getName(), checkedFirmwareEntity.getName());
        assertEquals(firmware.getOciName(), checkedFirmwareEntity.getOciName());
        assertEquals(firmware.getFirmwareType(), checkedFirmwareEntity.getFirmwareType());
    }
}