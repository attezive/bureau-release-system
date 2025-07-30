package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.service.dto.MissionDto;
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
public class MissionService {
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;

    @Transactional
    public MissionDto createMission(MissionDto missionDto) throws EntityNotFoundException {
        Mission mission = Mission
                .builder()
                .name(missionDto.getName())
                .hardwareSet(createHardwareSet(missionDto))
                .build();
        return new MissionDto(missionDao.save(mission), missionDto.getHardwareIds());
    }

    @Transactional(readOnly = true)
    public List<MissionDto> getAllMissions() {
        List<MissionDto> missionDtoList = new ArrayList<>();
        missionDao.findAll()
                .forEach(mission -> missionDtoList
                        .add(new MissionDto(
                                mission,
                                getHardwareIds(mission)
                        )));
        return missionDtoList;
    }

    @Transactional(readOnly = true)
    public MissionDto getMissionById(int missionId) throws EntityNotFoundException {
        Mission mission = missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"));
        return new MissionDto(
                mission,
                getHardwareIds(mission)
        );
    }

    @Transactional
    public void deleteMission(int missionId) {
        missionDao.deleteById(missionId);
    }

    @Transactional(readOnly = true)
    public Set<Hardware> createHardwareSet(MissionDto missionDto) throws EntityNotFoundException {
        log.debug("Creating Hardware Set for Mission {}", missionDto);
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

    private Set<Long> getHardwareIds(Mission mission) {
        return mission.getHardwareSet().stream().map(Hardware::getId).collect(Collectors.toSet());
    }
}
