package bureau.release.system.controller;

import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.impl.FirmwareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/firmware")
@RequiredArgsConstructor
@Tag(name = "Контроллер прошивок", description = "Управление прошивками")
public class FirmwareController {
    private final FirmwareService firmwareService;
    private final ArtifactDownloader artifactDownloader;

    @GetMapping
    @Operation(
            summary = "Получение списка прошивок",
            description = "Позволяет получить список прошивок с учетом пагинации"
    )
    public List<FirmwareDto> getFirmware(
            @RequestParam(required = false, defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Размер страницы") int size
    ) {
        log.info("GetFirmware: page={}, size={}", page, size);
        return firmwareService.getAllFirmware(page, size);
    }

    @PostMapping
    @Operation(
            summary = "Создание новой прошивки",
            description = "Позволяет создать новую прошивку, исходя из переданных данных"
    )
    public FirmwareDto createFirmware(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные создаваемой прошивки")
            FirmwareDto firmwareData
    ) {
        log.info("CreateFirmware: {}", firmwareData);
        return firmwareService.createFirmware(firmwareData);
    }

    @GetMapping("/{firmwareId}")
    @Operation(
            summary = "Получение прошивки по id",
            description = "Позволяет получить данные о прошивке, исходя из переданного id"
    )
    public FirmwareDto getFirmwareById(
            @PathVariable @Parameter(description = "Id запрашиваемой прошивки", example = "1") long firmwareId
    ) {
        log.info("GetFirmwareById: id={}", firmwareId);
        return firmwareService.getFirmwareById(firmwareId);
    }

    @GetMapping("/{firmwareId}/versions")
    @Operation(
            summary = "Получение списка манифестов артефактов по id прошивки",
            description = "Позволяет получить данные о версиях/артефактах для прошивки, исходя из переданного id"
    )
    public List<Manifest> getFirmwareVersions(
            @PathVariable @Parameter(description = "Id запрашиваемой прошивки", example = "1") long firmwareId
    ) {
        log.info("GetFirmwareVersions: id={}", firmwareId);
        FirmwareDto firmware = firmwareService.getFirmwareById(firmwareId);
        return artifactDownloader.getArtifacts(firmware.getOciName());
    }

    @GetMapping("/types")
    @Operation(
            summary = "Получение списка типов прошивок",
            description = "Позволяет получить список типов прошивок"
    )
    public List<FirmwareTypeDto> getFirmwareTypes() {
        log.info("GetFirmwareTypes");
        return firmwareService.getFirmwareTypes();
    }
}
