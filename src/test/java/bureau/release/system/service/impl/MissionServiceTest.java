package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.MissionDto;
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
class MissionServiceTest {
    @InjectMocks
    private MissionService missionService;

    @Mock
    private MissionDao missionDao;

    @Mock
    private HardwareDao hardwareDao;

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
    void createMission() {
        Mockito.when(hardwareDao.findById(firstHardwareId)).thenReturn(Optional.of(firstHardware));
        Mockito.when(hardwareDao.findById(secondHardwareId)).thenReturn(Optional.of(secondHardware));

        MissionDto missionDto = new MissionDto("Mission", hardwareIdsSet);

        Mission mission = Mission
                .builder()
                .id(missionId)
                .name(missionDto.getName())
                .hardwareSet(hardwareSet)
                .build();
        Mockito.when(missionDao.save(ArgumentMatchers.any(Mission.class))).thenReturn(mission);

        MissionDto missionDtoResult = missionService.createMission(missionDto);

        assertEquals(hardwareIdsSet, missionDtoResult.getHardwareIds(), "Incorrect hardware ids set");
        assertEquals(mission.getId(), missionDtoResult.getId(), "Incorrect mission id");
        assertEquals(missionDto.getName(), missionDtoResult.getName(), "Incorrect mission name");
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(firstHardwareId);
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(secondHardwareId);
        Mockito.verify(missionDao, Mockito.times(1)).save(ArgumentMatchers.any(Mission.class));
    }

    @Test
    void createMissionFailed() {
        Mockito.when(hardwareDao.findById(firstHardwareId)).thenReturn(Optional.empty());

        MissionDto missionDto = new MissionDto("name", hardwareIdsSet);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> missionService.createMission(missionDto));

        assertEquals("Hardware not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(firstHardwareId);
    }

    @Test
    void getAllMissions() {
        Mission firstMission = Mission
                .builder()
                .id(missionId)
                .name("First Mission")
                .hardwareSet(hardwareSet)
                .build();
        List<Hardware> uniqueHardwareSet = List.of(firstHardware);
        Mission secondMission = Mission
                .builder()
                .id(missionId + 1)
                .name("Second Mission")
                .hardwareSet(uniqueHardwareSet)
                .build();
        List<MissionDto> missions = List.of(
                new MissionDto(firstMission, hardwareIdsSet),
                new MissionDto(secondMission, List.of(firstHardwareId)));

        Mockito.when(missionDao.findAll()).thenReturn(List.of(firstMission, secondMission));

        List<MissionDto> allMissions = missionService.getAllMissions();

        assertEquals(allMissions, missions, "Incorrect equals missions list");
        Mockito.verify(missionDao, Mockito.times(1)).findAll();
    }

    @Test
    void getEmptyMissions() {
        Mockito.when(missionDao.findAll()).thenReturn(new ArrayList<>());

        List<MissionDto> missions = missionService.getAllMissions();

        assertEquals(new ArrayList<>(), missions, "Incorrect equals empty missions list");
        Mockito.verify(missionDao, Mockito.times(1)).findAll();
    }

    @Test
    void getMissionById() {
        Mission mission = Mission
                .builder()
                .id(missionId)
                .name("First Mission")
                .hardwareSet(hardwareSet)
                .build();
        MissionDto correctMissionDto = new MissionDto(mission, hardwareIdsSet);

        Mockito.when(missionDao.findById(missionId)).thenReturn(Optional.of(mission));

        MissionDto missionDto = missionService.getMissionById(missionId);

        assertEquals(correctMissionDto, missionDto, "Incorrect mission");
        Mockito.verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void getMissionByIdFailed() {
        Mockito.when(missionDao.findById(missionId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> missionService.getMissionById(missionId));

        assertEquals("Mission not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(missionDao, Mockito.times(1)).findById(missionId);
    }

    @Test
    void deleteMission() {
        missionService.deleteMission(ArgumentMatchers.anyInt());
        Mockito.verify(missionDao, Mockito.times(1)).deleteById(ArgumentMatchers.anyInt());
    }
}