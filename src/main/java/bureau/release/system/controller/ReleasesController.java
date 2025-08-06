package bureau.release.system.controller;

import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.dto.ReleaseStatusDto;
import bureau.release.system.service.impl.ReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/releases")
@Tag(name = "Контроллер релизов", description = "Управление релизами")
public class ReleasesController {
    private final ReleaseService releaseService;

    @GetMapping
    @Operation(
            summary = "Получение списка релизов",
            description = "Позволяет получить список прошивок с учетом пагинации и возможности фильтрации по миссии"
    )
    public List<ReleaseDto> getReleases(
            @RequestParam(required = false, defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Размер страницы") int size,
            @RequestParam(required = false) @Parameter(description = "Id миссии для фильтра релизов") Integer missionId
    ) {
        log.info("GetReleases: page={}, size={}, missionId={}", page, size, missionId);
        return releaseService.getAllReleases(page, size, missionId);
    }

    @PostMapping
    @Operation(
            summary = "Создание нового релиза",
            description = "Позволяет создать новый релиз, исходя из переданных данных"
    )
    public ReleaseDto createRelease(@RequestBody ReleaseDto releaseData) {
        ReleaseDto release = releaseService.createRelease(releaseData);
        log.info("CreateRelease: releaseData={}", release);
        return release;
    }

    @GetMapping("/{releaseId}")
    @Operation(
            summary = "Получение релиза по id",
            description = "Позволяет получить данные о релизе, исходя из переданного id"
    )
    public ReleaseDto getReleaseById(
            @PathVariable @Parameter(description = "Id запрашиваемого релиза", example = "1") long releaseId
    ) {
        log.info("GetReleaseById: id={}", releaseId);
        return releaseService.getReleaseById(releaseId);
    }

    @PostMapping("/{releaseId}")
    @Operation(
            summary = "Выгрузка на Harbor собранного релиза по id",
            description = "Позволяет собрать и выгрузить на Harbor релиз, исходя из переданного id"
    )
    public ReleaseDto uploadHarbor(
            @PathVariable @Parameter(description = "Id выгружаемого релиза", example = "1") long releaseId
    ) {
        log.info("Upload to Harbor: releaseId = {}", releaseId);
        return releaseService.uploadReleaseToHarbor(releaseId);
    }

    @GetMapping(value = "/{releaseId}/tar", produces = "application/tar")
    @Operation(
            summary = "Выгрузка клиенту собранного релиза по id",
            description = "Позволяет собрать и потоково выгрузить клиенту релиз, исходя из переданного id"
    )
    public ResponseEntity<StreamingResponseBody> getTar(
            @PathVariable @Parameter(description = "Id выгружаемого релиза", example = "1") long releaseId
    ) {
        log.info("GetTar: releaseId={}", releaseId);
        ReleaseDto releaseDto = releaseService.getReleaseById(releaseId);
        StreamingResponseBody responseBody = releaseService.getTar(releaseId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="+releaseDto.getName()+".tar")
                .contentType(MediaType.parseMediaType("application/tar"))
                .body(responseBody);
    }

    @GetMapping("/statuses")
    @Operation(
            summary = "Получение списка статусов релизов",
            description = "Позволяет получить список статусов релизов"
    )
    public List<ReleaseStatusDto> getReleaseStatuses() {
        log.info("GetReleaseStatuses");
        return releaseService.getReleaseStatuses();
    }
}
