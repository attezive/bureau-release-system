package bureau.release.system.service.impl;

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
    private final MissionHardwareService missionHardwareService;

    @Transactional
    public MissionDto createMission(MissionDto missionDto) throws EntityNotFoundException {
        Mission mission = Mission.builder().name(missionDto.getName()).build();
        mission.setHardwareSet(missionHardwareService.createHardwareSet(missionDto));
        return new MissionDto(missionDao.save(mission), missionDto.getHardwareIds());
    }

    @Transactional(readOnly = true)
    public List<MissionDto> getAllMissions() {
        List<MissionDto> missionDtoList = new ArrayList<>();
        missionDao.findAll()
                .forEach(mission -> missionDtoList
                        .add(new MissionDto(
                                mission,
                                missionHardwareService.getHardwareIdsForMission(mission.getId())
                        )));
        return missionDtoList;
    }

    @Transactional(readOnly = true)
    public MissionDto getMissionById(int missionId) throws EntityNotFoundException {
        Mission mission = missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"));
        return new MissionDto(
                mission,
                mission.getHardwareSet().stream().map(Hardware::getId).collect(Collectors.toSet())
        );
    }

    @Transactional
    public void deleteMission(int missionId) {
        missionDao.deleteById(missionId);
    }
}
