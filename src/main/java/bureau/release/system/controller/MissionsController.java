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
        log.info("GetMissions");
        return missionService.getAllMissions();
    }

    @GetMapping("/{missionId}")
    public MissionDto getMission(@PathVariable int missionId) {
        log.info("GetMission: id={}", missionId);
        return missionService.getMissionById(missionId);
    }

    @PostMapping
    public MissionDto createMission(@RequestBody MissionDto missionData) {
        log.info("CreateMission: missionData={}", missionData);
        return missionService.createMission(missionData);
    }

    @DeleteMapping("/{missionId}")
    public ErrorDto deleteMission(@PathVariable int missionId) {
        log.info("Delete Mission: id={}", missionId);
        missionService.deleteMission(missionId);
        return new ErrorDto("Successfully deleted");
    }
}
