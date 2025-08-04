package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.FirmwareTypeDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareType;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.mapping.FirmwareMapper;
import bureau.release.system.service.mapping.FirmwareTypeMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirmwareService {
    private final FirmwareDao firmwareDao;
    private final FirmwareTypeDao firmwareTypeDao;
    private final FirmwareMapper firmwareMapper;
    private final FirmwareTypeMapper firmwareTypeMapper;

    @Transactional
    public FirmwareDto createFirmware(FirmwareDto firmwareDto) {
        FirmwareType firmwareType = firmwareTypeDao.findByName(firmwareDto.getType())
                .orElseThrow(() -> new EntityNotFoundException("Type not found"));
        Firmware firmware = firmwareMapper.toEntity(firmwareDto, firmwareType);
        return firmwareMapper.toDto(firmwareDao.save(firmware));
    }

    @Transactional(readOnly = true)
    public FirmwareDto getFirmwareById(long id) {
        Firmware firmware = firmwareDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
        return firmwareMapper.toDto(firmware);
    }

    @Transactional(readOnly = true)
    public List<FirmwareDto> getAllFirmware(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return firmwareDao.findAll(pageable).map(firmwareMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<FirmwareTypeDto> getFirmwareTypes() {
        return firmwareTypeDao.findAll().stream().map(firmwareTypeMapper::toDto).toList();
    }
}
