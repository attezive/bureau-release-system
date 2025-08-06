package bureau.release.system.controller;

import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.impl.HardwareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/hardware")
@RequiredArgsConstructor
@Tag(name = "Контроллер девайсов", description = "Управление девайсами")
public class HardwareController {
    private final HardwareService hardwareService;

    @GetMapping
    @Operation(
            summary = "Получение списка девайсов",
            description = "Позволяет получить список всех девайсов или только относящихся к конкретной миссии"
    )
    public List<HardwareDto> getHardware(
            @RequestParam(required = false) @Parameter(description = "Id миссии для фильтра девайсов") Integer missionId
    ) {
        log.info("GetHardware: missionId={}", missionId);
        if (missionId == null) {
            return hardwareService.getAllHardware();
        }
        return hardwareService.getHardwareByMissionId(missionId);
    }

    @PostMapping
    @Operation(
            summary = "Создание нового девайса",
            description = "Позволяет создать новый девайс, исходя из переданных данных"
    )
    public HardwareDto createHardware(@RequestBody HardwareDto hardwareData) {
        log.info("CreateHardware: hardwareData={}", hardwareData);
        return hardwareService.createHardware(hardwareData);
    }

    @GetMapping("/{hardwareId}")
    @Operation(
            summary = "Получение девайса по id",
            description = "Позволяет получить данные о девайсе, исходя из переданного id"
    )
    public HardwareDto getHardwareById(
            @PathVariable @Parameter(description = "Id запрашиваемого девайса", example = "1") long hardwareId
    ) {
        log.info("GetHardwareById: id={}", hardwareId);
        return hardwareService.getHardwareById(hardwareId);
    }
}
