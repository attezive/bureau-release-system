package bureau.release.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReleaseDto {
    @Schema(description = "Уникальный идентификатор релиза", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Наименование релиза", example = "Release 1")
    @NotBlank
    private String name;

    @Schema(description = "Дата создания релиза", example = "2025-01-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate releaseDate;

    @Schema(description = "Наименование репозитория oci", example = "project/release")
    @NotBlank
    private String ociName;

    @Schema(description = "Тэг артефакта", example = "v1")
    @NotBlank
    private String reference;

    @Schema(description = "digest артефакта в виде sha256", example = "sha256:8...4", accessMode = Schema.AccessMode.READ_ONLY)
    private String digest;

    @Schema(description = "Статус релиза",
            allowableValues = {"CREATED", "DOWNLOADING", "UPLOADING", "COMPLETED", "BUILD_ERROR"},
            accessMode = Schema.AccessMode.READ_ONLY)
    private ReleaseStatusDto status;

    @Schema(description = "Идентификатор релиза для первичной инициализации",
            example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(1)
    private Long originId;

    @Schema(description = "Идентификатор миссии", example = "1")
    @Positive
    private int missionId;

    @Schema(description = "Список содержимого релиза")
    @Valid
    private List<ReleaseContentDto> releaseContent;
}
