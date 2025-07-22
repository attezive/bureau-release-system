package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.HardwareToMissionDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.HardwareToMission;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionHardwareService {
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;
    private final HardwareToMissionDao hardwareToMissionDao;

    public void addHardwareToMission(Integer missionId, Long hardwareId) {
        Mission mission = missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"));

        Hardware hardware = hardwareDao.findById(hardwareId)
                .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));

        HardwareToMission link = new HardwareToMission();
        link.setMission(mission);
        link.setHardware(hardware);

        hardwareToMissionDao.save(link);
    }

    public List<Long> getHardwareIdsForMission(Integer missionId) {
        List<Long> hardwareIds = new ArrayList<>();
        hardwareToMissionDao.findHardwareByMissionId(missionId)
                .forEach(hardware -> hardwareIds.add(hardware.getId()));
        return hardwareIds;
    }

    public List<Integer> getMissionsIdsForHardware(Long hardwareId) {
        List<Integer> missionIds = new ArrayList<>();
        hardwareToMissionDao.findMissionsByHardwareId(hardwareId)
                .forEach(mission -> missionIds.add(mission.getId()));
        return missionIds;
    }

    public List<HardwareDto> getHardwareDtoListForMission(Integer missionId) {
        List<HardwareDto> hardwareDtoList = new ArrayList<>();
        hardwareToMissionDao.findHardwareByMissionId(missionId)
                .forEach(hardware -> hardwareDtoList
                        .add(new HardwareDto(hardware, getMissionsIdsForHardware(hardware.getId()))));
        return hardwareDtoList;
    }
}
