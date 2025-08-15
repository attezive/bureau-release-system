package bureau.release.system.controller;

import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.dto.error.ValidationErrorResponse;
import bureau.release.system.service.impl.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MissionDto>> getMissions() {
        log.info("GetMissions");
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    @PostMapping
    @Operation(
            summary = "Создание новой миссии",
            description = "Позволяет создать новую миссию, исходя из переданных данных",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное создание",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "Местоположение миссии")),
                    @ApiResponse(responseCode = "400", description = "Неправильне тело миссии",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены указанные данные",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<MissionDto> createMission(@RequestBody MissionDto missionData) {
        log.info("CreateMission: missionData={}", missionData);
        MissionDto createdMission = missionService.createMission(missionData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/missions/" + createdMission.getId())
                .body(createdMission);
    }

    @GetMapping("/{missionId}")
    @Operation(
            summary = "Получение миссии по id",
            description = "Позволяет получить данные о миссии, исходя из переданного id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Неправильный id",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найден девайс по id",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<MissionDto> getMissionById(
            @PathVariable @Parameter(description = "Id запрашиваемой миссии", example = "1") int missionId
    ) {
        log.info("GetMission: id={}", missionId);
        return ResponseEntity.ok(missionService.getMissionById(missionId));
    }

    @DeleteMapping("/{missionId}")
    @Operation(
            summary = "Удаление миссии по id",
            description = "Позволяет удалить данные о миссии, исходя из переданного id. Исходя из особеннойстей " +
                    "устройства базы данных, ошибка не будет возвращена даже при неправильном id"
    )
    public ResponseEntity<ErrorDto> deleteMission(
            @PathVariable @Parameter(description = "Id удаляемой миссии", example = "1") int missionId
    ) {
        log.info("Delete Mission: id={}", missionId);
        missionService.deleteMission(missionId);
        return ResponseEntity.ok(new ErrorDto("Successfully deleted"));
    }
}
