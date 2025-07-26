package bureau.release.system.controller;

import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.impl.HardwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/hardware")
@RequiredArgsConstructor
public class HardwareController {
    private final HardwareService hardwareService;

    @GetMapping
    public List<HardwareDto> getHardware(@RequestParam(required = false) Integer missionId) {
        log.debug("getHardware");
        if (missionId == null) {
            return hardwareService.getAllHardware();
        }
        return hardwareService.getHardwareByMissionId(missionId);
    }

    @GetMapping("/{hardwareId}")
    public HardwareDto getHardwareById(@PathVariable int hardwareId) {
        log.debug("getHardwareById {}", hardwareId);
        return hardwareService.getHardwareById(hardwareId);
    }

    @PostMapping
    public HardwareDto createHardware(@RequestBody HardwareDto hardwareData) {
        HardwareDto hardware = hardwareService.createHardware(hardwareData);
        log.debug("createHardware {}", hardware);
        return hardware;
    }
}
