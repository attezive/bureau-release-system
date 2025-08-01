package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
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

    @Transactional
    public HardwareDto createHardware(HardwareDto hardwareDto) {
        Hardware hardware = Hardware
                .builder()
                .name(hardwareDto.getName())
                .firmwareSet(createFirmwareSet(hardwareDto))
                .build();
        hardware = hardwareDao.save(hardware);
        return new HardwareDto(hardware, hardwareDto.getFirmwareIds());
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getAllHardware() {
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        hardwareDao.findAll()
                .forEach(hardware -> hardwareDtoList
                        .add(new HardwareDto(
                                hardware,
                                getFirmwareIds(hardware),
                                getMissionsIds(hardware))
                        ));
        return hardwareDtoList;
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getHardwareByMissionId(int missionId) {
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        for (Hardware hardware : missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"))
                .getHardwareSet()) {
            hardwareDtoList.add(new HardwareDto(
                    hardware,
                    getFirmwareIds(hardware),
                    getMissionsIds(hardware)
            ));
        }
        return hardwareDtoList;
    }

    @Transactional(readOnly = true)
    public HardwareDto getHardwareById(long hardwareId) {
        Hardware hardware = hardwareDao.findById(hardwareId)
                .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));
        return new HardwareDto(
                hardware,
                getFirmwareIds(hardware),
                getMissionsIds(hardware));
    }

    private List<Firmware> createFirmwareSet(HardwareDto hardwareDto) throws EntityNotFoundException {
        log.debug("Create Firmware Set for Hardware {}", hardwareDto);
        List<Firmware> firmwareSet = new ArrayList<>();
        Hardware hardware = Hardware.builder().name(hardwareDto.getName()).build();
        for (Long firmwareId : hardwareDto.getFirmwareIds()) {
            Firmware firmware = firmwareDao.findById(firmwareId)
                    .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));
            firmwareSet.add(firmware);
            firmware.getHardwareSet().add(hardware);
        }
        return firmwareSet;
    }

    private List<Integer> getMissionsIds(Hardware hardware) {
        return hardware.getMissions().stream().map(Mission::getId).toList();
    }

    private List<Long> getFirmwareIds(Hardware hardware) {
        return hardware.getFirmwareSet().stream().map(Firmware::getId).toList();
    }
}
