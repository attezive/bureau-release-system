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
        log.info("GetHardware: missionId={}", missionId);
        if (missionId == null) {
            return hardwareService.getAllHardware();
        }
        return hardwareService.getHardwareByMissionId(missionId);
    }

    @GetMapping("/{hardwareId}")
    public HardwareDto getHardwareById(@PathVariable long hardwareId) {
        log.info("GetHardwareById: id={}", hardwareId);
        return hardwareService.getHardwareById(hardwareId);
    }

    @PostMapping
    public HardwareDto createHardware(@RequestBody HardwareDto hardwareData) {
        log.info("CreateHardware: hardwareData={}", hardwareData);
        return hardwareService.createHardware(hardwareData);
    }
}
