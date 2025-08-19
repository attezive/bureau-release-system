package bureau.release.system.controller;

import bureau.release.system.config.SecurityWebConfig;
import bureau.release.system.service.dto.HardwareDto;
import bureau.release.system.service.impl.HardwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HardwareController.class)
@Import(SecurityWebConfig.class)
class HardwareControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HardwareService hardwareService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getHardware_whenAdminAuthority_thenReturnHardwareList() throws Exception {
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
    @WithMockUser(authorities = "admin")
    void getHardwareByMission_whenAdminAuthority_thenReturnHardware() throws Exception {
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
    @WithMockUser(authorities = "admin")
    void getHardwareById_whenAdminAuthority_thenReturnHardware() throws Exception {
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
    @WithMockUser(authorities = "admin")
    void createHardware_whenAdminAuthority_thenCreateAndReturnHardware() throws Exception {
        HardwareDto requestHardwareDto = new HardwareDto();
        requestHardwareDto.setName("Hardware");
        requestHardwareDto.setFirmwareIds(List.of(3L, 4L));

        HardwareDto responseHardwareDto = new HardwareDto(1L, "Hardware", List.of(1, 2), List.of(3L, 4L));

        Mockito.when(hardwareService.createHardware(requestHardwareDto)).thenReturn(responseHardwareDto);

        mockMvc.perform(post("/hardware")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestHardwareDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/hardware/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hardware"))
                .andExpect(jsonPath("$.missionsIds.[0]").value(1))
                .andExpect(jsonPath("$.missionsIds.[1]").value(2))
                .andExpect(jsonPath("$.firmwareIds.[0]").value(3L))
                .andExpect(jsonPath("$.firmwareIds.[1]").value(4L));

        Mockito.verify(hardwareService, Mockito.times(1)).createHardware(requestHardwareDto);
    }

    @Test
    @WithMockUser(authorities = "user")
    void createHardware_whenUserAuthority_thenCreateAndReturnHardware() throws Exception {
        HardwareDto requestHardwareDto = new HardwareDto();
        requestHardwareDto.setName("Hardware");
        requestHardwareDto.setFirmwareIds(List.of(3L, 4L));

        HardwareDto responseHardwareDto = new HardwareDto(1L, "Hardware", List.of(1, 2), List.of(3L, 4L));

        Mockito.when(hardwareService.createHardware(requestHardwareDto)).thenReturn(responseHardwareDto);

        mockMvc.perform(post("/hardware")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestHardwareDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/hardware/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hardware"))
                .andExpect(jsonPath("$.missionsIds.[0]").value(1))
                .andExpect(jsonPath("$.missionsIds.[1]").value(2))
                .andExpect(jsonPath("$.firmwareIds.[0]").value(3L))
                .andExpect(jsonPath("$.firmwareIds.[1]").value(4L));

        Mockito.verify(hardwareService, Mockito.times(1)).createHardware(requestHardwareDto);
    }

    @Test
    @WithAnonymousUser
    void getAnyMethod_whenAnonymous_thenUnauthorized() throws Exception {

        mockMvc.perform(get("/hardware"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/hardware")
                        .param("missionId", "3"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/hardware/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/hardware"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "user")
    void getAnyMethod_whenUser_thenIs2xxSuccessful() throws Exception {

        mockMvc.perform(get("/hardware"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/hardware")
                        .param("missionId", "3"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/hardware/1"))
                .andExpect(status().is2xxSuccessful());
    }
}