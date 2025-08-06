package bureau.release.system.controller;

import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.impl.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Tag(name = "Контроллер миссий", description = "Управление миссиями")
public class MissionsController {
    private final MissionService missionService;

    @GetMapping
    @Operation(
            summary = "Получение списка миссий",
            description = "Позволяет получить список всех миссий"
    )
    public List<MissionDto> getMissions() {
        log.info("GetMissions");
        return missionService.getAllMissions();
    }

    @PostMapping
    @Operation(
            summary = "Создание новой миссии",
            description = "Позволяет создать новую миссию, исходя из переданных данных"
    )
    public MissionDto createMission(@RequestBody MissionDto missionData) {
        log.info("CreateMission: missionData={}", missionData);
        return missionService.createMission(missionData);
    }

    @GetMapping("/{missionId}")
    @Operation(
            summary = "Получение миссии по id",
            description = "Позволяет получить данные о миссии, исходя из переданного id"
    )
    public MissionDto getMissionById(
            @PathVariable @Parameter(description = "Id запрашиваемой миссии", example = "1") int missionId
    ) {
        log.info("GetMission: id={}", missionId);
        return missionService.getMissionById(missionId);
    }

    @DeleteMapping("/{missionId}")
    @Operation(
            summary = "Удаление миссии по id",
            description = "Позволяет удалить данные о миссии, исходя из переданного id. Исходя из особеннойстей " +
                    "устройства базы данных, ошибка не будет возвращена даже при неправильном id"
    )
    public ErrorDto deleteMission(
            @PathVariable @Parameter(description = "Id удаляемой миссии", example = "1") int missionId
    ) {
        log.info("Delete Mission: id={}", missionId);
        missionService.deleteMission(missionId);
        return new ErrorDto("Successfully deleted");
    }
}
