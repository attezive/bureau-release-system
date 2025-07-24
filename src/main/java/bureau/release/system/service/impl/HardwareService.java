package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
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
    private final MissionHardwareService missionHardwareService;
    private final HardwareFirmwareService hardwareFirmwareService;

    @Transactional
    public HardwareDto createHardware(HardwareDto hardwareDto) {
        Hardware hardware = Hardware
                .builder()
                .name(hardwareDto.getName())
                .firmwareSet(hardwareFirmwareService.createFirmwareSet(hardwareDto))
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
                                        missionHardwareService.getMissionsIdsForHardware(hardware.getId()),
                                        hardwareFirmwareService.getFirmwareIdsForHardware(hardware.getId())
                                )
                        ));
        return hardwareDtoList;
    }

    @Transactional(readOnly = true)
    public List<HardwareDto> getHardwareByMissionId(int missionId) {
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        for (Hardware hardware : missionHardwareService.getHardwareEntityForMission(missionId)) {
            hardwareDtoList.add(new HardwareDto(
                    hardware,
                    hardware.getMissions().stream().map(Mission::getId).collect(Collectors.toSet()),
                    hardware.getFirmwareSet().stream().map(Firmware::getId).collect(Collectors.toSet())
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
                missionHardwareService.getMissionsIdsForHardware(hardware.getId()),
                hardwareFirmwareService.getFirmwareIdsForHardware(hardware.getId())
        );
    }
}
