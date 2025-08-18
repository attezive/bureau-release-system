package bureau.release.system.controller;

import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.dto.client.ArtifactWebhook;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.error.ValidationErrorResponse;
import bureau.release.system.service.impl.FirmwareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Webhook;
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
@RequestMapping("/firmware")
@RequiredArgsConstructor
@Tag(name = "Контроллер прошивок", description = "Управление прошивками")
public class FirmwareController {
    private final FirmwareService firmwareService;

    @GetMapping
    @Operation(
            summary = "Получение списка прошивок",
            description = "Позволяет получить список прошивок с учетом пагинации",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Правильный тип параметра, ошибка в значении",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
            }
    )
    public ResponseEntity<List<FirmwareDto>> getFirmware(
            @RequestParam(required = false, defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Размер страницы") int size
    ) {
        log.info("GetFirmware: page={}, size={}", page, size);
        return ResponseEntity.ok(firmwareService.getAllFirmware(page, size));
    }

    @PostMapping
    @Operation(
            summary = "Создание новой прошивки",
            description = "Позволяет создать новую прошивку, исходя из переданных данных. (Admin rules only)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное создание",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "Местоположение прошивки")),
                    @ApiResponse(responseCode = "400", description = "Неправильне тело прошивки",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены указанные данные",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<FirmwareDto> createFirmware(@RequestBody FirmwareDto firmwareData) {
        log.info("CreateFirmware: {}", firmwareData);
        FirmwareDto createdFirmware = firmwareService.createFirmware(firmwareData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/firmware/" + createdFirmware.getId())
                .body(createdFirmware);
    }

    @GetMapping("/{firmwareId}")
    @Operation(
            summary = "Получение прошивки по id",
            description = "Позволяет получить данные о прошивке, исходя из переданного id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Неправильный id",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдена прошивка по id",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<FirmwareDto> getFirmwareById(
            @PathVariable @Parameter(description = "Id запрашиваемой прошивки", example = "1") long firmwareId
    ) {
        log.info("GetFirmwareById: id={}", firmwareId);
        return ResponseEntity.ok(firmwareService.getFirmwareById(firmwareId));
    }

    @GetMapping("/{firmwareId}/versions")
    @Operation(
            summary = "Получение списка манифестов артефактов по id прошивки",
            description = "Позволяет получить данные о версиях/артефактах для прошивки, исходя из переданного id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Неправильный id",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены версии указанной прошивки",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<List<Manifest>> getFirmwareVersions(
            @PathVariable @Parameter(description = "Id запрашиваемой прошивки", example = "1") long firmwareId
    ) {
        log.info("GetFirmwareVersions: id={}", firmwareId);
        return ResponseEntity.ok(firmwareService.getFirmwareVersions(firmwareId));
    }

    @GetMapping("/types")
    @Operation(
            summary = "Получение списка типов прошивок",
            description = "Позволяет получить список типов прошивок"
    )
    public ResponseEntity<List<FirmwareTypeDto>> getFirmwareTypes() {
        log.info("GetFirmwareTypes");
        return ResponseEntity.ok(firmwareService.getFirmwareTypes());
    }

    @PostMapping("/harbor-webhook")
    @Webhook(
            name = "Вебхук прошивок с Harbor",
            operation = @Operation(
                    summary = "Получение нового загруженного артефакта, проверка на прошивку и загрузка",
                    description = "Позволяет перехватить информацию о созданном артефакте, и при условии, " +
                            "что он является прошивкой, создать на его базе прошивку в бд"
            ))
    public ResponseEntity<String> loadFirmwareWebhook(@RequestBody ArtifactWebhook payload) {
        log.info("LoadFirmwareWebhook: {}", payload.getType());
        log.debug("Webhook from Harbor: {}", payload);

        if (payload.getType().equals("PUSH_ARTIFACT")) {
            FirmwareDto firmware = firmwareService.hookFirmware(payload);
            log.debug("Hooked firmware: {}", firmware);
            if (firmware != null) {
                log.info("CreateFirmware By Webhook: {}", firmware);
                firmwareService.createFirmware(firmware);
            }
        }

        return ResponseEntity.ok("Load successful");
    }
}
