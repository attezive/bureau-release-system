package bureau.release.system.service.impl;

import bureau.release.system.dal.HardwareDao;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.dal.MissionDao;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.mapping.MissionMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MissionService {
    private final MissionDao missionDao;
    private final HardwareDao hardwareDao;
    private final MissionMapper missionMapper;

    @Transactional
    public MissionDto createMission(MissionDto missionDto) throws EntityNotFoundException {
        log.debug("Creating Hardware Set for Mission {}", missionDto);
        List<Hardware> hardwareList = new ArrayList<>();
        for (Long hardwareId : missionDto.getHardwareIds()) {
            Hardware hardware = hardwareDao.findById(hardwareId)
                    .orElseThrow(() -> new EntityNotFoundException("Hardware not found"));
            hardwareList.add(hardware);
        }
        Mission mission = missionMapper.toEntity(missionDto, hardwareList);
        return missionMapper.toDto(missionDao.save(mission));
    }

    @Transactional(readOnly = true)
    public List<MissionDto> getAllMissions() {
        return missionDao.findAll().stream().map(missionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MissionDto getMissionById(int missionId) throws EntityNotFoundException {
        Mission mission = missionDao.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found"));
        return missionMapper.toDto(mission);
    }

    @Transactional
    public void deleteMission(int missionId) {
        missionDao.deleteById(missionId);
    }
}
