package bureau.release.system.service.impl;

import bureau.release.system.dal.*;
import bureau.release.system.model.*;
import bureau.release.system.service.dto.*;
import bureau.release.system.service.mapping.FirmwareVersionMapper;
import bureau.release.system.service.mapping.ReleaseMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

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

    @Spy
    private FirmwareVersionMapper firmwareVersionMapper = Mappers.getMapper(FirmwareVersionMapper.class);

    @Spy
    private ReleaseMapper releaseMapper = Mappers.getMapper(ReleaseMapper.class);

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

        ReleaseDto mappedReleaseDto = new ReleaseDto(1L, releaseDto.getName(), LocalDate.now(),
                releaseDto.getOciName(), releaseDto.getReference(), null, ReleaseStatusDto.CREATED,
                1L, releaseDto.getMissionId(), null);

        Release mappedRelease = Release.builder().name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(new ArrayList<>()).build();

        Release savedRelease = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).build();

        Release release = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(firmwareVersionList).build();

        ReleaseDto correctReleaseDto = new ReleaseDto(release.getId(), release.getName(), release.getReleaseDate(),
                release.getOciName(), release.getReference(), release.getDigest(),
                ReleaseStatusDto.valueOf(releaseStatus.getName()), release.getId(), release.getMission().getId(),
                releaseContentList);

        Mockito.when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        Mockito.when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        Mockito.when(releaseDao.save(mappedRelease)).thenReturn(savedRelease);

        Mockito.when(firmwareDao.findById(1L)).thenReturn(Optional.of(firstFirmware));
        Mockito.when(firmwareDao.findById(2L)).thenReturn(Optional.of(secondFirmware));
        Mockito.when(firmwareDao.findById(3L)).thenReturn(Optional.of(thirdFirmware));
        Mockito.when(hardwareDao.findById(1L)).thenReturn(Optional.of(firstHardware));
        Mockito.when(hardwareDao.findById(2L)).thenReturn(Optional.of(secondHardware));

        Mockito.when(releaseMapper.toEntity(eq(releaseDto), eq(releaseStatus), eq(mission))).thenReturn(mappedRelease);
        Mockito.when(releaseMapper.toDto(eq(savedRelease))).thenReturn(mappedReleaseDto);
        Mockito.when(firmwareVersionDao.save(isNotNull())).thenReturn(new FirmwareVersion());

        ReleaseDto checkedReleaseDto = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, checkedReleaseDto, "Incorrect releaseDto");
        Mockito.verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        Mockito.verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        Mockito.verify(releaseMapper, Mockito.times(1)).toEntity(releaseDto, releaseStatus, mission);
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

        ReleaseDto mappedReleaseDto = new ReleaseDto(1L, releaseDto.getName(), LocalDate.now(),
                releaseDto.getOciName(), releaseDto.getReference(), null, ReleaseStatusDto.CREATED,
                1L, releaseDto.getMissionId(), null);

        Release mappedRelease = Release.builder().name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(new ArrayList<>()).build();

        Release savedRelease = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).build();

        Release release = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(firmwareVersionList).build();

        ReleaseDto correctReleaseDto = new ReleaseDto(release.getId(), release.getName(), release.getReleaseDate(),
                release.getOciName(), release.getReference(), release.getDigest(),
                ReleaseStatusDto.valueOf(releaseStatus.getName()), release.getId(), release.getMission().getId(),
                releaseContentList);

        Mockito.when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        Mockito.when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        Mockito.when(releaseDao.save(mappedRelease)).thenReturn(savedRelease);

        Mockito.when(releaseMapper.toEntity(eq(releaseDto), eq(releaseStatus), eq(mission))).thenReturn(mappedRelease);
        Mockito.when(releaseMapper.toDto(eq(savedRelease))).thenReturn(mappedReleaseDto);
        Mockito.when(firmwareVersionDao.save(ArgumentMatchers.isNotNull())).thenReturn(new FirmwareVersion());

        Mockito.when(releaseDao.findById(releaseId-1)).thenReturn(Optional.of(release));

        ReleaseDto checkedReleaseDto = releaseService.createRelease(releaseDto);
        assertEquals(correctReleaseDto, checkedReleaseDto, "Incorrect releaseDto");
        Mockito.verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        Mockito.verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
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

        ReleaseDto mappedReleaseDto = new ReleaseDto(1L, releaseDto.getName(), LocalDate.now(),
                releaseDto.getOciName(), releaseDto.getReference(), null, ReleaseStatusDto.CREATED,
                1L, releaseDto.getMissionId(), null);

        Release mappedRelease = Release.builder().name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).firmwareVersions(new ArrayList<>()).build();

        Release savedRelease = Release.builder().id(releaseId).name(releaseDto.getName()).status(releaseStatus)
                .ociName(releaseDto.getOciName()).reference(releaseDto.getReference()).mission(mission)
                .releaseDate(LocalDate.now()).build();

        Mockito.when(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())).thenReturn(Optional.of(releaseStatus));
        Mockito.when(missionDao.findById(releaseDto.getMissionId())).thenReturn(Optional.of(mission));
        Mockito.when(releaseMapper.toEntity(eq(releaseDto), eq(releaseStatus), eq(mission))).thenReturn(mappedRelease);
        Mockito.when(releaseMapper.toDto(eq(savedRelease))).thenReturn(mappedReleaseDto);
        Mockito.when(releaseDao.save(mappedRelease)).thenReturn(savedRelease);

        Firmware firmware = Firmware.builder().id(4L).build();

        Mockito.when(firmwareDao.findById(4L)).thenReturn(Optional.of(firmware));
        Mockito.when(hardwareDao.findById(1L)).thenReturn(Optional.of(firstHardware));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> releaseService.createRelease(releaseDto));

        assertTrue(thrown.getMessage().contains("is not represented for Hardware"), "Incorrect message");
        Mockito.verify(releaseStatusDao, Mockito.times(1)).findByName(ReleaseStatusDto.CREATED.name());
        Mockito.verify(missionDao, Mockito.times(1)).findById(releaseDto.getMissionId());
        Mockito.verify(releaseDao, Mockito.times(1)).save(mappedRelease);
        Mockito.verify(firmwareDao, Mockito.times(1)).findById(4L);
        Mockito.verify(hardwareDao, Mockito.times(1)).findById(1L);
    }

    @Test
    void getAllReleases() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release firstRelease = Release.builder().id(releaseId).name("First Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto firstReleaseDto = new ReleaseDto(firstRelease.getId(), firstRelease.getName(),
                firstRelease.getReleaseDate(), firstRelease.getOciName(), firstRelease.getReference(),
                firstRelease.getDigest(), ReleaseStatusDto.valueOf(releaseStatus.getName()), firstRelease.getId(),
                firstRelease.getMission().getId(), releaseContentList);
        Release secondRelease = Release.builder().id(releaseId+1).name("First Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto secondReleaseDto = new ReleaseDto(secondRelease.getId(), secondRelease.getName(),
                secondRelease.getReleaseDate(), secondRelease.getOciName(), secondRelease.getReference(),
                secondRelease.getDigest(), ReleaseStatusDto.valueOf(releaseStatus.getName()), secondRelease.getId(),
                secondRelease.getMission().getId(), releaseContentList);

        Mockito.when(releaseDao.findByMission(missionId, PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(firstRelease)));
        Mockito.when(releaseDao.findByMission(missionId, PageRequest.of(1, 1)))
                .thenReturn(new PageImpl<>(List.of(secondRelease)));
        Mockito.when(releaseDao.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(firstRelease, secondRelease)));

        Mockito.when(releaseMapper.toDto(eq(firstRelease))).thenReturn(firstReleaseDto);
        Mockito.when(releaseMapper.toDto(eq(secondRelease))).thenReturn(secondReleaseDto);

        List<ReleaseDto> checkedFirstReleaseDtoLIst = releaseService.getAllReleases(0, 1, missionId);
        List<ReleaseDto> checkedSecondReleaseDtoLIst = releaseService.getAllReleases(1, 1, missionId);
        List<ReleaseDto> checkedAllReleaseDtoLIst = releaseService.getAllReleases(0, 2, null);

        assertEquals(List.of(firstReleaseDto), checkedFirstReleaseDtoLIst, "Incorrect firstReleaseDtoLIst");
        assertEquals(List.of(secondReleaseDto), checkedSecondReleaseDtoLIst, "Incorrect secondReleaseDtoLIst");
        assertEquals(List.of(firstReleaseDto, secondReleaseDto), checkedAllReleaseDtoLIst, "Incorrect allReleaseDtoLIst");
    }

    @Test
    void getAllReleasesEmptyPage(){
        int page = 1;
        int pageSize = 10;

        Mockito.when(releaseDao.findAll(PageRequest.of(page, pageSize))).thenReturn(new PageImpl<>(List.of()));

        List<ReleaseDto> allReleaseList = releaseService.getAllReleases(page, pageSize, null);

        assertEquals(new ArrayList<>(), allReleaseList, "Incorrect Release page");
        Mockito.verify(releaseDao, Mockito.times(1)).findAll(PageRequest.of(page, pageSize));
    }

    @Test
    void getReleaseById() {
        ReleaseStatus releaseStatus = ReleaseStatus.builder().name(ReleaseStatusDto.CREATED.name()).build();
        Mission mission = Mission.builder().id(missionId).build();

        Release release = Release.builder().id(releaseId).name("Release").status(releaseStatus)
                .ociName("repo").reference("reference").mission(mission).releaseDate(LocalDate.now())
                .firmwareVersions(firmwareVersionList).build();
        ReleaseDto releaseDto = new ReleaseDto(release.getId(), release.getName(), release.getReleaseDate(),
                release.getOciName(), release.getReference(), release.getDigest(),
                ReleaseStatusDto.valueOf(releaseStatus.getName()), release.getId(),
                release.getMission().getId(), releaseContentList);

        Mockito.when(releaseDao.findById(releaseId)).thenReturn(Optional.of(release));
        Mockito.when(releaseMapper.toDto(eq(release))).thenReturn(releaseDto);

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
        List<ReleaseStatusDto> releaseStatusDtoList = new ArrayList<>();
        for (ReleaseStatusDto releaseStatusDto : ReleaseStatusDto.values()) {
            ReleaseStatus releaseStatus = ReleaseStatus.builder().id(i++).name(releaseStatusDto.name()).build();
            releaseStatusList.add(releaseStatus);
            releaseStatusDtoList.add(releaseStatusDto);
        }

        Mockito.when(releaseStatusDao.findAll()).thenReturn(releaseStatusList);

        List<ReleaseStatusDto> checkedReleaseStatusList = releaseService.getReleaseStatuses();

        assertEquals(releaseStatusDtoList, checkedReleaseStatusList, "Incorrect releaseStatusList");
        Mockito.verify(releaseStatusDao, Mockito.times(1)).findAll();
    }
}