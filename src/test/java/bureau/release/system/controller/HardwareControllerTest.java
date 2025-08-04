package bureau.release.system.controller;

import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.impl.HardwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HardwareController.class)
@AutoConfigureMockMvc(addFilters = false)
class HardwareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HardwareService hardwareService;

    @Test
    void getHardware() throws Exception {
        HardwareDto firstHardwareDto = new HardwareDto(1L, "First Hardware", List.of(1, 2), List.of(3L, 4L));
        HardwareDto secondHardwareDto = new HardwareDto(2L, "Second Hardware", List.of(2, 3), List.of(1L));
        HardwareDto thirdHardwareDto = new HardwareDto(3L, "Third Hardware", List.of(3), List.of(2L));

        List<HardwareDto> hardwareDtoList = List.of(firstHardwareDto, secondHardwareDto, thirdHardwareDto);

        Mockito.when(hardwareService.getAllHardware()).thenReturn(hardwareDtoList);

        mockMvc.perform(get("/hardware"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("First Hardware"))
                .andExpect(jsonPath("$[0].missionsIds.[0]").value(1))
                .andExpect(jsonPath("$[0].missionsIds.[1]").value(2))
                .andExpect(jsonPath("$[0].firmwareIds.[0]").value(3L))
                .andExpect(jsonPath("$[0].firmwareIds.[1]").value(4L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second Hardware"))
                .andExpect(jsonPath("$[1].missionsIds.[0]").value(2))
                .andExpect(jsonPath("$[1].missionsIds.[1]").value(3))
                .andExpect(jsonPath("$[1].firmwareIds.[0]").value(1L))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].name").value("Third Hardware"))
                .andExpect(jsonPath("$[2].missionsIds.[0]").value(3))
                .andExpect(jsonPath("$[2].firmwareIds.[0]").value(2L));

        Mockito.verify(hardwareService, Mockito.times(1)).getAllHardware();
    }

    @Test
    void getHardwareByMission() throws Exception {
        HardwareDto firstHardwareDto = new HardwareDto(1L, "First Hardware", List.of(1, 2), List.of(3L, 4L));
        HardwareDto secondHardwareDto = new HardwareDto(2L, "Second Hardware", List.of(2, 3), List.of(1L));
        HardwareDto thirdHardwareDto = new HardwareDto(3L, "Third Hardware", List.of(3), List.of(2L));

        List<HardwareDto> hardwareDtoList = List.of(secondHardwareDto, thirdHardwareDto);

        Mockito.when(hardwareService.getHardwareByMissionId(3)).thenReturn(hardwareDtoList);

        mockMvc.perform(get("/hardware")
                        .param("missionId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Second Hardware"))
                .andExpect(jsonPath("$[0].missionsIds.[0]").value(2))
                .andExpect(jsonPath("$[0].missionsIds.[1]").value(3))
                .andExpect(jsonPath("$[0].firmwareIds.[0]").value(1L))
                .andExpect(jsonPath("$[1].id").value(3L))
                .andExpect(jsonPath("$[1].name").value("Third Hardware"))
                .andExpect(jsonPath("$[1].missionsIds.[0]").value(3))
                .andExpect(jsonPath("$[1].firmwareIds.[0]").value(2L))
                .andExpect(jsonPath("$[2].id").doesNotExist());

        Mockito.verify(hardwareService, Mockito.times(1)).getHardwareByMissionId(3);
    }

    @Test
    void getHardwareById() throws Exception {
        HardwareDto hardwareDto = new HardwareDto(1L, "Hardware", List.of(1, 2), List.of(3L, 4L));
        Mockito.when(hardwareService.getHardwareById(1L)).thenReturn(hardwareDto);

        mockMvc.perform(get("/hardware/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hardware"))
                .andExpect(jsonPath("$.missionsIds.[0]").value(1))
                .andExpect(jsonPath("$.missionsIds.[1]").value(2))
                .andExpect(jsonPath("$.firmwareIds.[0]").value(3L))
                .andExpect(jsonPath("$.firmwareIds.[1]").value(4L));

        Mockito.verify(hardwareService, Mockito.times(1)).getHardwareById(1L);
    }

    @Test
    void createHardware() throws Exception {
        HardwareDto requestHardwareDto = new HardwareDto();
        requestHardwareDto.setName("Hardware");
        requestHardwareDto.setFirmwareIds(List.of(3L, 4L));

        HardwareDto responseHardwareDto = new HardwareDto(1L, "Hardware", List.of(1, 2), List.of(3L, 4L));

        Mockito.when(hardwareService.createHardware(requestHardwareDto)).thenReturn(responseHardwareDto);

        mockMvc.perform(post("/hardware")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestHardwareDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hardware"))
                .andExpect(jsonPath("$.missionsIds.[0]").value(1))
                .andExpect(jsonPath("$.missionsIds.[1]").value(2))
                .andExpect(jsonPath("$.firmwareIds.[0]").value(3L))
                .andExpect(jsonPath("$.firmwareIds.[1]").value(4L));

        Mockito.verify(hardwareService, Mockito.times(1)).createHardware(requestHardwareDto);
    }
}