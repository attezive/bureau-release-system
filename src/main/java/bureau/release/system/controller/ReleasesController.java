package bureau.release.system.controller;

import bureau.release.system.model.ReleaseStatus;
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
    public String getReleases(@RequestParam(required = false, defaultValue = "0") int limit,
                              @RequestParam(required = false, defaultValue = "0") int offset,
                              @RequestParam(required = false) String mission) {
        return "1";
    }

    @GetMapping("/{releaseId}")
    public String getReleaseById(@PathVariable int releaseId) {
        return "";
    }

    @PostMapping
    public String createRelease(@RequestBody byte[] releaseData) {
        return "";
    }

    @GetMapping("/statuses")
    public List<ReleaseStatus> getReleaseStatuses() {
        log.debug("getReleaseStatuses");
        return releaseService.getReleaseStatuses();
    }
}
