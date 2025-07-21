package bureau.release.system.controller;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.Manifest;
import bureau.release.system.service.impl.OciArtifactDownloader;
import bureau.release.system.service.impl.OciArtifactUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

// TEST CONTROLLER ONLY FOR TEST USE SERVICES
@RestController
@RequestMapping("/t")
@RequiredArgsConstructor
public class TestController {
    private final OciArtifactDownloader artifactDownloader;
    private final OciArtifactUploader artifactUploader;
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

    @GetMapping("/b/u/s")
    public String startUpload(){
        String status = artifactUploader.uploadBlob(
                properties.name(),
                new Blob("hrbr.txt",
                        "be072c96381f32a974dac9e53cffc0b914778ef954f799e7e24d730d464476dc",
                        Paths.get("hrbr.txt")));
        return status;
    }
}
