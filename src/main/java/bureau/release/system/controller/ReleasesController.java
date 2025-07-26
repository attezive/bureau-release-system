package bureau.release.system.controller;

import bureau.release.system.model.ReleaseStatus;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.impl.ReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/releases")
public class ReleasesController {
    private final ReleaseService releaseService;

    @GetMapping
    public List<ReleaseDto> getReleases(@RequestParam(required = false, defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "1") int size,
                              @RequestParam(required = false) Integer missionId) {
        log.debug("getReleases");
        return releaseService.getAllReleases(page, size, missionId);
    }

    @GetMapping("/{releaseId}")
    public ReleaseDto getReleaseById(@PathVariable int releaseId) {
        log.debug("getReleaseById {}", releaseId);
        return releaseService.getReleaseById(releaseId);
    }

    @PostMapping
    public ReleaseDto createRelease(@RequestBody ReleaseDto releaseData) {
        ReleaseDto release = releaseService.createRelease(releaseData);
        log.debug("createRelease {}", release);
        return release;
    }

    @GetMapping("/statuses")
    public List<ReleaseStatus> getReleaseStatuses() {
        log.debug("getReleaseStatuses");
        return releaseService.getReleaseStatuses();
    }
}
