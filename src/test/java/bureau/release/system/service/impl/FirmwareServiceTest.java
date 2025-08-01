package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.FirmwareTypeDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareType;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FirmwareServiceTest {
    @InjectMocks
    private FirmwareService firmwareService;

    @Mock
    private FirmwareDao firmwareDao;

    @Mock
    private FirmwareTypeDao firmwareTypeDao;

    @Test
    void createFirmware() {
        Long firmwareId = 1L;
        FirmwareDto firmwareDto = new FirmwareDto();
        firmwareDto.setName("Firmware");
        firmwareDto.setOciName("testOci");
        firmwareDto.setType("APPLICATION");

        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");

        Firmware firmware = Firmware
                .builder()
                .id(firmwareId)
                .name(firmwareDto.getName())
                .type(firmwareType)
                .ociName(firmwareDto.getOciName())
                .build();

        Mockito.when(firmwareTypeDao.findByName(firmwareType.getName())).thenReturn(Optional.of(firmwareType));
        Mockito.when(firmwareDao.save(ArgumentMatchers.any(Firmware.class))).thenReturn(firmware);

        FirmwareDto firmwareDtoResult = firmwareService.createFirmware(firmwareDto);

        assertEquals(firmware.getId(), firmwareDtoResult.getId(), "Incorrect firmware id");
        assertEquals(firmware.getName(), firmwareDtoResult.getName(), "Incorrect name");
        assertEquals(firmware.getOciName(), firmwareDtoResult.getOciName(), "Incorrect OCI name");
        assertEquals(firmware.getType().getName(), firmwareDtoResult.getType(), "Incorrect type");
        Mockito.verify(firmwareTypeDao, Mockito.times(1)).findByName(firmwareType.getName());
        Mockito.verify(firmwareDao, Mockito.times(1)).save(ArgumentMatchers.any(Firmware.class));
    }

    @Test
    void createFirmwareFailed() {
        FirmwareDto firmwareDto = new FirmwareDto();
        firmwareDto.setName("Firmware");
        firmwareDto.setOciName("testOci");
        firmwareDto.setType("NOT_FOUND");

        Mockito.when(firmwareTypeDao.findByName("NOT_FOUND")).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.createFirmware(firmwareDto));

        assertEquals("Type not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(firmwareTypeDao, Mockito.times(1)).findByName("NOT_FOUND");
    }

    @Test
    void getFirmwareById() {
        Long firmwareId = 1L;
        FirmwareType firmwareType = new FirmwareType(1, "APPLICATION");
        Long hardwareId = 1L;
        Hardware hardware = Hardware.builder().id(hardwareId).build();
        Firmware firmware = Firmware
                .builder()
                .id(firmwareId)
                .name("Firmware")
                .type(firmwareType)
                .ociName("testrepo")
                .hardwareSet(List.of(hardware))
                .build();
        FirmwareDto correctFirmwareDto = new FirmwareDto(firmware, List.of(hardwareId));

        Mockito.when(firmwareDao.findById(firmwareId)).thenReturn(Optional.of(firmware));

        FirmwareDto firmwareDto = firmwareService.getFirmwareById(firmwareId);

        assertEquals(correctFirmwareDto, firmwareDto, "Incorrect firmware");
        Mockito.verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
    }

    @Test
    void getFirmwareByIdFailed() {
        Long firmwareId = 1L;
        Mockito.when(firmwareDao.findById(firmwareId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> firmwareService.getFirmwareById(firmwareId));

        assertEquals("Firmware not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(firmwareDao, Mockito.times(1)).findById(firmwareId);
    }

    @Test
    void getAllFirmware() {
        Long firstFirmwareId = 1L;
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        Long firstHardwareId = 1L;
        Hardware firstHardware = Hardware.builder().id(firstHardwareId).build();
        Firmware firstFirmware = Firmware
                .builder()
                .id(firstFirmwareId)
                .name("First Firmware")
                .type(firstFirmwareType)
                .ociName("testrepo")
                .hardwareSet(List.of(firstHardware))
                .build();
        FirmwareDto firstFirmwareDto = new FirmwareDto(firstFirmware, List.of(firstHardwareId));
        Long secondFirmwareId = 2L;
        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");
        Long secondHardwareId = 2L;
        Hardware secondHardware = Hardware.builder().id(secondHardwareId).build();
        Firmware secondFirmware = Firmware
                .builder()
                .id(secondFirmwareId)
                .name("Second Firmware")
                .type(secondFirmwareType)
                .ociName("testrepo")
                .hardwareSet(List.of(firstHardware, secondHardware))
                .build();
        FirmwareDto secondFirmwareDto = new FirmwareDto(secondFirmware, List.of(firstHardwareId, secondHardwareId));

        Mockito.when(firmwareDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware, secondFirmware)));
        Mockito.when(firmwareDao.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstFirmware)));
        Mockito.when(firmwareDao.findAll(PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondFirmware)));

        List<FirmwareDto> allFirmwareList = firmwareService.getAllFirmware(0, 2);
        List<FirmwareDto> firstFirmwareList = firmwareService.getAllFirmware(0, 1);
        List<FirmwareDto> secondFirmwareList = firmwareService.getAllFirmware(1, 1);

        assertEquals(List.of(firstFirmwareDto), firstFirmwareList, "Incorrect first Firmware page");
        assertEquals(List.of(secondFirmwareDto), secondFirmwareList, "Incorrect second Firmware page");
        assertEquals(List.of(firstFirmwareDto, secondFirmwareDto), allFirmwareList, "Incorrect all Firmware page");
        Mockito.verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 2));
        Mockito.verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(0, 1));
        Mockito.verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(1, 1));
    }

    @Test
    void getAllFirmwareEmptyList(){
        int page = 1;
        int pageSize = 10;

        Mockito.when(firmwareDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<FirmwareDto>  allFirmwareList = firmwareService.getAllFirmware(page, pageSize);

        assertEquals(new ArrayList<>(), allFirmwareList, "Incorrect all Firmware page");
        Mockito.verify(firmwareDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }


    @Test
    void getFirmwareTypes() {
        FirmwareType firstFirmwareType = new FirmwareType(1, "APPLICATION");
        FirmwareType secondFirmwareType = new FirmwareType(2, "FPGA");
        List<FirmwareTypeDto> firmwareTypes = List.of(
                new FirmwareTypeDto(firstFirmwareType),
                new FirmwareTypeDto(secondFirmwareType));

        Mockito.when(firmwareTypeDao.findAll()).thenReturn(List.of(firstFirmwareType, secondFirmwareType));

        List<FirmwareTypeDto> allFirmwareTypes = firmwareService.getFirmwareTypes();

        assertEquals(firmwareTypes, allFirmwareTypes, "Incorrect firmware types");
        Mockito.verify(firmwareTypeDao, Mockito.times(1)).findAll();
    }
}