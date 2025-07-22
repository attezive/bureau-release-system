package bureau.release.system.controller;

import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.impl.HardwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/hardware")
@RequiredArgsConstructor
public class HardwareController {
    private final HardwareService hardwareService;

    @GetMapping
    public String getHardware(@RequestParam(required = false) Integer missionId) {
        log.debug("getHardware");
        if (missionId == null) {
            return hardwareService.getAllHardware().toString();
        }
        return hardwareService.getHardwareByMission(missionId).toString();
    }

    @GetMapping("/{hardwareId}")
    public String getHardwareById(@PathVariable int hardwareId) {
        log.debug("getHardwareById {}", hardwareId);
        return hardwareService.getHardwareById(hardwareId).toString();
    }

    @PostMapping
    public String createHardware(@RequestBody HardwareDto hardwareData) {
        log.debug("createHardware {}", hardwareData);
        return hardwareService.createHardware(hardwareData).toString();
    }
}
