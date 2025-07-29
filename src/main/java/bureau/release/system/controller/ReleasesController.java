package bureau.release.system.controller;

import bureau.release.system.model.ReleaseStatus;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.impl.ReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/releases")
public class ReleasesController {
    private final ReleaseService releaseService;
    private final ArtifactDownloader artifactDownloader;

    @GetMapping
    public List<ReleaseDto> getReleases(@RequestParam(required = false, defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "1") int size,
                              @RequestParam(required = false) Integer missionId) {
        log.info("getReleases");
        return releaseService.getAllReleases(page, size, missionId);
    }

    @GetMapping("/{releaseId}")
    public ReleaseDto getReleaseById(@PathVariable int releaseId) {
        log.info("getReleaseById {}", releaseId);
        return releaseService.getReleaseById(releaseId);
    }

    @GetMapping(value = "/{releaseId}/tar", produces = "application/tar")
    public ResponseEntity<StreamingResponseBody> getTar(@PathVariable int releaseId) {
        log.info("getTar {}", releaseId);
        ReleaseDto releaseDto = releaseService.getReleaseById(releaseId);
        StreamingResponseBody responseBody = outputStream ->
                artifactDownloader.loadReleaseContent(releaseDto, outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="+releaseDto.getName()+".tar")
                .contentType(MediaType.parseMediaType("application/tar"))
                .body(responseBody);
    }

    @GetMapping("/statuses")
    public List<ReleaseStatus> getReleaseStatuses() {
        log.info("getReleaseStatuses");
        return releaseService.getReleaseStatuses();
    }

    @PostMapping
    public ReleaseDto createRelease(@RequestBody ReleaseDto releaseData) {
        ReleaseDto release = releaseService.createRelease(releaseData);
        log.info("createRelease {}", release);
        return release;
    }
}
