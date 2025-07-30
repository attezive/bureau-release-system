package bureau.release.system.controller;

import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.client.Artifact;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.impl.FirmwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/firmware")
@RequiredArgsConstructor
public class FirmwareController {
    private final FirmwareService firmwareService;
    private final ArtifactDownloader artifactDownloader;

    @GetMapping
    public List<FirmwareDto> getFirmware(@RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "1") int size) {
        log.info("GetFirmware: page={}, size={}", page, size);
        return firmwareService.getAllFirmware(page, size);
    }

    @GetMapping("/{firmwareId}")
    public FirmwareDto getFirmwareById(@PathVariable long firmwareId) {
        log.info("GetFirmwareById: id={}", firmwareId);
        return firmwareService.getFirmwareById(firmwareId);
    }

    @GetMapping("/{firmwareId}/versions")
    public List<Artifact> getFirmwareVersions(@PathVariable long firmwareId) {
        log.info("GetFirmwareVersions: id={}", firmwareId);
        FirmwareDto firmware = firmwareService.getFirmwareById(firmwareId);
        String[] repositoryLink = firmware.getOciName().split("/");
        return artifactDownloader.getArtifacts(repositoryLink[0], repositoryLink[1]);
    }

    @PostMapping
    public FirmwareDto createFirmware(@RequestBody FirmwareDto firmwareData) {
        log.info("CreateFirmware: {}", firmwareData);
        return firmwareService.createFirmware(firmwareData);
    }

    @GetMapping("/types")
    public List<FirmwareTypeDto> getFirmwareTypes() {
        log.info("GetFirmwareTypes");
        return firmwareService.getFirmwareTypes();
    }

}
