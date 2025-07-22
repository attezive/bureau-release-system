package bureau.release.system.service.impl;

import bureau.release.system.model.Mission;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.service.dto.MissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MissionService {
    private final MissionDao missionDao;
    private final MissionHardwareService missionHardwareService;;

    public MissionDto createMission(MissionDto missionDto) {
        return new MissionDto(missionDao.save(missionDto.toEntity()));
    }

    public List<MissionDto> getAllMissions(){
        List<MissionDto> missionDtoList = new ArrayList<>();
        missionDao.findAll()
                .forEach(mission -> missionDtoList
                        .add(new MissionDto(
                                mission,
                                missionHardwareService.getHardwareIdsForMission(mission.getId())
                        )));
        return missionDtoList;
    }

    public Optional<MissionDto> getMissionById(int missionId) {
        Mission mission = missionDao.findById(missionId).orElse(null);
        if (mission != null) {
            return Optional.of(new MissionDto(
                    mission,
                    missionHardwareService.getHardwareIdsForMission(mission.getId())
            ));
        }
        return Optional.empty();
    }

    public void deleteMission(int missionId) {
        missionDao.deleteById(missionId);
    }
}
