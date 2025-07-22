package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.HardwareDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HardwareService {
    private final HardwareDao hardwareDao;
    private final MissionHardwareService missionHardwareService;

    public HardwareDto createHardware(HardwareDto hardwareDto){
        return new HardwareDto(hardwareDao.save(hardwareDto.toEntity()));
    }

    public List<HardwareDto> getAllHardware(){
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        hardwareDao.findAll()
                .forEach(hardware -> hardwareDtoList
                        .add(new HardwareDto(
                                hardware,
                                missionHardwareService.getMissionsIdsForHardware(hardware.getId()))
                        )
                );
        return hardwareDtoList;
    }

    public List<HardwareDto> getHardwareByMission(int missionId){
        return missionHardwareService.getHardwareDtoListForMission(missionId);
    }

    public Optional<HardwareDto> getHardwareById(long hardwareId){
        Hardware hardware = hardwareDao.findById(hardwareId).orElse(null);
        if (hardware != null) {
            return Optional.of(
                    new HardwareDto(hardware, missionHardwareService.getMissionsIdsForHardware(hardware.getId()))
            );
        }
        return Optional.empty();
    }

}
