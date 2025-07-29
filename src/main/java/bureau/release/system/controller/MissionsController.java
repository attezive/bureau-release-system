package bureau.release.system.controller;

import bureau.release.system.service.dto.ErrorDto;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.impl.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionsController {
    private final MissionService missionService;

    @GetMapping
    public List<MissionDto> getMissions() {
        log.info("getMissions");
        return missionService.getAllMissions();
    }

    @GetMapping("/{missionId}")
    public MissionDto getMission(@PathVariable int missionId) {
        log.info("getMission {}", missionId);
        return missionService.getMissionById(missionId);
    }

    @PostMapping
    public MissionDto createMission(@RequestBody MissionDto missionData) {
        MissionDto mission = missionService.createMission(missionData);
        log.info("Create Mission: {}", mission);
        return mission;
    }

    @DeleteMapping("/{missionId}")
    public ErrorDto deleteMission(@PathVariable int missionId) {
        log.info("Delete Mission: {}", missionId);
        missionService.deleteMission(missionId);
        return new ErrorDto("Successfully deleted");
    }
}
