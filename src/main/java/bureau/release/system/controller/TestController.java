package bureau.release.system.controller;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.service.dto.Manifest;
import bureau.release.system.service.impl.OciArtifactDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

// TEST CONTROLLER ONLY FOR TEST USE SERVICES
@RestController
@RequestMapping("/t")
@RequiredArgsConstructor
public class TestController {
    private final OciArtifactDownloader artifactDownloader;
    private final OciRegistryProperties properties;

    @GetMapping("/{tId}")
    public String getManifest(@PathVariable String tId) {
        return artifactDownloader.loadManifest(properties.name(), tId).toString();
    }

    @GetMapping("/b/d/{tId}")
    public String getBlob(@PathVariable String tId) {
        Path path = Paths.get("output.txt");
        return artifactDownloader.loadBlob(properties.name(), tId, path).digest();
    }

    @GetMapping("/b/m/{tId}")
    public String getBlobs(@PathVariable String tId) {
        Manifest manifest =  artifactDownloader.loadManifest(properties.name(), tId);
        return artifactDownloader.loadBlobs(properties.name(), manifest, "").toString();
    }
}
