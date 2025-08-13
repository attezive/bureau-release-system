package bureau.release.system.controller;

import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.FirmwareDto;
import bureau.release.system.service.dto.FirmwareTypeDto;
import bureau.release.system.service.dto.client.LayerAnnotation;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.client.ManifestLayer;
import bureau.release.system.service.impl.FirmwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(FirmwareController.class)
@AutoConfigureMockMvc(addFilters = false)
class FirmwareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FirmwareService firmwareService;

    @MockitoBean
    private ArtifactDownloader artifactDownloader;

    @Test
    void getFirmware() throws Exception {
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
    void getFirmwareByDefault() throws Exception {
        FirmwareDto firstFirmwareDto = new FirmwareDto(1, "First Firmware", "APPLICATION",
                "project/repo", List.of(1L, 2L, 3L));
        FirmwareDto secondFirmwareDto = new FirmwareDto(2, "Second Firmware", "FPGA",
                "project/repo", List.of(2L, 3L));
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
    void getFirmwareById() throws Exception {
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
    void getFirmwareVersions() throws Exception {
        FirmwareDto firmwareDto = new FirmwareDto(1, "Talk", "APPLICATION",
                "project/talk", List.of(1L, 2L, 3L));
        Mockito.when(firmwareService.getFirmwareById(1)).thenReturn(firmwareDto);

        LayerAnnotation firstLayerAnnotation = new LayerAnnotation("hello world.bin");
        LayerAnnotation secondLayerAnnotation = new LayerAnnotation("bye world.bin");
        ManifestLayer firstManifestLayer = new ManifestLayer(MediaType.APPLICATION_OCTET_STREAM.toString(),
                "sha256digest1", 10, firstLayerAnnotation);
        ManifestLayer secondManifestLayer = new ManifestLayer(MediaType.APPLICATION_OCTET_STREAM.toString(),
                "sha256digest2", 8, secondLayerAnnotation);
        Manifest manifest = new Manifest("Talk", "v1", List.of(firstManifestLayer, secondManifestLayer));

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
    void createFirmware() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Firmware"))
                .andExpect(jsonPath("$.type").value("APPLICATION"))
                .andExpect(jsonPath("$.ociName").value("project/repo"))
                .andExpect(jsonPath("$.hardwareIds.[0]").doesNotExist());

        Mockito.verify(firmwareService, Mockito.times(1)).createFirmware(requestFirmware);
    }

    @Test
    void getFirmwareTypes() throws Exception {
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
}