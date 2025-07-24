package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.MissionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MissionHardwareService {
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;

    @Transactional(readOnly = true)
    public Set<Integer> getMissionsIdsForHardware(Long hardwareId) {
        Set<Integer> missionIds = new HashSet<>();
        getMissionsForHardware(hardwareId)
                .forEach(mission -> missionIds.add(mission.getId()));
        return missionIds;
    }

    @Transactional(readOnly = true)
    public Set<Mission> getMissionsForHardware(Long hardwareId) {
        return hardwareDao.findById(hardwareId)
                .map(Hardware::getMissions)
                .orElseThrow(() -> new EntityNotFoundException("Mission for Hardware not found"));
    }

    @Transactional(readOnly = true)
    public Set<Long> getHardwareIdsForMission(Integer missionId) {
        Set<Long> hardwareIds = new HashSet<>();
        getHardwareEntityForMission(missionId)
                .forEach(hardware -> hardwareIds.add(hardware.getId()));
        return hardwareIds;
    }

    @Transactional(readOnly = true)
    public Set<Hardware> getHardwareEntityForMission(Integer missionId) {
        return missionDao.findById(missionId)
                .map(Mission::getHardwareSet)
                .orElseThrow(() -> new EntityNotFoundException("Hardware for Mission not found"));
    }

    @Transactional(readOnly = true)
    public Set<Hardware> createHardwareSet(MissionDto missionDto) throws EntityNotFoundException {
        Set<Hardware> hardwareSet = new HashSet<>();
        Mission mission = Mission.builder().name(missionDto.getName()).build();
        for (Long hardwareId : missionDto.getHardwareIds()) {
            Hardware hardware = hardwareDao.findById(hardwareId)
                    .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));
            hardwareSet.add(hardware);
            hardware.getMissions().add(mission);
        }
        return hardwareSet;
    }
}
