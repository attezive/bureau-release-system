package bureau.release.system.service.impl;

import bureau.release.system.dal.*;
import bureau.release.system.model.*;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.ArtifactUploader;
import bureau.release.system.service.dto.*;
import bureau.release.system.service.mapping.FirmwareVersionMapper;
import bureau.release.system.service.mapping.FirmwareVersionMapperImpl;
import bureau.release.system.service.mapping.ReleaseMapper;
import bureau.release.system.service.mapping.ReleaseMapperImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(classes = {
        ReleaseService.class,
        FirmwareVersionMapperImpl.class,
        ReleaseMapperImpl.class,
})
class ReleaseServiceTest {
    @Autowired
    private ReleaseService releaseService;

    @MockitoBean
    private ReleaseDao releaseDao;

    @MockitoBean
    private ReleaseStatusDao releaseStatusDao;

    @MockitoBean
    private FirmwareVersionDao firmwareVersionDao;

    @MockitoBean
    private FirmwareDao firmwareDao;

    @MockitoBean
    private MissionDao missionDao;

    @MockitoBean
    private HardwareDao hardwareDao;

    @MockitoBean
    private ArtifactDownloader artifactDownloader;

    @MockitoBean
    private ArtifactUploader artifactUploader;

    @MockitoSpyBean
    private FirmwareVersionMapper firmwareVersionMapper;

    @MockitoSpyBean
    private ReleaseMapper releaseMapper;

    private Long releaseId;
    private int missionId;
    private List<ReleaseContentDto> releaseContentList;
    private List<FirmwareVersion> firmwareVersionList;
    private Firmware firstFirmware;
    private Firmware secondFirmware;
    private Firmware thirdFirmware;
    private Hardware firstHardware;
    private Hardware secondHardware;

    @BeforeEach
    void setUp() {
        releaseId = 1L;
        missionId = 1;
        firstFirmware = Firmware.builder().id(1L).name("Firmware 1").build();
        secondFirmware = Firmware.builder().id(2L).name("Firmware 2").build();
        thirdFirmware = Firmware.builder().id(3L).name("Firmware 3").build();

        firstHardware = Hardware.builder().id(1L).name("hardware 1")
                .firmwareList(List.of(firstFirmware, secondFirmware)).build();
        secondHardware = Hardware.builder().id(2L).name("hardware 2")
                .firmwareList(List.of(thirdFirmware)).build();

        Release release = Release.builder().id(releaseId).build();

        FirmwareVersion firstFirmwareVersion = FirmwareVersion.builder().firmwareVersion("v1")
                .firmware(firstFirmware).hardware(firstHardware).release(release).build();
        FirmwareVersionDto firstFirmwareVersionDto = new FirmwareVersionDto(null,
                firstFirmwareVersion.getFirmwareVersion(), firstFirmware.getId(),
                release.getId(), firstHardware.getId());

        FirmwareVersion secondFirmwareVersion = FirmwareVersion.builder().firmwareVersion("v2")
                .firmware(secondFirmware).hardware(firstHardware).release(release).build();
        FirmwareVersionDto secondFirmwareVersionDto = new FirmwareVersionDto(null,
                secondFirmwareVersion.getFirmwareVersion(), secondFirmware.getId(),
                release.getId(), firstHardware.getId());

        FirmwareVersion thirdFirmwareVersion = FirmwareVersion.builder().firmwareVersion("v2")
                .firmware(thirdFirmware).hardware(secondHardware).release(release).build();
        FirmwareVersionDto thirdFirmwareVersionDto = new FirmwareVersionDto(null,
                thirdFirmwareVersion.getFirmwareVersion(), thirdFirmware.getId(),
                release.getId(), secondHardware.getId());

        firmwareVersionList = List.of(firstFirmwareVersion, secondFirmwareVersion, thirdFirmwareVersion);

        ReleaseContentDto firstReleaseContentDto = new ReleaseContentDto();
        firstReleaseContentDto.setHardwareId(firstHardware.getId());
        firstReleaseContentDto.setFirmwareVersions(List.of(firstFirmwareVersionDto, secondFirmwareVersionDto));
        ReleaseContentDto secondReleaseContentDto = new ReleaseContentDto();
        secondReleaseContentDto.setHardwareId(secondHardware.getId());
        secondReleaseContentDto.setFirmwareVersions(List.of(thirdFirmwareVersionDto));

        releaseContentList = List.of(firstReleaseContentDto, secondReleaseContentDto);
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

        ReleaseDto correctReleaseDto = new ReleaseDto(releaseId, releaseDto.getName(), LocalDate.now(),
                releaseDto.getOciName(), releaseDto.getReference(), null,
                ReleaseStatusDto.valueOf(releaseStatus.getName()), releaseId, missionId,
                releaseContentList);

        when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        when(releaseDao.save(any(Release.class))).thenAnswer(inv -> {
            Release passedRelease = inv.getArgument(0, Release.class);
            passedRelease.setId(releaseId);
            return passedRelease;
        });

        when(firmwareDao.findById(1L)).thenReturn(Optional.of(firstFirmware));
        when(firmwareDao.findById(2L)).thenReturn(Optional.of(secondFirmware));
        when(firmwareDao.findById(3L)).thenReturn(Optional.of(thirdFirmware));
        when(hardwareDao.findById(1L)).thenReturn(Optional.of(firstHardware));
        when(hardwareDao.findById(2L)).thenReturn(Optional.of(secondHardware));

        when(firmwareVersionDao.save(isNotNull())).thenReturn(null);

        ReleaseDto releaseDtoResult = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, releaseDtoResult, "Incorrect releaseDto");
        verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        verify(releaseMapper, Mockito.times(1)).toEntity(releaseDto, releaseStatus, mission);
        verify(firmwareDao, Mockito.times(1)).findById(1L);
        verify(firmwareDao, Mockito.times(1)).findById(2L);
        verify(firmwareDao, Mockito.times(1)).findById(3L);
        verify(hardwareDao, Mockito.times(2)).findById(1L);
        verify(hardwareDao, Mockito.times(1)).findById(2L);
        verify(firmwareVersionDao, Mockito.times(3)).save(isNotNull());
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

