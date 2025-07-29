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
import java.util.stream.Collectors;

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
        return new HardwareDto(hardware.getId(), hardware.getName(), hardwareDto.getFirmwareIds());
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getAllHardware() {
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        hardwareDao.findAll()
                .forEach(hardware -> hardwareDtoList
                        .add(new HardwareDto(
                                        hardware,
                                        getMissionsIds(hardware),
                                        getFirmwareIds(hardware)
                                )
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
                    getMissionsIds(hardware),
                    getFirmwareIds(hardware)
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
                getMissionsIds(hardware),
                getFirmwareIds(hardware)
        );
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

    private Set<Integer> getMissionsIds(Hardware hardware) {
        return hardware.getMissions().stream().map(Mission::getId).collect(Collectors.toSet());
    }

    private Set<Long> getFirmwareIds(Hardware hardware) {
        return hardware.getFirmwareSet().stream().map(Firmware::getId).collect(Collectors.toSet());
    }
}
