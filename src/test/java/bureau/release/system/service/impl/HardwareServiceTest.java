package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HardwareServiceTest {
    @InjectMocks
    private HardwareService hardwareService;

    @Mock
    private MissionDao missionDao;

    @Mock
    private HardwareDao hardwareDao;

    @Mock
    private FirmwareDao firmwareDao;

    private List<Long> firmwareIdsSet;
    private Long firstFirmwareId;
    private Long secondFirmwareId;
    private List<Firmware> firmwareSet;
    private Long hardwareId;
    private Firmware firstFirmware;
    private Firmware secondFirmware;

    @BeforeEach
    void setUp() {
        firmwareIdsSet = new ArrayList<>();
        firstFirmwareId = 1L;
        secondFirmwareId = 2L;
        firmwareIdsSet.add(firstFirmwareId);
        firmwareIdsSet.add(secondFirmwareId);

        firmwareSet = new ArrayList<>();
        List<Hardware> hardwareSet = new ArrayList<>();
        hardwareId = 1L;
        hardwareSet.add(Hardware.builder().id(hardwareId).build());
        firstFirmware = Firmware.builder().id(firstFirmwareId).hardwareSet(hardwareSet).build();
        secondFirmware = Firmware.builder().id(secondFirmwareId).hardwareSet(hardwareSet).build();
        firmwareSet.add(firstFirmware);
        firmwareSet.add(secondFirmware);
    }

    @Test
    void createHardware() {
        Mockito.when(firmwareDao.findById(firstFirmwareId)).thenReturn(Optional.of(firstFirmware));
        Mockito.when(firmwareDao.findById(secondFirmwareId)).thenReturn(Optional.of(secondFirmware));

        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setName("Hardware");
        hardwareDto.setFirmwareIds(firmwareIdsSet);

        Hardware hardware = Hardware
                .builder()
                .id(hardwareId)
                .name(hardwareDto.getName())
                .firmwareSet(firmwareSet)
                .build();
        Mockito.when(hardwareDao.save(ArgumentMatchers.any(Hardware.class))).thenReturn(hardware);

        HardwareDto hardwareDtoResult = hardwareService.createHardware(hardwareDto);

        assertEquals(firmwareIdsSet, hardwareDtoResult.getFirmwareIds(), "Incorrect firmware ids set");
        assertEquals(hardware.getId(), hardwareDtoResult.getId(), "Incorrect hardware id");
        assertEquals(hardware.getName(), hardwareDtoResult.getName(), "Incorrect hardware name");
        Mockito.verify(firmwareDao, Mockito.times(1)).findById(firstFirmwareId);
        Mockito.verify(firmwareDao, Mockito.times(1)).findById(secondFirmwareId);
        Mockito.verify(hardwareDao, Mockito.times(1)).save(ArgumentMatchers.any(Hardware.class));
    }

    @Test
    void createHardwareFailed() {
        Mockito.when(firmwareDao.findById(firstFirmwareId)).thenReturn(Optional.empty());

        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setName("Hardware");
        hardwareDto.setFirmwareIds(firmwareIdsSet);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.createHardware(hardwareDto));

        assertEquals("Firmware not found", thrown.getMessage(), "Incorrect message");
    }

    @Test
    void getAllHardware() {
        Integer firstMissionId = 1;
        Mission firstMission = Mission.builder().id(firstMissionId).build();
        Hardware firstHardware = Hardware
                .builder()
                .id(hardwareId)
                .name("First Hardware")
                .missions(List.of(firstMission))
                .firmwareSet(firmwareSet)
                .build();
        Integer secondMissionId = 2;
        Mission secondMission = Mission.builder().id(secondMissionId).build();
        Hardware secondHardware = Hardware
                .builder()
                .id(hardwareId + 1)
                .name("Second Hardware")
                .missions(List.of(firstMission, secondMission))
                .firmwareSet(List.of(firstFirmware))
                .build();
        Hardware thirdHardware = Hardware
                .builder()
                .id(hardwareId + 2)
                .name("Third Hardware")
                .missions(new ArrayList<>())
                .firmwareSet(List.of(secondFirmware))
                .build();
        List<HardwareDto> hardwareList = List.of(
                new HardwareDto(firstHardware, firmwareIdsSet, List.of(firstMissionId)),
                new HardwareDto(secondHardware, List.of(firstFirmwareId), List.of(firstMissionId, secondMissionId)),
                new HardwareDto(thirdHardware, List.of(secondFirmwareId)));

        Mockito.when(hardwareDao.findAll()).thenReturn(List.of(firstHardware, secondHardware, thirdHardware));

        List<HardwareDto> allHardwareList = hardwareService.getAllHardware();

        assertEquals(hardwareList, allHardwareList, "Incorrect hardware list");
        Mockito.verify(hardwareDao, Mockito.times(1)).findAll();
    }

    @Test
    void getEmptyHardware() {
        Mockito.when(hardwareDao.findAll()).thenReturn(new ArrayList<>());

        List<HardwareDto> allHardwareList = hardwareService.getAllHardware();

        assertEquals(allHardwareList, List.of(), "Incorrect hardware list");
        Mockito.verify(hardwareDao, Mockito.times(1)).findAll();
    }

    @Test
    void getHardwareByMissionId() {
        Integer missionId = 1;
        Mission mission = Mission.builder().id(missionId).hardwareSet(new ArrayList<>()).build();
        Hardware firstHardware = Hardware
                .builder()
                .id(hardwareId)
                .name("First Hardware")
                .missions(List.of(mission))
                .firmwareSet(firmwareSet)
                .build();
        mission.getHardwareSet().add(firstHardware);
        Hardware secondHardware = Hardware
                .builder()
                .id(hardwareId + 1)
                .name("Second Hardware")
                .missions(List.of(mission))
                .firmwareSet(List.of(firstFirmware))
                .build();
        mission.getHardwareSet().add(secondHardware);
        List<HardwareDto> hardwareList = List.of(
                new HardwareDto(firstHardware, firmwareIdsSet, List.of(missionId)),
                new HardwareDto(secondHardware, List.of(firstFirmwareId), List.of(missionId)));

        Mockito.when(missionDao.findById(missionId)).thenReturn(Optional.of(mission));

        List<HardwareDto> allHardwareList = hardwareService.getHardwareByMissionId(missionId);

        assertEquals(hardwareList, allHardwareList, "Incorrect hardware list");
        Mockito.verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void getHardwareByMissionIdFailed() {
        int missionId = 1;
        Mockito.when(missionDao.findById(missionId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.getHardwareByMissionId(missionId));

        assertEquals("Mission not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void getHardwareById() {
        Integer missionId = 1;
        Mission mission = Mission.builder().id(missionId).build();
        Hardware hardware = Hardware
                .builder()
                .id(hardwareId)
                .name("Hardware")
                .missions(List.of(mission))
                .firmwareSet(firmwareSet)
                .build();
        HardwareDto correctHardwareDto = new HardwareDto(hardware, firmwareIdsSet, List.of(missionId));

        Mockito.when(hardwareDao.findById(hardwareId)).thenReturn(Optional.of(hardware));

        HardwareDto hardwareDto = hardwareService.getHardwareById(hardwareId);

        assertEquals(correctHardwareDto, hardwareDto, "Incorrect hardware");
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(hardwareId);
    }

    @Test
    void getHardwareByIdFailed() {
        Mockito.when(hardwareDao.findById(hardwareId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.getHardwareById(hardwareId));

        assertEquals("Hardware not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(hardwareId);
    }
}