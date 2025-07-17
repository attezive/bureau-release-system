package bureau.release.system.controller;

import bureau.release.system.config.HarborRegistryProperties;
import bureau.release.system.service.dto.Manifest;
import bureau.release.system.service.dto.RepositoryReference;
import bureau.release.system.service.impl.HarborArtifactLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/firmware")
@RequiredArgsConstructor
public class FirmwareController {
    private final HarborArtifactLoader harborArtifactLoader;
    private final HarborRegistryProperties properties;

    @GetMapping
    public String getAllFirmware() {
        return "getAllFirmware";
    }

    @GetMapping("/{firmwareId}")
    public String getFirmware(@PathVariable String firmwareId) {
        Manifest manifest = harborArtifactLoader.loadManifest(
                RepositoryReference.parse(properties.source(), firmwareId));

        return manifest.content().toString();
    }
}
