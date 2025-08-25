package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.mapping.MissionMapper;
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
class MissionServiceTest {
    @InjectMocks
    private MissionService missionService;

    @Mock
    private MissionDao missionDao;

    @Mock
    private HardwareDao hardwareDao;

    @Spy
    private MissionMapper missionMapper = Mappers.getMapper(MissionMapper.class);

    private List<Long> hardwareIdsSet;
    private Long firstHardwareId;
    private Long secondHardwareId;
    private List<Hardware> hardwareSet;
    private Integer missionId;
    private Hardware firstHardware;
    private Hardware secondHardware;

    @BeforeEach
    void setup() {
        hardwareIdsSet = new ArrayList<>();
        firstHardwareId = 1L;
        secondHardwareId = 2L;
        hardwareIdsSet.add(firstHardwareId);
        hardwareIdsSet.add(secondHardwareId);

        hardwareSet = new ArrayList<>();
        List<Mission> missionSet = new ArrayList<>();
        missionId = 1;
        missionSet.add(Mission.builder().id(missionId).build());
        firstHardware = Hardware.builder().id(firstHardwareId).missions(missionSet).build();
        secondHardware = Hardware.builder().id(secondHardwareId).missions(missionSet).build();
        hardwareSet.add(firstHardware);
        hardwareSet.add(secondHardware);
    }

    @Test
    void createMission_createAndReturn() {
        when(hardwareDao.findById(firstHardwareId)).thenReturn(Optional.of(firstHardware));
        when(hardwareDao.findById(secondHardwareId)).thenReturn(Optional.of(secondHardware));

        MissionDto requestMissionDto = new MissionDto();
        requestMissionDto.setName("Mission");
        requestMissionDto.setHardwareIds(hardwareIdsSet);

        when(missionDao.save(eq(missionMapper.toEntity(requestMissionDto, hardwareSet)))).thenAnswer(inv -> {
            Mission mission = inv.getArgument(0, Mission.class);
            mission.setId(missionId);
            return mission;
        });

        MissionDto receivedMissionDto = missionService.createMission(requestMissionDto);

        assertEquals(hardwareIdsSet, receivedMissionDto.getHardwareIds(), "Incorrect hardware ids set");
        assertEquals(missionId, receivedMissionDto.getId(), "Incorrect mission id");
        assertEquals(requestMissionDto.getName(), receivedMissionDto.getName(), "Incorrect mission name");
        verify(hardwareDao, Mockito.times(1)).findById(firstHardwareId);
        verify(hardwareDao, Mockito.times(1)).findById(secondHardwareId);
        verify(missionMapper, Mockito.times(1)).toDto(any(Mission.class));
        verify(missionMapper, Mockito.times(2)).toEntity(requestMissionDto, hardwareSet);
        verify(missionDao, Mockito.times(1)).save(any(Mission.class));
    }

    @Test
    void createMission_failedByUnknowHardware_throwsEntityNotFoundException() {
        when(hardwareDao.findById(firstHardwareId)).thenReturn(Optional.empty());

        MissionDto requestMissionDto = new MissionDto();
        requestMissionDto.setName("Mission");
        requestMissionDto.setHardwareIds(hardwareIdsSet);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> missionService.createMission(requestMissionDto));

        assertEquals("Hardware not found", thrown.getMessage(), "Incorrect message");
        verify(hardwareDao, Mockito.times(1)).findById(firstHardwareId);
        verify(missionMapper, Mockito.times(0)).toEntity(requestMissionDto, hardwareSet);
    }

    @Test
    void getAllMissions_returnMissions() {
        Mission firstMission = Mission.builder().id(missionId).name("First Mission")
                .hardwareList(hardwareSet).build();
        List<Hardware> uniqueHardwareSet = List.of(firstHardware);
        MissionDto firstMissionDto = missionMapper.toDto(firstMission);

        Mission secondMission = Mission.builder().id(missionId + 1).name("Second Mission")
                .hardwareList(uniqueHardwareSet).build();
        MissionDto secondMissionDto = missionMapper.toDto(secondMission);

        List<MissionDto> expectedMissions = List.of(firstMissionDto, secondMissionDto);

        when(missionDao.findAll()).thenReturn(List.of(firstMission, secondMission));

        List<MissionDto> receivedMissions = missionService.getAllMissions();

        assertEquals(receivedMissions, expectedMissions, "Incorrect equals missions list");
        verify(missionDao, Mockito.times(1)).findAll();
        verify(missionMapper, Mockito.times(4)).toDto(any(Mission.class));
    }

    @Test
    void getAllMissions_whenMissionsEmpty_thenReturnMissions() {
        when(missionDao.findAll()).thenReturn(new ArrayList<>());

        List<MissionDto> missions = missionService.getAllMissions();

        assertEquals(new ArrayList<>(), missions, "Incorrect equals empty missions list");
        verify(missionDao, Mockito.times(1)).findAll();
    }

    @Test
    void getMissionById_returnMissions() {
        Mission mission = Mission.builder().id(missionId).name("First Mission").hardwareList(hardwareSet).build();
        MissionDto expectedMissionDto = missionMapper.toDto(mission);

        when(missionDao.findById(missionId)).thenReturn(Optional.of(mission));

        MissionDto receivedMissionDto = missionService.getMissionById(missionId);

        assertEquals(expectedMissionDto, receivedMissionDto, "Incorrect mission");
        verify(missionDao, Mockito.times(1)).findById(missionId);
        verify(missionMapper, Mockito.times(2)).toDto(mission);
    }

    @Test
    void getMissionById_failedByUnknownId_throwsEntityNotFoundException() {
        when(missionDao.findById(missionId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> missionService.getMissionById(missionId));

        assertEquals("Mission not found", thrown.getMessage(), "Incorrect message");
        verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void deleteMission() {
        missionService.deleteMission(anyInt());
        verify(missionDao, Mockito.times(1)).deleteById(anyInt());
    }
}