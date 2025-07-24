package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.HardwareDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.HardwareDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HardwareFirmwareService {
    private final HardwareDao hardwareDao;
    private final FirmwareDao firmwareDao;

    @Transactional(readOnly = true)
    public Set<Long> getHardwareIdsForFirmware(Long firmwareId) {
        return getHardwareForFirmware(firmwareId).stream().map(Hardware::getId).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Hardware> getHardwareForFirmware(Long firmwareId) {
        return firmwareDao.findById(firmwareId)
                .map(Firmware::getHardwareSet)
                .orElseThrow(() -> new EntityNotFoundException("Hardware for Firmware not found"));
    }

    @Transactional(readOnly = true)
    public Set<Long> getFirmwareIdsForHardware(Long hardwareId) {
        return getFirmwareEntityForHardware(hardwareId).stream().map(Firmware::getId).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Firmware> getFirmwareEntityForHardware(Long hardwareId) {
        return hardwareDao.findById(hardwareId)
                .map(Hardware::getFirmwareSet)
                .orElseThrow(() -> new EntityNotFoundException("Firmware for Hardware not found"));
    }

    @Transactional(readOnly = true)
    public Set<Firmware> createFirmwareSet(HardwareDto hardwareDto) throws EntityNotFoundException {
        Set<Firmware> firmwareSet = new HashSet<>();
        Hardware hardware = Hardware.builder().name(hardwareDto.getName()).build();
        for (Long firmwareId : hardwareDto.getFirmwareIds()) {
            Firmware firmware = firmwareDao.findById(firmwareId)
                    .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
            firmwareSet.add(firmware);
            firmware.getHardwareSet().add(hardware);
        }
        return firmwareSet;
    }
}
