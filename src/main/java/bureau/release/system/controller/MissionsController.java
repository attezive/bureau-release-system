package bureau.release.system.controller;

import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.impl.MissionHardwareService;
import bureau.release.system.service.impl.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionsController {
    private final MissionService missionService;
    private final MissionHardwareService missionHardwareService;

    @GetMapping
    public String getMissions() {
        log.debug("getMissions");
        return missionService.getAllMissions().toString();
    }

    @GetMapping("/{missionId}")
    public String getMission(@PathVariable int missionId) {
        log.debug("getMission {}", missionId);
        return missionService.getMissionById(missionId).toString();
    }

    @PostMapping
    public String createMission(@RequestBody MissionDto missionData) {
        MissionDto mission = missionService.createMission(missionData);
        log.debug("Create Mission: {}", mission);
        for (long hardwareId : missionData.getHardwareIds()) {
            missionHardwareService.addHardwareToMission(mission.getId(), hardwareId);
        }
        mission.setHardwareIds(missionData.getHardwareIds());
        return mission.toString();
    }

    @DeleteMapping("/{missionId}")
    public String deleteMission(@PathVariable int missionId, @RequestParam(required = false) String key) {
        //TODO Add key-token generation and check it
        if (key == null) {
            return "Token For Accept Mission Delete";
        }
        log.debug("Delete Mission: {}", missionId);
        missionService.deleteMission(missionId);
        return "Ok";
    }
}
