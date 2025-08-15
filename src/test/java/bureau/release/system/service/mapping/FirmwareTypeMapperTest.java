package bureau.release.system.service.mapping;

import bureau.release.system.model.FirmwareType;
import bureau.release.system.service.dto.FirmwareTypeDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class FirmwareTypeMapperTest {

    private final FirmwareTypeMapper firmwareTypeMapper = Mappers.getMapper(FirmwareTypeMapper.class);

    @Test
    void toDto() {
        FirmwareType firmwareType = new FirmwareType(1, "FPGA");
        FirmwareTypeDto firmwareTypeDto = new FirmwareTypeDto(1, "FPGA");

        FirmwareTypeDto checkedFirmwareTypeDto = firmwareTypeMapper.toDto(firmwareType);

        assertEquals(firmwareTypeDto.getId(), checkedFirmwareTypeDto.getId());
        assertEquals(firmwareTypeDto.getName(), checkedFirmwareTypeDto.getName());
    }

    @Test
    void toEntity() {
        FirmwareType firmwareType = new FirmwareType(1, "FPGA");
        FirmwareTypeDto firmwareTypeDto = new FirmwareTypeDto(1, "FPGA");

        FirmwareType checkedFirmwareTypeEntity = firmwareTypeMapper.toEntity(firmwareTypeDto);

        assertEquals(firmwareType.getName(), checkedFirmwareTypeEntity.getName());
    }
}