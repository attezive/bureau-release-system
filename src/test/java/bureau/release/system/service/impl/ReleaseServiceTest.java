package bureau.release.system.service.impl;

import bureau.release.system.dal.*;
import bureau.release.system.model.*;
import bureau.release.system.service.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {
    @InjectMocks
    private ReleaseService releaseService;

    @Mock
    private ReleaseDao releaseDao;

    @Mock
    private ReleaseStatusDao releaseStatusDao;

    @Mock
    private FirmwareVersionDao firmwareVersionDao;

    @Mock
    private FirmwareDao firmwareDao;

    @Mock
    private MissionDao missionDao;

    @Mock
    private HardwareDao hardwareDao;

    private Long releaseId;
    private int missionId;
    private List<ReleaseContentDto> releaseContentList;
    private List<FirmwareVersion> firmwareVersionList;
    private Firmware firmware1;
    private Firmware firmware2;
    private Firmware firmware3;
    private Hardware hardware1;
    private Hardware hardware2;

    @BeforeEach
    void setUp() {
        releaseId = 1L;
        missionId = 1;
        firmware1 = Firmware.builder().id(1L).name("Firmware 1").build();
        firmware2 = Firmware.builder().id(2L).name("Firmware 2").build();
        firmware3 = Firmware.builder().id(3L).name("Firmware 3").build();

        hardware1 = Hardware.builder().id(1L).name("hardware 1")
                .firmwareSet(List.of(firmware1, firmware2)).build();
        hardware2 = Hardware.builder().id(2L).name("hardware 2")
                .firmwareSet(List.of(firmware3)).build();

        Release release = Release.builder().id(releaseId).build();

        FirmwareVersion firmwareVersion1 = FirmwareVersion.builder().firmwareVersion("v1")
                .firmware(firmware1).hardware(hardware1).release(release).build();
        FirmwareVersion firmwareVersion2 = FirmwareVersion.builder().firmwareVersion("v2")
                .firmware(firmware2).hardware(hardware1).release(release).build();
        FirmwareVersion firmwareVersion3 = FirmwareVersion.builder().firmwareVersion("v2")
                .firmware(firmware3).hardware(hardware2).release(release).build();
        firmwareVersionList = List.of(firmwareVersion1, firmwareVersion2, firmwareVersion3);

        ReleaseContentDto releaseContentDto1 = new ReleaseContentDto();
        releaseContentDto1.setHardwareId(hardware1.getId());
        releaseContentDto1.setFirmwareVersions(List.of(
                new FirmwareVersionDto(firmwareVersion1),
                new FirmwareVersionDto(firmwareVersion2)
        ));
        ReleaseContentDto releaseContentDto2 = new ReleaseContentDto();
        releaseContentDto2.setHardwareId(hardware2.getId());
        releaseContentDto2.setFirmwareVersions(List.of(new FirmwareVersionDto(firmwareVersion3)));

        releaseContentList = List.of(releaseContentDto1, releaseContentDto2);
    }

    @Test
    void createRelease() {
        ReleaseDto releaseDto = new ReleaseDto();
        releaseDto.setName("test");
        releaseDto.setOciName("testrepo");
        releaseDto.setMissionId(missionId);
        releaseDto.setReference("reference");
        releaseDto.setReleaseContent(releaseContentList);

        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release savedRelease = Release
                .builder()
                .id(releaseId)
                .name(releaseDto.getName())
                .status(releaseStatus)
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .mission(mission)
                .releaseDate(LocalDate.now())
                .build();

        Release release = Release
                .builder()
                .id(releaseId)
                .name(releaseDto.getName())
                .status(releaseStatus)
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();
        ReleaseDto correctReleaseDto = new ReleaseDto(release, releaseContentList);
        correctReleaseDto.setOriginId(releaseId);

        Mockito.when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        Mockito.when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        Mockito.when(releaseDao.save(ArgumentMatchers.any(Release.class))).thenReturn(savedRelease);

        Mockito.when(firmwareDao.findById(1L)).thenReturn(Optional.of(firmware1));
        Mockito.when(firmwareDao.findById(2L)).thenReturn(Optional.of(firmware2));
        Mockito.when(firmwareDao.findById(3L)).thenReturn(Optional.of(firmware3));
        Mockito.when(hardwareDao.findById(1L)).thenReturn(Optional.of(hardware1));
        Mockito.when(hardwareDao.findById(2L)).thenReturn(Optional.of(hardware2));

        Mockito.when(firmwareVersionDao.save(ArgumentMatchers.any(FirmwareVersion.class)))
                .thenReturn(new FirmwareVersion());

        ReleaseDto checkedReleaseDto = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, checkedReleaseDto, "Incorrect releaseDto");
    }

    @Test
    void createReleaseByOrigin() {
        ReleaseDto releaseDto = new ReleaseDto();
        releaseDto.setName("test");
        releaseDto.setOciName("testrepo");
        releaseDto.setMissionId(missionId);
        releaseDto.setReference("reference");
        releaseDto.setOriginId(releaseId-1);
        releaseDto.setReleaseContent(new ArrayList<>());

        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release savedRelease = Release
                .builder()
                .id(releaseId)
                .name(releaseDto.getName())
                .status(releaseStatus)
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .mission(mission)
                .releaseDate(LocalDate.now())
                .build();

        Release release = Release
                .builder()
                .id(releaseId)
                .name(releaseDto.getName())
                .status(releaseStatus)
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();
        ReleaseDto correctReleaseDto = new ReleaseDto(release, releaseContentList);
        correctReleaseDto.setOriginId(releaseId);

        Mockito.when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        Mockito.when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        Mockito.when(releaseDao.save(ArgumentMatchers.any(Release.class))).thenReturn(savedRelease);

        Mockito.when(firmwareVersionDao.save(ArgumentMatchers.any(FirmwareVersion.class)))
                .thenReturn(new FirmwareVersion());

        Mockito.when(releaseDao.findById(releaseId-1)).thenReturn(Optional.of(release));

        ReleaseDto checkedReleaseDto = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, checkedReleaseDto, "Incorrect releaseDto");
    }

    @Test
    void getAllReleases() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release firstRelease = Release
                .builder()
                .id(releaseId)
                .name("First Release")
                .status(releaseStatus)
                .ociName("repo")
                .reference("reference")
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();
        ReleaseDto firstReleaseDto = new ReleaseDto(firstRelease, releaseContentList);
        Release secondRelease = Release
                .builder()
                .id(releaseId+1)
                .name("First Release")
                .status(releaseStatus)
                .ociName("repo")
                .reference("reference")
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();
        ReleaseDto secondReleaseDto = new ReleaseDto(secondRelease, releaseContentList);

        Mockito.when(releaseDao.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstRelease)));
        Mockito.when(releaseDao.findAll(PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondRelease)));
        Mockito.when(releaseDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstRelease, secondRelease)));

        List<ReleaseDto> checkedFirstReleaseDtoLIst = releaseService.getAllReleases(0, 1, missionId);
        List<ReleaseDto> checkedSecondReleaseDtoLIst = releaseService.getAllReleases(1, 1, missionId);
        List<ReleaseDto> checkedAllReleaseDtoLIst = releaseService.getAllReleases(0, 2, missionId);

        assertEquals(List.of(firstReleaseDto), checkedFirstReleaseDtoLIst, "Incorrect firstReleaseDtoLIst");
        assertEquals(List.of(secondReleaseDto), checkedSecondReleaseDtoLIst, "Incorrect secondReleaseDtoLIst");
        assertEquals(List.of(firstReleaseDto, secondReleaseDto), checkedAllReleaseDtoLIst, "Incorrect allReleaseDtoLIst");
    }

    @Test
    void getAllReleasesEmptyPage(){
        int page = 1;
        int pageSize = 10;

        Mockito.when(releaseDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<ReleaseDto> allReleaseList = releaseService.getAllReleases(page, pageSize, 0);

        assertEquals(new ArrayList<>(), allReleaseList, "Incorrect Release page");
        Mockito.verify(releaseDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }

    @Test
    void getAllReleasesEmptyMission(){
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release firstRelease = Release
                .builder()
                .id(releaseId)
                .name("First Release")
                .status(releaseStatus)
                .ociName("repo")
                .reference("reference")
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();

        Mockito.when(releaseDao.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstRelease)));

        List<ReleaseDto> allReleaseList = releaseService.getAllReleases(0, 1, missionId+1);
        assertEquals(new ArrayList<>(), allReleaseList, "Incorrect Release Mission filter");
        Mockito.verify(releaseDao, Mockito.times(1)).findAll(PageRequest.of(0, 1));
    }

    @Test
    void getReleaseById() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release release = Release
                .builder()
                .id(releaseId)
                .name("Release")
                .status(releaseStatus)
                .ociName("repo")
                .reference("reference")
                .mission(mission)
                .releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList)
                .build();
        ReleaseDto releaseDto = new ReleaseDto(release, releaseContentList);

        Mockito.when(releaseDao.findById(releaseId)).thenReturn(Optional.of(release));

        ReleaseDto checkedReleaseDto = releaseService.getReleaseById(releaseId);

        assertEquals(releaseDto, checkedReleaseDto, "Incorrect releaseDto");
        Mockito.verify(releaseDao, Mockito.times(1)).findById(releaseId);
    }

    @Test
    void getReleaseByIdFailed() {
        Mockito.when(releaseDao.findById(releaseId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> releaseService.getReleaseById(releaseId));

        assertEquals("Release not found", thrown.getMessage(), "Incorrect message");
        Mockito.verify(releaseDao, Mockito.times(1)).findById(releaseId);
    }

    @Test
    void getReleaseStatuses() {
        int i = 1;
        List<ReleaseStatus> releaseStatusList = new ArrayList<>();
        for (ReleaseStatusDto releaseStatusDto : ReleaseStatusDto.values()) {
            ReleaseStatus releaseStatus = ReleaseStatus.builder().id(i++).name(releaseStatusDto.name()).build();
            releaseStatusList.add(releaseStatus);
        }

        Mockito.when(releaseStatusDao.findAll()).thenReturn(releaseStatusList);

        List<ReleaseStatus> checkedReleaseStatusList = releaseService.getReleaseStatuses();

        assertEquals(releaseStatusList, checkedReleaseStatusList, "Incorrect releaseStatusList");
        Mockito.verify(releaseStatusDao, Mockito.times(1)).findAll();
    }
}