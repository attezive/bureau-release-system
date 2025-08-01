package bureau.release.system.service.impl;

import bureau.release.system.dal.*;
import bureau.release.system.exception.ReleaseSystemException;
import bureau.release.system.model.*;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.ArtifactUploader;
import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseContentDto;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.dto.ReleaseStatusDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReleaseService {
    private final ReleaseDao releaseDao;
    private final ReleaseStatusDao releaseStatusDao;
    private final FirmwareVersionDao firmwareVersionDao;
    private final FirmwareDao firmwareDao;
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;
    private final ArtifactDownloader artifactDownloader;
    private final ArtifactUploader artifactUploader;

    @Transactional
    public ReleaseDto createRelease(ReleaseDto releaseDto) {
        Release release = Release
                .builder()
                .name(releaseDto.getName())
                .status(releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())
                        .orElseThrow(() -> new EntityNotFoundException("Release Status not found")))
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .mission(missionDao.findById(releaseDto.getMissionId())
                        .orElseThrow(() -> new EntityNotFoundException("Mission not found")))
                .releaseDate(LocalDate.now())
                .build();
        release = releaseDao.save(release);
        List<ReleaseContentDto> releaseContent = createReleaseContent(releaseDto, release);
        return new ReleaseDto(release, releaseContent);
    }

    private List<ReleaseContentDto> createReleaseContent(ReleaseDto releaseDto, Release release) {
        log.debug("Creating Release Content: releaseId={}", releaseDto.getId());
        List<ReleaseContentDto> releaseContentList = new ArrayList<>();
        List<FirmwareVersion> firmwareVersions = new ArrayList<>();

        for (ReleaseContentDto releaseContentDto : releaseDto.getReleaseContent()) {
            List<FirmwareVersionDto> firmwareVersionDtoList = new ArrayList<>();
            for (FirmwareVersionDto firmwareVersionDto : releaseContentDto.getFirmwareVersions()) {

                firmwareVersionDto.setHardwareId(releaseContentDto.getHardwareId());
                Firmware firmware = firmwareDao.findById(firmwareVersionDto.getFirmwareId())
                        .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
                Hardware hardware = hardwareDao.findById(firmwareVersionDto.getHardwareId())
                        .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));

                FirmwareVersion firmwareVersion = FirmwareVersion
                        .builder()
                        .firmwareVersion(firmwareVersionDto.getFirmwareVersion())
                        .firmware(firmware)
                        .release(release)
                        .hardware(hardware)
                        .build();
                firmwareVersionDao.save(firmwareVersion);
                firmwareVersions.add(firmwareVersion);
                firmwareVersionDtoList.add(new FirmwareVersionDto(firmwareVersion));
            }
            releaseContentDto.setFirmwareVersions(firmwareVersionDtoList);
            releaseContentList.add(releaseContentDto);
        }

        if (releaseDto.getOriginId() != null) {
            List<ReleaseContentDto> originReleaseContent = setupByOrigin(
                    release,
                    releaseDto.getOriginId(),
                    firmwareVersions);
            releaseContentList.addAll(originReleaseContent);
        }
        release.setFirmwareVersions(firmwareVersions);
        return releaseContentList;
    }

    private List<ReleaseContentDto> setupByOrigin(Release release, Long originId, List<FirmwareVersion> firmwareVersions) {
        log.debug("Setting up Release Content: originId={}", originId);
        Release originRelease = releaseDao.findById(originId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));

        Map<Long, List<FirmwareVersionDto>> releaseContentMap = getReleaseContentMap(firmwareVersions);
        for (FirmwareVersion originFirmwareVersion : originRelease.getFirmwareVersions()) {
            long hardwareId = originFirmwareVersion.getHardware().getId();
            FirmwareVersionDto originFirmwareVersionDto = new FirmwareVersionDto(originFirmwareVersion);
            if (!releaseContentMap.containsKey(hardwareId)) {
                releaseContentMap.put(hardwareId, new ArrayList<>());
            }
            if (!releaseContentMap.get(hardwareId).contains(originFirmwareVersionDto)) {
                FirmwareVersion firmwareVersion = FirmwareVersion
                        .builder()
                        .firmwareVersion(originFirmwareVersion.getFirmwareVersion())
                        .firmware(originFirmwareVersion.getFirmware())
                        .release(release)
                        .hardware(originFirmwareVersion.getHardware())
                        .build();
                firmwareVersionDao.save(firmwareVersion);
                firmwareVersions.add(firmwareVersion);
                releaseContentMap.get(hardwareId).add(new FirmwareVersionDto(firmwareVersion));
            }
        }

        return formatMapToList(releaseContentMap);
    }

    @Transactional(readOnly = true)
    public List<ReleaseDto> getAllReleases(int page, int size, Integer missionId) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReleaseDto> releases = new ArrayList<>();
        releaseDao.findAll(pageable).forEach(release -> {
            if (missionId == null || release.getMission().getId().equals(missionId)) {
                releases.add(new ReleaseDto(
                                release,
                                getReleaseContentList(release)
                        )
                );
            }
        });
        return releases;
    }

    @Transactional(readOnly = true)
    public ReleaseDto getReleaseById(long releaseId) throws EntityNotFoundException {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        return new ReleaseDto(
                release,
                getReleaseContentList(release)
        );
    }

    @Transactional(readOnly = true)
    public List<ReleaseStatus> getReleaseStatuses() {
        return releaseStatusDao.findAll();
    }

    private List<ReleaseContentDto> getReleaseContentList(Release release) {
        Map<Long, List<FirmwareVersionDto>> releaseContentMap = getReleaseContentMap(release.getFirmwareVersions());
        return formatMapToList(releaseContentMap);
    }

    private List<ReleaseContentDto> formatMapToList(Map<Long, List<FirmwareVersionDto>> releaseContentMap) {
        List<ReleaseContentDto> releaseContentList = new ArrayList<>();
        releaseContentMap.forEach((hardwareId, firmwareVersions) ->
                releaseContentList.add(new ReleaseContentDto(hardwareId, firmwareVersions)));
        return releaseContentList;
    }

    private Map<Long, List<FirmwareVersionDto>> getReleaseContentMap(List<FirmwareVersion> firmwareVersions) {
        Map<Long, List<FirmwareVersionDto>> releaseContentMap = new HashMap<>();
        firmwareVersions.forEach(firmwareVersion -> {
            if (!releaseContentMap.containsKey(firmwareVersion.getHardware().getId())) {
                releaseContentMap.put(firmwareVersion.getHardware().getId(), new ArrayList<>());
            }
            releaseContentMap.get(firmwareVersion.getHardware().getId()).add(new FirmwareVersionDto(firmwareVersion));
        });
        return releaseContentMap;
    }

    public StreamingResponseBody getTar(long releaseId) {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        return outputStream ->
                artifactDownloader.loadReleaseContent(release, outputStream);
    }

    public ReleaseDto uploadReleaseToHarbor(long releaseId) {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));

        ByteArrayOutputStream outputStream;
        try {
            outputStream = downloadRelease(release);
        } catch (ReleaseSystemException e) {
            setReleaseStatus(release, ReleaseStatusDto.BUILD_ERROR);
            throw e;
        }

        String digest;
        try {
            digest = uploadRelease(release, outputStream);
        }  catch (ReleaseSystemException e) {
            setReleaseStatus(release, ReleaseStatusDto.BUILD_ERROR);
            throw e;
        }

        release.setDigest(digest);
        setReleaseStatus(release, ReleaseStatusDto.COMPLETED);
        return new ReleaseDto(release, getReleaseContentList(release));
    }

    private ByteArrayOutputStream downloadRelease(Release release) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        setReleaseStatus(release, ReleaseStatusDto.DOWNLOADING);
        artifactDownloader.loadReleaseContent(release, outputStream);
        return outputStream;
    }

    private String uploadRelease(Release release, ByteArrayOutputStream outputStream) {
        setReleaseStatus(release, ReleaseStatusDto.UPLOADING);
        return artifactUploader.uploadArtifact(outputStream,
                release.getName() + ".tar",
                release.getOciName(),
                release.getReference());
    }

    private void setReleaseStatus(Release release, ReleaseStatusDto releaseStatus) {
        log.debug("Setting release status by releaseId={} to {}", release.getId(), releaseStatus.name());
        release.setStatus(
                releaseStatusDao.findByName(releaseStatus.name())
                        .orElseThrow(() -> new EntityNotFoundException("Release Status not found"))
        );
        releaseDao.save(release);
    }
}
