package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.FirmwareTypeDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirmwareService {
    private final FirmwareDao firmwareDao;
    private final FirmwareTypeDao firmwareTypeDao;
    private final HardwareFirmwareService hardwareFirmwareService;

    @Transactional
    public FirmwareDto createFirmware(FirmwareDto firmwareDto) {
        Firmware firmware = Firmware
                .builder()
                .name(firmwareDto.getName())
                .ociName(firmwareDto.getOciName())
                .type(firmwareTypeDao.findById(findTypeId(firmwareDto.getType())).get())
                .build();
        return new FirmwareDto(firmwareDao.save(firmware));
    }

    @Transactional(readOnly = true)
    public FirmwareDto getFirmwareById(long id) {
        Firmware firmware = firmwareDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
        return new FirmwareDto(firmware, hardwareFirmwareService.getHardwareIdsForFirmware(firmware.getId()));
    }

    @Transactional(readOnly = true)
    public List<FirmwareDto> getAllFirmware(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<FirmwareDto> firmwareDtoList = new ArrayList<>();
        firmwareDao.findAll(pageable).forEach(firmware -> firmwareDtoList
                .add(new FirmwareDto(
                        firmware,
                        hardwareFirmwareService.getHardwareIdsForFirmware(firmware.getId())
                )
            ));
        return  firmwareDtoList;
    }

    @Transactional(readOnly = true)
    public int findTypeId(String typeName) {
        return firmwareTypeDao
                .findIdByName(typeName)
                .orElseThrow(() -> new EntityNotFoundException("Type not found"));
    }

    @Transactional(readOnly = true)
    public List<FirmwareTypeDto> getFirmwareTypes() {
        return firmwareTypeDao.findAll().stream().map(FirmwareTypeDto::new).toList();
    }
}
