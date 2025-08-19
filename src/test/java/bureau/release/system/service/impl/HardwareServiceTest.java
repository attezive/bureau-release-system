package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.mapping.HardwareMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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

    @Spy
    private HardwareMapper hardwareMapper = Mappers.getMapper(HardwareMapper.class);


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
        firstFirmware = Firmware.builder().id(firstFirmwareId).hardwareList(hardwareSet).build();
        secondFirmware = Firmware.builder().id(secondFirmwareId).hardwareList(hardwareSet).build();
        firmwareSet.add(firstFirmware);
        firmwareSet.add(secondFirmware);
    }

    @Test
    void createHardware_createAndReturn() {
        when(firmwareDao.findById(firstFirmwareId)).thenReturn(Optional.of(firstFirmware));
        when(firmwareDao.findById(secondFirmwareId)).thenReturn(Optional.of(secondFirmware));

        HardwareDto requestHardwareDto = new HardwareDto();
        requestHardwareDto.setName("Hardware");
        requestHardwareDto.setFirmwareIds(firmwareIdsSet);

        when(hardwareDao.save(eq(hardwareMapper.toEntity(requestHardwareDto, firmwareSet, new ArrayList<>()))))
                .thenAnswer(inv -> {
                    Hardware hardware = inv.getArgument(0, Hardware.class);
                    hardware.setId(hardwareId);
                    return hardware;
                });

        HardwareDto receivedHardwareDto = hardwareService.createHardware(requestHardwareDto);

        assertEquals(firmwareIdsSet, receivedHardwareDto.getFirmwareIds(), "Incorrect firmware ids set");
        assertEquals(hardwareId, receivedHardwareDto.getId(), "Incorrect hardware id");
        assertEquals(requestHardwareDto.getName(), receivedHardwareDto.getName(), "Incorrect hardware name");
        verify(firmwareDao, Mockito.times(1)).findById(firstFirmwareId);
        verify(firmwareDao, Mockito.times(1)).findById(secondFirmwareId);
        verify(hardwareMapper, Mockito.times(1)).toDto(any(Hardware.class));
        verify(hardwareMapper, Mockito.times(2))
                .toEntity(requestHardwareDto, firmwareSet, new ArrayList<>());
        verify(hardwareDao, Mockito.times(1)).save(any(Hardware.class));
    }

    @Test
    void createHardware_failedByUnknownFirmware_throwsEntityNotFoundException() {
        when(firmwareDao.findById(firstFirmwareId)).thenReturn(Optional.empty());

        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setName("Hardware");
        hardwareDto.setFirmwareIds(firmwareIdsSet);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.createHardware(hardwareDto));

        assertEquals("Firmware not found", thrown.getMessage(), "Incorrect message");
        verify(hardwareMapper, Mockito.times(0))
                .toEntity(hardwareDto, firmwareSet, new ArrayList<>());
    }

    @Test
    void getAllHardware_returnHardwareList() {
        Mission firstMission = Mission.builder().id(1).build();
        Mission secondMission = Mission.builder().id(2).build();

        Hardware firstHardware = Hardware.builder().id(hardwareId).name("First Hardware")
                .missions(List.of(firstMission)).firmwareList(firmwareSet).build();
        HardwareDto firstHardwareDto = hardwareMapper.toDto(firstHardware);

        Hardware secondHardware = Hardware.builder().id(hardwareId + 1).name("Second Hardware")
                .missions(List.of(firstMission, secondMission)).firmwareList(List.of(firstFirmware)).build();
        HardwareDto secondHardwareDto = hardwareMapper.toDto(secondHardware);

        Hardware thirdHardware = Hardware.builder().id(hardwareId + 2).name("Third Hardware")
                .missions(new ArrayList<>()).firmwareList(List.of(secondFirmware)).build();
        HardwareDto thirdHardwareDto = hardwareMapper.toDto(thirdHardware);

        List<HardwareDto> expectedHardwareList = List.of(firstHardwareDto, secondHardwareDto, thirdHardwareDto);

        when(hardwareDao.findAll()).thenReturn(List.of(firstHardware, secondHardware, thirdHardware));

        List<HardwareDto> allHardwareList = hardwareService.getAllHardware();

        assertEquals(expectedHardwareList, allHardwareList, "Incorrect hardware list");
        verify(hardwareDao, Mockito.times(1)).findAll();
        verify(hardwareMapper, Mockito.times(6)).toDto(any(Hardware.class));
    }

    @Test
    void getAllHardware_whenHardwareEmpty_returnEmptyList() {
        when(hardwareDao.findAll()).thenReturn(new ArrayList<>());

        List<HardwareDto> allHardwareList = hardwareService.getAllHardware();

        assertEquals(allHardwareList, List.of(), "Incorrect hardware list");
        verify(hardwareDao, Mockito.times(1)).findAll();
    }

    @Test
    void getHardwareByMissionId_returnHardwareList() {
        Integer missionId = 1;
        Mission mission = Mission.builder().id(missionId).hardwareList(new ArrayList<>()).build();

        Hardware firstHardware = Hardware.builder().id(hardwareId).name("First Hardware")
                .missions(List.of(mission)).firmwareList(firmwareSet).build();
        HardwareDto firstHardwareDto = hardwareMapper.toDto(firstHardware);
        mission.getHardwareList().add(firstHardware);

        Hardware secondHardware = Hardware.builder().id(hardwareId + 1).name("Second Hardware")
                .missions(List.of(mission)).firmwareList(List.of(firstFirmware)).build();
        HardwareDto secondHardwareDto = hardwareMapper.toDto(secondHardware);
        mission.getHardwareList().add(secondHardware);

        List<HardwareDto> expectedHardwareList = List.of(firstHardwareDto, secondHardwareDto);

        when(missionDao.findById(missionId)).thenReturn(Optional.of(mission));

        List<HardwareDto> receivedHardwareList = hardwareService.getHardwareByMissionId(missionId);

        assertEquals(expectedHardwareList, receivedHardwareList, "Incorrect hardware list");
        verify(missionDao, Mockito.times(1)).findById(missionId);
        verify(hardwareMapper, Mockito.times(4)).toDto(any(Hardware.class));
    }

    @Test
    void getHardwareByMissionId_failedByUnknownMission_throwsEntityNotFoundException() {
        int missionId = 1;
        when(missionDao.findById(missionId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.getHardwareByMissionId(missionId));

        assertEquals("Mission not found", thrown.getMessage(), "Incorrect message");
        verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void getHardwareById_returnHardware() {
        Mission mission = Mission.builder().id(1).build();

        Hardware hardware = Hardware.builder().id(hardwareId).name("Hardware")
                .missions(List.of(mission)).firmwareList(firmwareSet).build();
        HardwareDto expectedHardwareDto = hardwareMapper.toDto(hardware);

        when(hardwareDao.findById(hardwareId)).thenReturn(Optional.of(hardware));

        HardwareDto receivedhardwareDto = hardwareService.getHardwareById(hardwareId);

        assertEquals(expectedHardwareDto, receivedhardwareDto, "Incorrect hardware");
        verify(hardwareDao, Mockito.times(1)).findById(hardwareId);
    }

    @Test
    void getHardwareById_failedByUnknownId_throwsEntityNotFoundException() {
        when(hardwareDao.findById(hardwareId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> hardwareService.getHardwareById(hardwareId));

        assertEquals("Hardware not found", thrown.getMessage(), "Incorrect message");
        verify(hardwareDao, Mockito.times(1)).findById(hardwareId);
    }
}