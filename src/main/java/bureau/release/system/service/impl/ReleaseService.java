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
import bureau.release.system.service.mapping.FirmwareVersionMapper;
import bureau.release.system.service.mapping.ReleaseMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ReleaseService {
    private final ReleaseDao releaseDao;
    private final ReleaseStatusDao releaseStatusDao;
    private final FirmwareVersionDao firmwareVersionDao;
    private final FirmwareDao firmwareDao;
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;
    private final ArtifactDownloader artifactDownloader;
    private final ArtifactUploader artifactUploader;
    private final FirmwareVersionMapper firmwareVersionMapper;
    private final ReleaseMapper releaseMapper;

    @Transactional
    public ReleaseDto createRelease(@Valid ReleaseDto releaseDto) {
        releaseDto.setReleaseDate(LocalDate.now());
        Release release = releaseMapper.toEntity(
                releaseDto,
                releaseStatusDao.findByName(ReleaseStatusDto.CREATED.name())
                        .orElseThrow(() -> new EntityNotFoundException("Release Status not found")),
                missionDao.findById(releaseDto.getMissionId())
                        .orElseThrow(() -> new EntityNotFoundException("Mission not found")));
        release = releaseDao.save(release);
        release.setFirmwareVersions(new ArrayList<>());

        ReleaseDto resultReleaseDto = releaseMapper.toDto(release);
        List<ReleaseContentDto> releaseContent = createReleaseContent(releaseDto, release);
        log.debug("Create release with content: {}", releaseContent);
        resultReleaseDto.setReleaseContent(releaseContent);
        return resultReleaseDto;
    }

    private List<ReleaseContentDto> createReleaseContent(ReleaseDto releaseDto, Release release) {
        log.debug("Creating Release Content: releaseId={}", releaseDto.getId());
        List<ReleaseContentDto> releaseContentList = new ArrayList<>();

        for (ReleaseContentDto releaseContentDto : releaseDto.getReleaseContent()) {
            List<FirmwareVersionDto> firmwareVersionDtoList = new ArrayList<>();
            for (FirmwareVersionDto firmwareVersionDto : releaseContentDto.getFirmwareVersions()) {

                firmwareVersionDto.setHardwareId(releaseContentDto.getHardwareId());
                Firmware firmware = firmwareDao.findById(firmwareVersionDto.getFirmwareId())
                        .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
                Hardware hardware = hardwareDao.findById(firmwareVersionDto.getHardwareId())
                        .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));

                if (!hardware.getFirmwareList().contains(firmware)) {
                    throw new EntityNotFoundException(
                            String.format("Firmware %s(id %d) is not represented for Hardware %s(id %d)",
                                    firmware.getName(), firmwareVersionDto.getFirmwareId(),
                                    hardware.getName(), firmwareVersionDto.getHardwareId()));
                }

                FirmwareVersion firmwareVersion = firmwareVersionMapper
                        .toEntity(firmwareVersionDto, firmware, hardware, release);
                firmwareVersionDao.save(firmwareVersion);
                firmwareVersionDtoList.add(firmwareVersionMapper.toDto(firmwareVersion));
            }
            releaseContentDto.setFirmwareVersions(firmwareVersionDtoList);
            releaseContentList.add(releaseContentDto);
        }

        if (releaseDto.getOriginId() != null) {
            List<ReleaseContentDto> originReleaseContent = setupByOrigin(
                    release, releaseDto.getOriginId(), releaseContentList);
            releaseContentList.addAll(originReleaseContent);
        }

        return releaseContentList;
    }

    private List<ReleaseContentDto> setupByOrigin(Release release, Long originId,
                                                  List<ReleaseContentDto> releaseContentList) {
        log.debug("Setting up Release Content: originId={}", originId);
        Release originRelease = releaseDao.findById(originId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));

        Map<Long, List<FirmwareVersionDto>> releaseContentMap = new HashMap<>();
        for (ReleaseContentDto releaseContentDto : releaseContentList) {
            releaseContentMap.put(releaseContentDto.getHardwareId(), releaseContentDto.getFirmwareVersions());
        }

        for (FirmwareVersion originFirmwareVersion : originRelease.getFirmwareVersions()) {
            long hardwareId = originFirmwareVersion.getHardware().getId();
            FirmwareVersionDto originFirmwareVersionDto = firmwareVersionMapper.toDto(originFirmwareVersion);
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
                releaseContentMap.get(hardwareId).add(firmwareVersionMapper.toDto(firmwareVersion));
            }
        }

        List<ReleaseContentDto> originReleaseContentList = new ArrayList<>();
        releaseContentMap.forEach((hardwareId, firmwareVersionDto) ->
                originReleaseContentList.add(new ReleaseContentDto(hardwareId, firmwareVersionDto)));
        return originReleaseContentList;
    }

    @Transactional(readOnly = true)
    public List<ReleaseDto> getAllReleases(int page, int size, @Min(1) Integer missionId) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReleaseDto> releases = new ArrayList<>();
        releaseDao.findAll(pageable).forEach(release -> {
            if (missionId == null || release.getMission().getId().equals(missionId)) {
                releases.add(releaseMapper.toDto(release));
            }
        });
        return releases;
    }

    @Transactional(readOnly = true)
    public ReleaseDto getReleaseById(@Positive long releaseId) throws EntityNotFoundException {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        return releaseMapper.toDto(release);
    }

    @Transactional(readOnly = true)
    public List<ReleaseStatusDto> getReleaseStatuses() {
        List<ReleaseStatus> releaseStatuses = releaseStatusDao.findAll();
        log.debug("Get Release Status Dtos: releaseStatuses={}", releaseStatuses);
        return releaseStatuses.stream().map(
                releaseStatus -> ReleaseStatusDto.valueOf(releaseStatus.getName())).toList();
    }

    public StreamingResponseBody getTar(@Positive long releaseId) {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        log.debug("Get Tar: Release {}", release);
        return outputStream ->
                artifactDownloader.loadReleaseContent(release, outputStream);
    }

    public ReleaseDto uploadReleaseToHarbor(@Positive long releaseId) {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        log.debug("Upload Harbor: Release {}", release);

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
        } catch (ReleaseSystemException e) {
            setReleaseStatus(release, ReleaseStatusDto.BUILD_ERROR);
            throw e;
        }

        release.setDigest(digest);
        log.debug("Digest updated for release id {}: {}", releaseId, digest);
        setReleaseStatus(release, ReleaseStatusDto.COMPLETED);
        return releaseMapper.toDto(release);
    }

    private ByteArrayOutputStream downloadRelease(Release release) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        setReleaseStatus(release, ReleaseStatusDto.DOWNLOADING);
        log.debug("Download data for release id {}", release.getId());
        artifactDownloader.loadReleaseContent(release, outputStream);
        return outputStream;
    }

    private String uploadRelease(Release release, ByteArrayOutputStream outputStream) {
        setReleaseStatus(release, ReleaseStatusDto.UPLOADING);
        log.debug("Upload data release id {}", release.getId());
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
