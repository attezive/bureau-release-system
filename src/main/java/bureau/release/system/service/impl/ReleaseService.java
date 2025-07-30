package bureau.release.system.service.impl;

import bureau.release.system.dal.*;
import bureau.release.system.model.*;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.ArtifactUploader;
import bureau.release.system.service.dto.FirmwareVersionDto;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .mission(missionDao.findById(releaseDto.getMissionId())
                        .orElseThrow(() -> new EntityNotFoundException("Mission not found")))
                .releaseDate(LocalDate.now())
                .build();
        release = releaseDao.save(release);
        List<FirmwareVersionDto> firmwareVersions = createFirmwareVersions(releaseDto, release);
        return new ReleaseDto(release, firmwareVersions);
    }

    @Transactional
    public List<FirmwareVersionDto> createFirmwareVersions(ReleaseDto releaseDto, Release release) {
        log.debug("Creating Firmware Versions: releaseId={}", releaseDto.getId());
        Set<Long> firmwareIds = new HashSet<>();
        List<FirmwareVersionDto> firmwareVersions = new ArrayList<>();
        Firmware firmware;
        Hardware hardware;
        FirmwareVersion firmwareVersion;

        release.setFirmwareVersions(new ArrayList<>());
        for (FirmwareVersionDto firmwareVersionDto : releaseDto.getFirmwareVersions()) {
            firmware = firmwareDao.findById(firmwareVersionDto.getFirmwareId())
                    .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
            hardware = hardwareDao.findById(firmwareVersionDto.getHardwareId())
                    .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));
            firmwareIds.add(firmware.getId());
            firmwareVersion = FirmwareVersion
                    .builder()
                    .firmwareVersion(firmwareVersionDto.getFirmwareVersion())
                    .firmware(firmware)
                    .release(release)
                    .hardware(hardware)
                    .build();
            firmwareVersionDao.save(firmwareVersion);
            release.getFirmwareVersions().add(firmwareVersion);
            firmwareVersions.add(new FirmwareVersionDto(firmwareVersion));
        }

        if (releaseDto.getOriginId() != null) {
            setupByOrigin(releaseDto, release, firmwareIds,  firmwareVersions);
        }

        return firmwareVersions;
    }

    @Transactional
    public void setupByOrigin(ReleaseDto releaseDto, Release release,
                                                  Set<Long> firmwareIds, List<FirmwareVersionDto> firmwareVersions) {
        log.debug("Setting up Firmware Versions: originId={}", releaseDto.getOriginId());
        Firmware firmware;
        List<FirmwareVersion> originFirmware = releaseDao.findById(releaseDto.getOriginId())
                .orElseThrow(() -> new EntityNotFoundException("Release not found"))
                .getFirmwareVersions();

        for (FirmwareVersion originFirmwareVersion : originFirmware) {
            firmware = originFirmwareVersion.getFirmware();
            if (!firmwareIds.contains(firmware.getId())) {
                firmwareIds.add(firmware.getId());
                release.getFirmwareVersions().add(originFirmwareVersion);
                firmwareVersions.add(new FirmwareVersionDto(originFirmwareVersion));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ReleaseDto> getAllReleases(int page, int size, Integer missionId) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReleaseDto> releases = new ArrayList<>();
        releaseDao.findAll(pageable).forEach(release -> {
            if (missionId == null || release.getMission().getId().equals(missionId)) {
                releases.add(new ReleaseDto(
                                release,
                                getFirmwareVersions(release)
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
                getFirmwareVersions(release)
        );
    }

    @Transactional(readOnly = true)
    public List<ReleaseStatus> getReleaseStatuses() {
        return releaseStatusDao.findAll();
    }

    private List<FirmwareVersionDto> getFirmwareVersions(Release release) {
        return release.getFirmwareVersions().stream().map(FirmwareVersionDto::new).toList();
    }

    @Transactional(readOnly = true)
    public StreamingResponseBody getTar(long releaseId){
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));
        return outputStream ->
                artifactDownloader.loadReleaseContent(release, outputStream);
    }

    @Transactional
    public ReleaseDto uploadReleaseToHarbor(long releaseId) {
        Release release = releaseDao.findById(releaseId)
                .orElseThrow(() -> new EntityNotFoundException("Release not found"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        setReleaseStatus(release, ReleaseStatusDto.DOWNLOADING);
        artifactDownloader.loadReleaseContent(release, outputStream);

        setReleaseStatus(release, ReleaseStatusDto.UPLOADING);
        String digest = artifactUploader.uploadArtifact(outputStream,
                release.getName()+".tar",
                release.getOciName(),
                release.getReference());

        release.setDigest(digest);
        setReleaseStatus(release, ReleaseStatusDto.COMPLETED);
        return new ReleaseDto(release, getFirmwareVersions(release));
    }

    private void setReleaseStatus(Release release, ReleaseStatusDto releaseStatus) {
        log.debug("Setting release status by releaseId={} to {}", release.getId(), releaseStatus.name());
        release.setStatus(
                releaseStatusDao.findByName(releaseStatus.name())
                        .orElseThrow(() -> new EntityNotFoundException("Release Status not found"))
        );
    }
}
