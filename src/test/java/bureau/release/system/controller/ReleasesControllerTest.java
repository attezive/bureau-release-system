package bureau.release.system.controller;

import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseContentDto;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.dto.ReleaseStatusDto;
import bureau.release.system.service.impl.ReleaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReleasesController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReleasesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReleaseService releaseService;

    @Test
    void getReleases() throws Exception {
        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto firstReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                "sha256digest1", ReleaseStatusDto.COMPLETED, 1L, 1, List.of(firstReleaseContent));

        FirmwareVersionDto thirdVersion = new FirmwareVersionDto(3L, "v2", 1L, 2L, 1L);
        ReleaseContentDto secondReleaseContent = new ReleaseContentDto(1L, List.of(thirdVersion));
        ReleaseDto secondReleaseDto = new ReleaseDto(2L, "Second Release", LocalDate.now(), "project/repo", "v2",
                null, ReleaseStatusDto.CREATED, 2L, 2, List.of(secondReleaseContent));

        List<ReleaseDto> releaseDtoList = List.of(firstReleaseDto, secondReleaseDto);

        Mockito.when(releaseService.getAllReleases(0, 10, null)).thenReturn(releaseDtoList);

        mockMvc.perform(get("/releases")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("First Release"))
                .andExpect(jsonPath("$[0].releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].ociName").value("project/repo"))
                .andExpect(jsonPath("$[0].reference").value("v1"))
                .andExpect(jsonPath("$[0].digest").value("sha256digest1"))
                .andExpect(jsonPath("$[0].status").value(ReleaseStatusDto.COMPLETED.name()))
                .andExpect(jsonPath("$[0].originId").value(1L))
                .andExpect(jsonPath("$[0].missionId").value(1))
                .andExpect(jsonPath("$[0].releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].id").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].releaseId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].id").value(2L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].firmwareId").value(2L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].releaseId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].hardwareId").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second Release"))
                .andExpect(jsonPath("$[1].releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[1].ociName").value("project/repo"))
                .andExpect(jsonPath("$[1].reference").value("v2"))
                .andExpect(jsonPath("$[1].digest").doesNotExist())
                .andExpect(jsonPath("$[1].status").value(ReleaseStatusDto.CREATED.name()))
                .andExpect(jsonPath("$[1].originId").value(2L))
                .andExpect(jsonPath("$[1].missionId").value(2))
                .andExpect(jsonPath("$[1].releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[0].id").value(3L))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v2"))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[0].releaseId").value(2L))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[1].releaseContent.[0].firmwareVersions.[1].id").doesNotExist());

        Mockito.verify(releaseService, Mockito.times(1)).getAllReleases(0, 10, null);
    }

    @Test
    void getReleasesDefault() throws Exception {
        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto firstReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                "sha256digest1", ReleaseStatusDto.COMPLETED, 1L, 1, List.of(firstReleaseContent));

        FirmwareVersionDto thirdVersion = new FirmwareVersionDto(3L, "v2", 1L, 2L, 1L);
        ReleaseContentDto secondReleaseContent = new ReleaseContentDto(1L, List.of(thirdVersion));
        ReleaseDto secondReleaseDto = new ReleaseDto(2L, "Second Release", LocalDate.now(), "project/repo", "v2",
                null, ReleaseStatusDto.CREATED, 2L, 2, List.of(secondReleaseContent));

        List<ReleaseDto> releaseDtoList = List.of(firstReleaseDto);

        Mockito.when(releaseService.getAllReleases(0, 1, null)).thenReturn(releaseDtoList);

        mockMvc.perform(get("/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("First Release"))
                .andExpect(jsonPath("$[0].releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].ociName").value("project/repo"))
                .andExpect(jsonPath("$[0].reference").value("v1"))
                .andExpect(jsonPath("$[0].digest").value("sha256digest1"))
                .andExpect(jsonPath("$[0].status").value(ReleaseStatusDto.COMPLETED.name()))
                .andExpect(jsonPath("$[0].originId").value(1L))
                .andExpect(jsonPath("$[0].missionId").value(1))
                .andExpect(jsonPath("$[0].releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].id").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].releaseId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].id").value(2L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].firmwareId").value(2L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].releaseId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].hardwareId").value(1L))
                .andExpect(jsonPath("$[1].id").doesNotExist());

        Mockito.verify(releaseService, Mockito.times(1)).getAllReleases(0, 1, null);
    }

    @Test
    void getReleasesByMission() throws Exception {
        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto firstReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                "sha256digest1", ReleaseStatusDto.COMPLETED, 1L, 1, List.of(firstReleaseContent));

        FirmwareVersionDto thirdVersion = new FirmwareVersionDto(3L, "v2", 1L, 2L, 1L);
        ReleaseContentDto secondReleaseContent = new ReleaseContentDto(1L, List.of(thirdVersion));
        ReleaseDto secondReleaseDto = new ReleaseDto(2L, "Second Release", LocalDate.now(), "project/repo", "v2",
                null, ReleaseStatusDto.CREATED, 2L, 2, List.of(secondReleaseContent));

        List<ReleaseDto> releaseDtoList = List.of(secondReleaseDto);

        Mockito.when(releaseService.getAllReleases(0, 10, 2)).thenReturn(releaseDtoList);

        mockMvc.perform(get("/releases")
                        .param("page", "0")
                        .param("size", "10")
                        .param("missionId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Second Release"))
                .andExpect(jsonPath("$[0].releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].ociName").value("project/repo"))
                .andExpect(jsonPath("$[0].reference").value("v2"))
                .andExpect(jsonPath("$[0].digest").doesNotExist())
                .andExpect(jsonPath("$[0].status").value(ReleaseStatusDto.CREATED.name()))
                .andExpect(jsonPath("$[0].originId").value(2L))
                .andExpect(jsonPath("$[0].missionId").value(2))
                .andExpect(jsonPath("$[0].releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].id").value(3L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v2"))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].releaseId").value(2L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$[0].releaseContent.[0].firmwareVersions.[1].id").doesNotExist())
                .andExpect(jsonPath("$[1].id").doesNotExist());

        Mockito.verify(releaseService, Mockito.times(1)).getAllReleases(0, 10, 2);
    }

    @Test
    void getReleaseById() throws Exception {
        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto firstReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                "sha256digest1", ReleaseStatusDto.COMPLETED, 1L, 1, List.of(firstReleaseContent));

        Mockito.when(releaseService.getReleaseById(1L)).thenReturn(firstReleaseDto);

        mockMvc.perform(get("/releases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("First Release"))
                .andExpect(jsonPath("$.releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.reference").value("v1"))
                .andExpect(jsonPath("$.digest").value("sha256digest1"))
                .andExpect(jsonPath("$.status").value(ReleaseStatusDto.COMPLETED.name()))
                .andExpect(jsonPath("$.originId").value(1L))
                .andExpect(jsonPath("$.missionId").value(1))
                .andExpect(jsonPath("$.releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].id").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].id").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareId").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].hardwareId").value(1L));

        Mockito.verify(releaseService, Mockito.times(1)).getReleaseById(1L);
    }

    @Test
    void getReleaseStatuses() throws Exception {
        List<ReleaseStatusDto> releaseStatusList = List.of(ReleaseStatusDto.values());

        Mockito.when(releaseService.getReleaseStatuses()).thenReturn(releaseStatusList);

        mockMvc.perform(get("/releases/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0]").value(ReleaseStatusDto.CREATED.name()))
                .andExpect(jsonPath("$[1]").value(ReleaseStatusDto.DOWNLOADING.name()))
                .andExpect(jsonPath("$[2]").value(ReleaseStatusDto.UPLOADING.name()))
                .andExpect(jsonPath("$[3]").value(ReleaseStatusDto.COMPLETED.name()))
                .andExpect(jsonPath("$[4]").value(ReleaseStatusDto.BUILD_DOWNLOADING_ERROR.name()));

        Mockito.verify(releaseService, Mockito.times(1)).getReleaseStatuses();
    }

    @Test
    void uploadHarbor() throws Exception {
        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto firstReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                "sha256digest1", ReleaseStatusDto.COMPLETED, 1L, 1, List.of(firstReleaseContent));

        Mockito.when(releaseService.uploadRelease(1L)).thenReturn(firstReleaseDto);

        mockMvc.perform(post("/releases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("First Release"))
                .andExpect(jsonPath("$.releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.reference").value("v1"))
                .andExpect(jsonPath("$.digest").value("sha256digest1"))
                .andExpect(jsonPath("$.status").value(ReleaseStatusDto.COMPLETED.name()))
                .andExpect(jsonPath("$.originId").value(1L))
                .andExpect(jsonPath("$.missionId").value(1))
                .andExpect(jsonPath("$.releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].id").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].id").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareId").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].hardwareId").value(1L));

        Mockito.verify(releaseService, Mockito.times(1)).uploadRelease(1L);
    }

    @Test
    void createRelease() throws Exception {
        FirmwareVersionDto requestFirstVersion = new FirmwareVersionDto();
        requestFirstVersion.setFirmwareId(1L);
        requestFirstVersion.setFirmwareVersion("v1");
        FirmwareVersionDto requestSecondVersion = new FirmwareVersionDto();
        requestSecondVersion.setFirmwareId(1L);
        requestSecondVersion.setFirmwareVersion("v1");
        ReleaseContentDto requestReleaseContent = new ReleaseContentDto(1L,
                List.of(requestFirstVersion, requestSecondVersion));
        ReleaseDto requestReleaseDto = new ReleaseDto();
        requestReleaseDto.setName("First Release");
        requestReleaseDto.setReleaseDate(LocalDate.now());
        requestReleaseDto.setOciName("project/repo");
        requestReleaseDto.setReference("v1");
        requestReleaseDto.setReleaseContent(List.of(requestReleaseContent));

        FirmwareVersionDto firstVersion = new FirmwareVersionDto(1L, "v1", 1L, 1L, 1L);
        FirmwareVersionDto secondVersion = new FirmwareVersionDto(2L, "v1", 2L, 1L, 1L);
        ReleaseContentDto firstReleaseContent = new ReleaseContentDto(1L, List.of(firstVersion, secondVersion));
        ReleaseDto responseReleaseDto = new ReleaseDto(1L, "First Release", LocalDate.now(), "project/repo", "v1",
                null, ReleaseStatusDto.CREATED, 1L, 1, List.of(firstReleaseContent));

        Mockito.when(releaseService.createRelease(requestReleaseDto)).thenReturn(responseReleaseDto);

        mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestReleaseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("First Release"))
                .andExpect(jsonPath("$.releaseDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.reference").value("v1"))
                .andExpect(jsonPath("$.digest").doesNotExist())
                .andExpect(jsonPath("$.status").value(ReleaseStatusDto.CREATED.name()))
                .andExpect(jsonPath("$.originId").value(1L))
                .andExpect(jsonPath("$.missionId").value(1))
                .andExpect(jsonPath("$.releaseContent.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].id").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].firmwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[0].hardwareId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].id").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareVersion").value("v1"))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].firmwareId").value(2L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].releaseId").value(1L))
                .andExpect(jsonPath("$.releaseContent.[0].firmwareVersions.[1].hardwareId").value(1L));

        Mockito.verify(releaseService, Mockito.times(1)).createRelease(requestReleaseDto);
    }
}