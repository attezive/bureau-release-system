package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.FirmwareTypeDao;
import bureau.release.system.exception.ClientException;
import bureau.release.system.exception.ReleaseSystemException;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareType;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.mapping.FirmwareMapper;
import bureau.release.system.service.mapping.FirmwareTypeMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class FirmwareService {
    private final FirmwareDao firmwareDao;
    private final FirmwareTypeDao firmwareTypeDao;
    private final FirmwareMapper firmwareMapper;
    private final FirmwareTypeMapper firmwareTypeMapper;
    private final ArtifactDownloader artifactDownloader;

    @PreAuthorize("hasAuthority('admin')")
    @Transactional
    public FirmwareDto createFirmware(@Valid FirmwareDto firmwareDto) {
        FirmwareType firmwareType = firmwareTypeDao.findByName(firmwareDto.getType())
                .orElseThrow(() -> new EntityNotFoundException("Type not found"));
        Firmware firmware = firmwareMapper.toEntity(firmwareDto, firmwareType);
        log.debug("Create Firmware {} for FirmwareDto {}", firmware, firmwareDto);
        return firmwareMapper.toDto(firmwareDao.save(firmware));
    }

    @Transactional(readOnly = true)
    public FirmwareDto getFirmwareById(@Positive long firmwareId) {
        Firmware firmware = firmwareDao.findById(firmwareId)
                .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
        log.debug("Get Firmware {} for FirmwareId {}", firmware, firmwareId);
        return firmwareMapper.toDto(firmware);
    }

    @Transactional(readOnly = true)
    public List<FirmwareDto> getAllFirmware(@PositiveOrZero int page, @Positive int size) {
        Pageable pageable = PageRequest.of(page, size);
        return firmwareDao.findAll(pageable).map(firmwareMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<FirmwareTypeDto> getFirmwareTypes() {
        return firmwareTypeDao.findAll().stream().map(firmwareTypeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<Manifest> getFirmwareVersions(@Positive long firmwareId) {
        log.info("GetFirmwareVersions: id={}", firmwareId);
        FirmwareDto firmware = getFirmwareById(firmwareId);
        List<Manifest> manifests;
        try {
            manifests = artifactDownloader.getArtifacts(firmware.getOciName());
        } catch (ReleaseSystemException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
        return manifests;
    }
}
