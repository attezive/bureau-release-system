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
    void createFirmware_createAndReturnDTO() {
        FirmwareDto requestFirmwareDto = new FirmwareDto();
        requestFirmwareDto.setName("Firmware");
        requestFirmwareDto.setOciName("testOci");
        requestFirmwareDto.setType("APPLICATION");

        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");

        when(firmwareTypeDao.findByName(firmwareType.getName())).thenReturn(Optional.of(firmwareType));
        when(firmwareDao.save(eq(firmwareMapper.toEntity(requestFirmwareDto, firmwareType)))).thenAnswer(inv -> {
            Firmware firmware = inv.getArgument(0, Firmware.class);
            firmware.setId(3L);
            return firmware;
        });

        FirmwareDto createdFirmwareDto = firmwareService.createFirmware(requestFirmwareDto);

        assertEquals(3L, createdFirmwareDto.getId(), "Incorrect firmware id");
        assertEquals(requestFirmwareDto.getName(), createdFirmwareDto.getName(), "Incorrect name");
        assertEquals(requestFirmwareDto.getOciName(), createdFirmwareDto.getOciName(), "Incorrect OCI name");
        assertEquals(requestFirmwareDto.getType(), createdFirmwareDto.getType(), "Incorrect type");
        verify(firmwareTypeDao, Mockito.times(1)).findByName(firmwareType.getName());
        verify(firmwareMapper, Mockito.times(2)).toEntity(requestFirmwareDto, firmwareType);
        verify(firmwareMapper, Mockito.times(1)).toDto(any(Firmware.class));
        verify(firmwareDao, Mockito.times(1)).save(any(Firmware.class));
    }

    @Test
    void createFirmware_failedByUnknownType_throwsEntityNotFoundException() {
        FirmwareDto requestFirmwareDto = new FirmwareDto();
        requestFirmwareDto.setName("Firmware");
        requestFirmwareDto.setOciName("testOci");
        requestFirmwareDto.setType("NOT_FOUND");

        when(firmwareTypeDao.findByName("NOT_FOUND")).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.createFirmware(requestFirmwareDto));

        assertEquals("Type not found", thrown.getMessage(), "Incorrect message");
        verify(firmwareTypeDao, Mockito.times(1)).findByName("NOT_FOUND");
    }

    @Test
    void getFirmwareById_returnFirmware() {
        Long firmwareId = 1L;
        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");
        Hardware hardware = Hardware.builder().id(1L).build();
        Firmware expectedFirmware = Firmware.builder().id(firmwareId).name("Firmware").firmwareType(firmwareType)
                .ociName("testrepo").hardwareList(List.of(hardware)).build();
        FirmwareDto expectedFirmwareDto = firmwareMapper.toDto(expectedFirmware);

        when(firmwareDao.findById(firmwareId)).thenReturn(Optional.of(expectedFirmware));

        FirmwareDto receivedFirmwareDto = firmwareService.getFirmwareById(firmwareId);

        assertEquals(expectedFirmwareDto, receivedFirmwareDto, "Incorrect firmware");
        verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
        verify(firmwareMapper, Mockito.times(2)).toDto(expectedFirmware);
    }

    @Test
    void getFirmwareById_failedByUnknownId_throwsEntityNotFoundException() {
        Long firmwareId = 1L;
        when(firmwareDao.findById(firmwareId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.getFirmwareById(firmwareId));

        assertEquals("Firmware not found", thrown.getMessage(), "Incorrect message");
        verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
    }

    @Test
    void getAllFirmware_returnFirmwareList() {
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");

        Hardware firstHardware = Hardware.builder().id(1L).build();
        Hardware secondHardware = Hardware.builder().id(2L).build();

        Long firstFirmwareId = 1L;
        Firmware firstFirmware = Firmware.builder().id(firstFirmwareId).name("First Firmware")
                .firmwareType(firstFirmwareType).ociName("testrepo")
                .hardwareList(List.of(firstHardware)).build();
        FirmwareDto expectedFirstFirmwareDto = firmwareMapper.toDto(firstFirmware);

        Long secondFirmwareId = 2L;
        Firmware secondFirmware = Firmware.builder().id(secondFirmwareId).name("Second Firmware")
                .firmwareType(secondFirmwareType).ociName("testrepo")
                .hardwareList(List.of(firstHardware, secondHardware)).build();
        FirmwareDto expectedSecondFirmwareDto = firmwareMapper.toDto(secondFirmware);

        when(firmwareDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware, secondFirmware)));
        when(firmwareDao.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware)));
        when(firmwareDao.findAll(PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondFirmware)));

        List<FirmwareDto> receivedAllFirmwareList = firmwareService.getAllFirmware(0, 2);
        List<FirmwareDto> receivedFirstFirmwareList = firmwareService.getAllFirmware(0, 1);
        List<FirmwareDto> receivedSecondFirmwareList = firmwareService.getAllFirmware(1, 1);

        assertEquals(List.of(expectedFirstFirmwareDto), receivedFirstFirmwareList, "Incorrect first Firmware page");
        assertEquals(List.of(expectedSecondFirmwareDto), receivedSecondFirmwareList, "Incorrect second Firmware page");
        assertEquals(List.of(expectedFirstFirmwareDto, expectedSecondFirmwareDto), receivedAllFirmwareList, "Incorrect all Firmware page");
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 2));
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 1));
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(1, 1));
        verify(firmwareMapper, Mockito.times(6)).toDto(any(Firmware.class));
    }

    @Test
    void getAllFirmware_whenPageEmpty_returnEmptyList(){
        int page = 1;
        int pageSize = 10;

        when(firmwareDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<FirmwareDto> allFirmwareList = firmwareService.getAllFirmware(page, pageSize);

        assertEquals(new ArrayList<>(), allFirmwareList, "Incorrect all Firmware page");
        verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }


    @Test
    void getFirmwareTypes_returnTypes() {
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        FirmwareTypeDto firstFirmwareTypeDto = new FirmwareTypeDto(1, "APPLICATION");

        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");
        FirmwareTypeDto secondFirmwareTypeDto = new FirmwareTypeDto(2, "FPGA");

        List<FirmwareTypeDto> expectedFirmwareTypes = List.of(firstFirmwareTypeDto, secondFirmwareTypeDto);

        when(firmwareTypeDao.findAll()).thenReturn(List.of(firstFirmwareType, secondFirmwareType));

        List<FirmwareTypeDto> allFirmwareTypes = firmwareService.getFirmwareTypes();

        assertEquals(expectedFirmwareTypes, allFirmwareTypes, "Incorrect firmware types");
        verify(firmwareTypeDao, Mockito.times(1)).findAll();
        verify(firmwareTypeMapper, Mockito.times(expectedFirmwareTypes.size())).toDto(any(FirmwareType.class));
    }
}