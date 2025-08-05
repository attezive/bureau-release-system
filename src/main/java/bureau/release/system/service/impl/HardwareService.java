package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.mapping.HardwareMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HardwareService {
    private final HardwareDao hardwareDao;
    private final MissionDao missionDao;
    private final FirmwareDao firmwareDao;
    private final HardwareMapper hardwareMapper;

    @Transactional
    public HardwareDto createHardware(HardwareDto hardwareDto) {
        log.debug("Create Firmware Set for HardwareDto {}", hardwareDto);
        List<Firmware> firmwareList = new ArrayList<>();
        for (Long firmwareId : hardwareDto.getFirmwareIds()) {
            Firmware firmware = firmwareDao.findById(firmwareId)
                    .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
            firmwareList.add(firmware);
        }
        log.debug("Create Hardware for HardwareDto {} and Firmware {}", hardwareDto, firmwareList);
        Hardware hardware = hardwareMapper.toEntity(hardwareDto, firmwareList, new ArrayList<>());
        return hardwareMapper.toDto(hardwareDao.save(hardware));
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getAllHardware() {
        return hardwareDao.findAll().stream().map(hardwareMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getHardwareByMissionId(int missionId) {
        return missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"))
                .getHardwareList().stream().map(hardwareMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public HardwareDto getHardwareById(long hardwareId) {
        Hardware hardware = hardwareDao.findById(hardwareId)
                .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));
        log.debug("Get Hardware {} for HardwareId {}", hardware, hardwareId);
        return hardwareMapper.toDto(hardware);
    }
}
