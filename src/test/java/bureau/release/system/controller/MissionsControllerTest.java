package bureau.release.system.controller;

import bureau.release.system.config.SecurityWebConfig;
import bureau.release.system.service.dto.MissionDto;
import bureau.release.system.service.impl.MissionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MissionsController.class)
@Import(SecurityWebConfig.class)
class MissionsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MissionService missionService;

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
    void getMissions_whenAdminAuthority_thenReturnMissions() throws Exception {
        MissionDto firstMissionDto = new MissionDto(1, "First Mission", List.of(1L, 2L, 3L));
        MissionDto secondMissionDto = new MissionDto(2, "Second Mission", List.of(2L, 3L, 4L));

        List<MissionDto> missions = List.of(firstMissionDto, secondMissionDto);

        Mockito.when(missionService.getAllMissions()).thenReturn(missions);

        mockMvc.perform(get("/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("First Mission"))
                .andExpect(jsonPath("$[0].hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$[0].hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$[0].hardwareIds.[2]").value(3L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second Mission"))
                .andExpect(jsonPath("$[1].hardwareIds.[0]").value(2L))
                .andExpect(jsonPath("$[1].hardwareIds.[1]").value(3L))
                .andExpect(jsonPath("$[1].hardwareIds.[2]").value(4L));

        Mockito.verify(missionService, Mockito.times(1)).getAllMissions();
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getMissionById_whenAdminAuthority_thenReturnMission() throws Exception {
        MissionDto missionDto = new MissionDto(1, "Mission", List.of(1L, 2L, 3L));
        Mockito.when(missionService.getMissionById(1)).thenReturn(missionDto);

        mockMvc.perform(get("/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mission"))
                .andExpect(jsonPath("$.hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$.hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$.hardwareIds.[2]").value(3L));

        Mockito.verify(missionService, Mockito.times(1)).getMissionById(1);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createMission_whenAdminAuthority_thenCreateAndReturnMission() throws Exception {
        MissionDto requestMission = new MissionDto();
        requestMission.setName("Mission");
        requestMission.setHardwareIds(List.of(1L, 2L, 3L));

        MissionDto responseMissionDto = new MissionDto(1, "Mission", List.of(1L, 2L, 3L));

        Mockito.when(missionService.createMission(requestMission)).thenReturn(responseMissionDto);

        mockMvc.perform(post("/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMission)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/missions/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mission"))
                .andExpect(jsonPath("$.hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$.hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$.hardwareIds.[2]").value(3L));

        Mockito.verify(missionService, Mockito.times(1)).createMission(requestMission);
    }

    @Test
    @WithMockUser(authorities = "user")
    void createMission_whenUserAuthority_thenCreateAndReturnMission() throws Exception {
        MissionDto requestMission = new MissionDto();
        requestMission.setName("Mission");
        requestMission.setHardwareIds(List.of(1L, 2L, 3L));

        MissionDto responseMissionDto = new MissionDto(1, "Mission", List.of(1L, 2L, 3L));

        Mockito.when(missionService.createMission(requestMission)).thenReturn(responseMissionDto);

        mockMvc.perform(post("/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMission)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/missions/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mission"))
                .andExpect(jsonPath("$.hardwareIds.[0]").value(1L))
                .andExpect(jsonPath("$.hardwareIds.[1]").value(2L))
                .andExpect(jsonPath("$.hardwareIds.[2]").value(3L));

        Mockito.verify(missionService, Mockito.times(1)).createMission(requestMission);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void deleteMissionById_whenAdminAuthority_Successful() throws Exception {
        mockMvc.perform(delete("/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully deleted"));

        Mockito.verify(missionService, Mockito.times(1)).deleteMission(1);
    }

    @Test
    @WithMockUser(authorities = "user")
    void deleteMissionById_whenUserAuthority_thenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/missions/1"))
                .andExpect(status().isForbidden());

        Mockito.verify(missionService, Mockito.times(0)).deleteMission(1);
    }

    @Test
    @WithAnonymousUser
    void getAnyMethod_whenAnonymous_thenUnauthorized() throws Exception {

        mockMvc.perform(get("/missions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/missions/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/missions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/missions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "user")
    void getAnyMethod_whenUser_thenIs2xxSuccessful() throws Exception {

        mockMvc.perform(get("/missions"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/missions/1"))
                .andExpect(status().is2xxSuccessful());
    }
}