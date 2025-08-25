package bureau.release.system.controller;

import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.error.ValidationErrorResponse;
import bureau.release.system.service.impl.HardwareService;
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
            description = "Позволяет получить список всех девайсов или только относящихся к конкретной миссии",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Правильный тип параметра, ошибка в значении",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
            }
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
            description = "Позволяет создать новый девайс, исходя из переданных данных",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное создание",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "Местоположение девайса")),
                    @ApiResponse(responseCode = "400", description = "Неправильне тело девайса",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены указанные данные",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<HardwareDto> createHardware(@RequestBody HardwareDto hardwareData) {
        log.info("CreateHardware: hardwareData={}", hardwareData);
        HardwareDto createdHardware = hardwareService.createHardware(hardwareData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/hardware/" + createdHardware.getId())
                .body(createdHardware);
    }

    @GetMapping("/{hardwareId}")
    @Operation(
            summary = "Получение девайса по id",
            description = "Позволяет получить данные о девайсе, исходя из переданного id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Неправильный id",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найден девайс по id",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public HardwareDto getHardwareById(
            @PathVariable @Parameter(description = "Id запрашиваемого девайса", example = "1") long hardwareId
    ) {
        log.info("GetHardwareById: id={}", hardwareId);
        return hardwareService.getHardwareById(hardwareId);
    }
}