        Release release = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(firmwareVersionList).build();

        ReleaseDto correctReleaseDto = new ReleaseDto(release.getId(), release.getName(), release.getReleaseDate(),
                release.getOciName(), release.getReference(), release.getDigest(),
                ReleaseStatusDto.valueOf(releaseStatus.getName()), release.getId(), release.getMission().getId(),
                releaseContentList);

        when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        when(releaseDao.save(any(Release.class))).thenAnswer(inv -> {
            Release passedRelease = inv.getArgument(0, Release.class);
            passedRelease.setId(release.getId());
            return passedRelease;
        });

        when(firmwareVersionDao.save(ArgumentMatchers.isNotNull())).thenReturn(null);

        when(releaseDao.findById(releaseId-1)).thenReturn(Optional.of(release));

        ReleaseDto releaseDtoResult = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, releaseDtoResult, "Incorrect releaseDto");
        verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        verify(releaseMapper, Mockito.times(1)).toEntity(releaseDto, releaseStatus, mission);
        verify(releaseDao, Mockito.times(1)).findById(releaseId-1);
    }

    @Test
    void createReleaseFailedByFirmware() {
        ReleaseDto releaseDto = new ReleaseDto();
        releaseDto.setName("test");
        releaseDto.setOciName("testrepo");
        releaseDto.setMissionId(missionId);
        releaseDto.setReference("reference");

        List<ReleaseContentDto> releaseContentList = new ArrayList<>();

        FirmwareVersionDto requestFirmwareVersionDto = new FirmwareVersionDto();
        requestFirmwareVersionDto.setFirmwareVersion("v1");
        requestFirmwareVersionDto.setFirmwareId(4L);

        releaseContentList.add(new ReleaseContentDto(1L, List.of(requestFirmwareVersionDto)));
        releaseDto.setReleaseContent(releaseContentList);

        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        when(releaseDao.save(any(Release.class))).thenAnswer(inv -> {
            Release passedRelease = inv.getArgument(0, Release.class);
            passedRelease.setId(releaseId);
            return passedRelease;
        });

        Firmware firmware = Firmware.builder().id(4L).build();

        when(firmwareDao.findById(4L)).thenReturn(Optional.of(firmware));
        when(hardwareDao.findById(1L)).thenReturn(Optional.of(firstHardware));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> releaseService.createRelease(releaseDto));

        assertTrue(thrown.getMessage().contains("is not represented for Hardware"), "Incorrect message");
        verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        verify(releaseDao, Mockito.times(1)).save(any(Release.class));
        verify(firmwareDao, Mockito.times(1)).findById(4L);
        verify(hardwareDao, Mockito.times(1)).findById(1L);
    }

    @Test
    void createReleaseFailedByOrigin() {
        ReleaseDto releaseDto = new ReleaseDto();
        releaseDto.setName("test");
        releaseDto.setOciName("testrepo");
        releaseDto.setMissionId(missionId);
        releaseDto.setReference("reference");
        releaseDto.setOriginId(releaseId-1);
        releaseDto.setReleaseContent(new ArrayList<>());

        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        when(releaseDao.save(any(Release.class))).thenAnswer(inv -> {
            Release passedRelease = inv.getArgument(0, Release.class);
            passedRelease.setId(releaseId);
            return passedRelease;
        });

        when(firmwareVersionDao.save(ArgumentMatchers.isNotNull())).thenReturn(null);

        when(releaseDao.findById(releaseId-1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> releaseService.createRelease(releaseDto));

        assertTrue(thrown.getMessage().contains("Release not found"), "Incorrect message");
        verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        verify(releaseDao, Mockito.times(1)).save(any(Release.class));
        verify(releaseDao, Mockito.times(1)).findById(releaseId-1);
    }

    @Test
    void getAllReleases() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release firstRelease = Release.builder().id(releaseId).name("First Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto firstReleaseDto = releaseMapper.toDto(firstRelease);
        Release secondRelease = Release.builder().id(releaseId+1).name("First Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto secondReleaseDto = releaseMapper.toDto(secondRelease);

        when(releaseDao.findByMission(missionId, PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstRelease)));
        when(releaseDao.findByMission(missionId, PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondRelease)));
        when(releaseDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstRelease, secondRelease)));

        List<ReleaseDto> checkedFirstReleaseDtoLIst = releaseService.getAllReleases(0, 1, missionId);
        List<ReleaseDto> checkedSecondReleaseDtoLIst = releaseService.getAllReleases(1, 1, missionId);
        List<ReleaseDto> checkedAllReleaseDtoLIst = releaseService.getAllReleases(0, 2, null);

        assertEquals(List.of(firstReleaseDto), checkedFirstReleaseDtoLIst, "Incorrect firstReleaseDtoLIst");
        assertEquals(List.of(secondReleaseDto), checkedSecondReleaseDtoLIst, "Incorrect secondReleaseDtoLIst");
        assertEquals(List.of(firstReleaseDto, secondReleaseDto), checkedAllReleaseDtoLIst, "Incorrect allReleaseDtoLIst");
        verify(releaseDao, Mockito.times(1)).findByMission(missionId, PageRequest.of(0, 1));
        verify(releaseDao, Mockito.times(1)).findByMission(missionId, PageRequest.of(1, 1));
        verify(releaseDao, Mockito.times(1)).findAll(PageRequest.of(0, 2));
        verify(releaseMapper, Mockito.times(3)).toDto(firstRelease);
        verify(releaseMapper, Mockito.times(3)).toDto(secondRelease);
    }

    @Test
    void getAllReleasesEmptyPage(){
        int page = 1;
        int pageSize = 10;

        when(releaseDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<ReleaseDto> allReleaseList = releaseService.getAllReleases(page, pageSize, null);

        assertEquals(new ArrayList<>(), allReleaseList, "Incorrect Release page");
        verify(releaseDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }

    @Test
    void getReleaseById() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release release = Release.builder().id(releaseId).name("Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto releaseDto = releaseMapper.toDto(release);

        when(releaseDao.findById(releaseId)).thenReturn(Optional.of(release));

        ReleaseDto checkedReleaseDto = releaseService.getReleaseById(releaseId);

        assertEquals(releaseDto, checkedReleaseDto, "Incorrect releaseDto");
        verify(releaseDao, Mockito.times(1)).findById(releaseId);
        verify(releaseMapper, Mockito.times(2)).toDto(release);
    }

    @Test
    void getReleaseByIdFailed() {
        when(releaseDao.findById(releaseId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> releaseService.getReleaseById(releaseId));

        assertEquals("Release not found", thrown.getMessage(), "Incorrect message");
        verify(releaseDao, Mockito.times(1)).findById(releaseId);
    }

    @Test
    void getReleaseStatuses() {
        int i = 1;
        List<ReleaseStatus> releaseStatusList = new ArrayList<>();
        List<ReleaseStatusDto> releaseStatusDtoList = new ArrayList<>();
        for (ReleaseStatusDto releaseStatusDto : ReleaseStatusDto.values()) {
            ReleaseStatus releaseStatus = ReleaseStatus.builder().id(i++).name(releaseStatusDto.name()).build();
            releaseStatusList.add(releaseStatus);
            releaseStatusDtoList.add(releaseStatusDto);
        }

        when(releaseStatusDao.findAll()).thenReturn(releaseStatusList);

        List<ReleaseStatusDto> checkedReleaseStatusList = releaseService.getReleaseStatuses();

        assertEquals(releaseStatusDtoList, checkedReleaseStatusList, "Incorrect releaseStatusList");
        verify(releaseStatusDao, Mockito.times(1)).findAll();

    }
}