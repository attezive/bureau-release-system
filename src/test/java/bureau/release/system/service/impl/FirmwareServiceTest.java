package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.FirmwareTypeDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareType;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.mapping.FirmwareMapper;
import bureau.release.system.service.mapping.FirmwareTypeMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FirmwareServiceTest {
    @InjectMocks
    private FirmwareService firmwareService;

    @Mock
    private FirmwareDao firmwareDao;

    @Mock
    private FirmwareTypeDao firmwareTypeDao;

    @Spy
    private FirmwareMapper firmwareMapper = Mappers.getMapper(FirmwareMapper.class);

    @Spy
    private FirmwareTypeMapper firmwareTypeMapper = Mappers.getMapper(FirmwareTypeMapper.class);

    @Test
    void createFirmware() {
        FirmwareDto firmwareDto = new FirmwareDto();
        firmwareDto.setName("Firmware");
        firmwareDto.setOciName("testOci");
        firmwareDto.setType("APPLICATION");

        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");

        when(firmwareTypeDao.findByName(firmwareType.getName())).thenReturn(Optional.of(firmwareType));
        when(firmwareDao.save(eq(firmwareMapper.toEntity(firmwareDto, firmwareType)))).thenAnswer(inv -> {
            Firmware firmware = inv.getArgument(0, Firmware.class);
            firmware.setId(3L);
            return firmware;
        });

        FirmwareDto firmwareDtoResult = firmwareService.createFirmware(firmwareDto);

        assertEquals(3L, firmwareDtoResult.getId(), "Incorrect firmware id");
        assertEquals(firmwareDto.getName(), firmwareDtoResult.getName(), "Incorrect name");
        assertEquals(firmwareDto.getOciName(), firmwareDtoResult.getOciName(), "Incorrect OCI name");
        assertEquals(firmwareDto.getType(), firmwareDtoResult.getType(), "Incorrect type");
        verify(firmwareTypeDao, Mockito.times(1)).findByName(firmwareType.getName());
        verify(firmwareMapper, Mockito.times(2)).toEntity(firmwareDto, firmwareType);
        verify(firmwareMapper, Mockito.times(1)).toDto(any(Firmware.class));
        verify(firmwareDao, Mockito.times(1)).save(any(Firmware.class));
    }

    @Test
    void createFirmwareFailed() {
        FirmwareDto firmwareDto = new FirmwareDto();
        firmwareDto.setName("Firmware");
        firmwareDto.setOciName("testOci");
        firmwareDto.setType("NOT_FOUND");

        when(firmwareTypeDao.findByName("NOT_FOUND")).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.createFirmware(firmwareDto));

        assertEquals("Type not found", thrown.getMessage(), "Incorrect message");
        verify(firmwareTypeDao, Mockito.times(1)).findByName("NOT_FOUND");
    }

    @Test
    void getFirmwareById() {
        Long firmwareId = 1L;
        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");
        Hardware hardware = Hardware.builder().id(1L).build();
        Firmware firmware = Firmware.builder().id(firmwareId).name("Firmware").firmwareType(firmwareType)
                .ociName("testrepo").hardwareList(List.of(hardware)).build();
        FirmwareDto firmwareDto = firmwareMapper.toDto(firmware);

        when(firmwareDao.findById(firmwareId)).thenReturn(Optional.of(firmware));

        FirmwareDto firmwareDtoResult = firmwareService.getFirmwareById(firmwareId);

        assertEquals(firmwareDto, firmwareDtoResult, "Incorrect firmware");
        verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
        verify(firmwareMapper, Mockito.times(2)).toDto(firmware);
    }

    @Test
    void getFirmwareByIdFailed() {
        Long firmwareId = 1L;
        when(firmwareDao.findById(firmwareId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.getFirmwareById(firmwareId));

        assertEquals("Firmware not found", thrown.getMessage(), "Incorrect message");
        verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
    }

    @Test
    void getAllFirmware() {
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");

        Hardware firstHardware = Hardware.builder().id(1L).build();
        Hardware secondHardware = Hardware.builder().id(2L).build();

        Long firstFirmwareId = 1L;
        Firmware firstFirmware = Firmware.builder().id(firstFirmwareId).name("First Firmware")
                .firmwareType(firstFirmwareType).ociName("testrepo")
                .hardwareList(List.of(firstHardware)).build();
        FirmwareDto firstFirmwareDto = firmwareMapper.toDto(firstFirmware);

        Long secondFirmwareId = 2L;
        Firmware secondFirmware = Firmware.builder().id(secondFirmwareId).name("Second Firmware")
                .firmwareType(secondFirmwareType).ociName("testrepo")
                .hardwareList(List.of(firstHardware, secondHardware)).build();
        FirmwareDto secondFirmwareDto = firmwareMapper.toDto(secondFirmware);

        when(firmwareDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware, secondFirmware)));
        when(firmwareDao.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware)));
        when(firmwareDao.findAll(PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondFirmware)));

        List<FirmwareDto> allFirmwareList = firmwareService.getAllFirmware(0, 2);
        List<FirmwareDto> firstFirmwareList = firmwareService.getAllFirmware(0, 1);
        List<FirmwareDto> secondFirmwareList = firmwareService.getAllFirmware(1, 1);

        assertEquals(List.of(firstFirmwareDto), firstFirmwareList, "Incorrect first Firmware page");
        assertEquals(List.of(secondFirmwareDto), secondFirmwareList, "Incorrect second Firmware page");
        assertEquals(List.of(firstFirmwareDto, secondFirmwareDto), allFirmwareList, "Incorrect all Firmware page");
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 2));
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 1));
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(1, 1));
        verify(firmwareMapper, Mockito.times(6)).toDto(any(Firmware.class));
    }

    @Test
    void getAllFirmwareEmptyList(){
        int page = 1;
        int pageSize = 10;

        when(firmwareDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<FirmwareDto> allFirmwareList = firmwareService.getAllFirmware(page, pageSize);

        assertEquals(new ArrayList<>(), allFirmwareList, "Incorrect all Firmware page");
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }


    @Test
    void getFirmwareTypes() {
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        FirmwareTypeDto firstFirmwareTypeDto = new FirmwareTypeDto(1, "APPLICATION");

        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");
        FirmwareTypeDto secondFirmwareTypeDto = new FirmwareTypeDto(2, "FPGA");

        List<FirmwareTypeDto> firmwareTypes = List.of(firstFirmwareTypeDto, secondFirmwareTypeDto);

        when(firmwareTypeDao.findAll()).thenReturn(List.of(firstFirmwareType, secondFirmwareType));

        List<FirmwareTypeDto> allFirmwareTypes = firmwareService.getFirmwareTypes();

        assertEquals(firmwareTypes, allFirmwareTypes, "Incorrect firmware types");
        verify(firmwareTypeDao, Mockito.times(1)).findAll();
        verify(firmwareTypeMapper, Mockito.times(firmwareTypes.size())).toDto(any(FirmwareType.class));
    }
}