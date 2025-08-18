package bureau.release.system.controller;

import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.dto.client.LayerAnnotations;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.client.ManifestAnnotation;
import bureau.release.system.service.dto.client.ManifestLayer;
import bureau.release.system.service.impl.FirmwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(FirmwareController.class)
class FirmwareControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FirmwareService firmwareService;

    @MockitoBean
    private ArtifactDownloader artifactDownloader;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getFirmware_whenAdminAuthority_thenReturnFirmware() throws Exception {
        FirmwareDto firstFirmwareDto = new FirmwareDto(1, "First Firmware", "APPLICATION",
                "project/repo", List.of(1L, 2L, 3L));
        FirmwareDto secondFirmwareDto = new FirmwareDto(2, "Second Firmware", "FPGA",
                "project/repo", List.of(2L, 3L));
        List<FirmwareDto> firmwareList = List.of(firstFirmwareDto, secondFirmwareDto);

        Mockito.when(firmwareService.getAllFirmware(0, 10)).thenReturn(firmwareList);

        mockMvc.perform(get("/firmware")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("First Firmware"))
                .andExpect(jsonPath("$[0].type").value("APPLICATION"))
                .andExpect(jsonPath("$[0].ociName").value("project/repo"))
                .andExpect(jsonPath("$[0].hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$[0].hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$[0].hardwareIds.[2]").value(3L))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Second Firmware"))
                .andExpect(jsonPath("$[1].type").value("FPGA"))
                .andExpect(jsonPath("$[1].ociName").value("project/repo"))
                .andExpect(jsonPath("$[1].hardwareIds.[0]").value(2L))
                .andExpect(jsonPath("$[1].hardwareIds.[1]").value(3L));

        Mockito.verify(firmwareService, Mockito.times(1)).getAllFirmware(0, 10);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getFirmwareByDefault_whenAdminAuthority_thenReturnFirmware() throws Exception {
        FirmwareDto firstFirmwareDto = new FirmwareDto(1, "First Firmware", "APPLICATION",
                "project/repo", List.of(1L, 2L, 3L));
        List<FirmwareDto> firmwareList = List.of(firstFirmwareDto);

        Mockito.when(firmwareService.getAllFirmware(0, 1)).thenReturn(firmwareList);

        mockMvc.perform(get("/firmware"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("First Firmware"))
                .andExpect(jsonPath("$[0].type").value("APPLICATION"))
                .andExpect(jsonPath("$[0].ociName").value("project/repo"))
                .andExpect(jsonPath("$[0].hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$[0].hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$[0].hardwareIds.[2]").value(3L))
                .andExpect(jsonPath("$[1].id").doesNotExist());

        Mockito.verify(firmwareService, Mockito.times(1)).getAllFirmware(0, 1);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getFirmwareById_whenAdminAuthority_thenReturnFirmware() throws Exception {
        FirmwareDto firmwareDto = new FirmwareDto(1, "Firmware", "APPLICATION",
                "project/repo", List.of(1L, 2L, 3L));
        Mockito.when(firmwareService.getFirmwareById(1)).thenReturn(firmwareDto);

        mockMvc.perform(get("/firmware/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Firmware"))
                .andExpect(jsonPath("$.type").value("APPLICATION"))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$.hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$.hardwareIds.[2]").value(3L));

        Mockito.verify(firmwareService, Mockito.times(1)).getFirmwareById(1);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getFirmwareVersions_whenAdminAuthority_thenReturnVersions() throws Exception {
        FirmwareDto firmwareDto = new FirmwareDto(1, "Talk", "APPLICATION",
                "project/talk", List.of(1L, 2L, 3L));
        Mockito.when(firmwareService.getFirmwareById(1)).thenReturn(firmwareDto);

        LayerAnnotations firstLayerAnnotations = new LayerAnnotations("hello world.bin");
        LayerAnnotations secondLayerAnnotations = new LayerAnnotations("bye world.bin");
        ManifestLayer firstManifestLayer = new ManifestLayer(MediaType.APPLICATION_OCTET_STREAM.toString(),
                "sha256digest1", 10, firstLayerAnnotations);
        ManifestLayer secondManifestLayer = new ManifestLayer(MediaType.APPLICATION_OCTET_STREAM.toString(),
                "sha256digest2", 8, secondLayerAnnotations);
        Manifest manifest = new Manifest("Talk", "v1", List.of(firstManifestLayer, secondManifestLayer),
                new ManifestAnnotation("APPLICATION", null));

        Mockito.when(firmwareService.getFirmwareVersions(firmwareDto.getId())).thenReturn(List.of(manifest));

        mockMvc.perform(get("/firmware/1/versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Talk"))
                .andExpect(jsonPath("$[0].reference").value("v1"))
                .andExpect(jsonPath("$[0].layers.[0].digest").value("sha256digest1"))
                .andExpect(jsonPath("$[0].layers.[1].digest").value("sha256digest2"))
                .andExpect(jsonPath("$[0].layers.[0].annotations.title").value("hello world.bin"))
                .andExpect(jsonPath("$[0].layers.[1].annotations.title").value("bye world.bin"))
                .andExpect(jsonPath("$[1].name").doesNotExist());

        Mockito.verify(firmwareService, Mockito.times(1)).getFirmwareVersions(firmwareDto.getId());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createFirmware_whenAdminAuthority_thenCreateAndReturnFirmware() throws Exception {
        FirmwareDto requestFirmware = new FirmwareDto();
        requestFirmware.setName("Firmware");
        requestFirmware.setType("APPLICATION");
        requestFirmware.setOciName("project/repo");

        FirmwareDto responseFirmware = new FirmwareDto(1, "Firmware", "APPLICATION",
                "project/repo", new ArrayList<>());

        Mockito.when(firmwareService.createFirmware(requestFirmware)).thenReturn(responseFirmware);

        mockMvc.perform(post("/firmware")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFirmware)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/firmware/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Firmware"))
                .andExpect(jsonPath("$.type").value("APPLICATION"))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.hardwareIds.[0]").doesNotExist());

        Mockito.verify(firmwareService, Mockito.times(1)).createFirmware(requestFirmware);
    }

    @Test
    @WithMockUser(authorities = "user")
    void createFirmware_whenUserAuthority_thenNotAuthenticated() throws Exception {
        FirmwareDto requestFirmware = new FirmwareDto();
        requestFirmware.setName("Firmware");
        requestFirmware.setType("APPLICATION");
        requestFirmware.setOciName("project/repo");

        FirmwareDto responseFirmware = new FirmwareDto(1, "Firmware", "APPLICATION",
                "project/repo", new ArrayList<>());

        Mockito.when(firmwareService.createFirmware(requestFirmware)).thenReturn(responseFirmware);

        mockMvc.perform(post("/firmware")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFirmware)))
                .andExpect(status().isForbidden());

        Mockito.verify(firmwareService, Mockito.times(0)).createFirmware(requestFirmware);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getFirmwareTypes_whenAdminAuthority_thenReturnTypes() throws Exception {
        FirmwareTypeDto firstFirmwareType = new FirmwareTypeDto(1, "APPLICATION");
        FirmwareTypeDto secondFirmwareType = new FirmwareTypeDto(2, "FPGA");
        List<FirmwareTypeDto> firstFirmwareTypes = List.of(firstFirmwareType, secondFirmwareType);

        Mockito.when(firmwareService.getFirmwareTypes()).thenReturn(firstFirmwareTypes);

        mockMvc.perform(get("/firmware/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("APPLICATION"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("FPGA"));

        Mockito.verify(firmwareService, Mockito.times(1)).getFirmwareTypes();
    }

    @Test
    @WithAnonymousUser
    void getAnyMethod_whenAnonymous_thenUnauthorized() throws Exception {

        mockMvc.perform(get("/firmware")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/firmware"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/firmware/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/firmware/1/versions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/firmware")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "user")
    void getAnyMethod_whenUser_thenIs2xxSuccessful() throws Exception {

        mockMvc.perform(get("/firmware")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/firmware"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/firmware/1"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/firmware/1/versions"))
                .andExpect(status().is2xxSuccessful());
    }
}