package bureau.release.system.controller;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.impl.OciArtifactDownloader;
import bureau.release.system.service.impl.OciArtifactUploader;
import bureau.release.system.service.impl.ReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

// TEST CONTROLLER ONLY FOR TEST USE SERVICES
@Slf4j
@RestController
@RequestMapping("/t")
@RequiredArgsConstructor
public class TestController {
    private final OciArtifactDownloader artifactDownloader;
    private final OciArtifactUploader artifactUploader;
    private final OciRegistryProperties properties;
    private final ReleaseService releaseService;

    @GetMapping
    public String test() {
        ReleaseDto releaseDto = releaseService.getReleaseById(9);
        StreamingResponseBody responseBody = outputStream ->
                artifactDownloader.loadReleaseContent(releaseDto, outputStream);
        log.info("Downloading release with id {}", releaseDto.getId());
        return artifactUploader.uploadBlob(properties.name(), "tar1", responseBody);
    }
}