package bureau.release.system.controller;

import bureau.release.system.config.SecurityWebConfig;
import bureau.release.system.service.dto.UserDto;
import bureau.release.system.service.dto.UsernameListDto;
import bureau.release.system.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(UsersController.class)
@Import(SecurityWebConfig.class)
class UsersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

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
    void getAllUsers_whenAdminAuthority_thenReturnUsers() throws Exception {
        UserDto firstUserDto = new UserDto("admin", "admin=", true, List.of("ADMIN"));
        UserDto secondUserDto = new UserDto("user", "user=", true, List.of("USER"));

        List<UserDto> userDtoList = List.of(firstUserDto, secondUserDto);

        Mockito.when(userService.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].password").value("admin="))
                .andExpect(jsonPath("$[0].enabled").value(true))
                .andExpect(jsonPath("$[0].authorities.[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1].username").value("user"))
                .andExpect(jsonPath("$[1].password").value("user="))
                .andExpect(jsonPath("$[1].enabled").value(true))
                .andExpect(jsonPath("$[1].authorities.[0]").value("USER"))
                .andExpect(jsonPath("$[2].username").doesNotExist());

        Mockito.verify(userService, Mockito.times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getUserByUsername_whenAdminAuthority_thenReturnUser() throws Exception {
        UserDto userDto = new UserDto("user", "user=", true, List.of("USER"));

        Mockito.when(userService.getUserByUsername("user")).thenReturn(userDto);

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.password").value("user="))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.authorities.[0]").value("USER"));

        Mockito.verify(userService, Mockito.times(1)).getUserByUsername("user");
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getUsernamesByAuthority_whenAdminAuthority_thenReturnUsernameList() throws Exception {
        UsernameListDto usernameListDto = new UsernameListDto(List.of("USER"));

        Mockito.when(userService.getUsernamesByAuthority("USER")).thenReturn(usernameListDto);

        mockMvc.perform(get("/users/authorities/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernames.[0]").value("USER"))
                .andExpect(jsonPath("$.usernames.[1]").doesNotExist());

        Mockito.verify(userService, Mockito.times(1)).getUsernamesByAuthority("USER");
    }

    @Test
    @WithMockUser(authorities = "admin")
    void saveUser_whenAdminAuthority_thenSaveAndReturn() throws Exception {
        UserDto requestUserDto = new UserDto();
        requestUserDto.setUsername("user");
        requestUserDto.setPassword("user=");
        requestUserDto.setAuthorities(List.of("USER"));

        UserDto respondUserDto = new UserDto("user", "user=", true, List.of("USER"));

        Mockito.when(userService.saveUser(requestUserDto)).thenReturn(respondUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.password").value("user="))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.authorities.[0]").value("USER"));

        Mockito.verify(userService, Mockito.times(1)).saveUser(requestUserDto);
    }

    @Test
    @WithAnonymousUser
    void getAnyMethod_whenAnonymous_thenUnauthorized() throws Exception {

        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/users/authorities/USER"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "user")
    void getAnyMethod_whenUser_thenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/users/authorities/USER"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/users"))
                .andExpect(status().isForbidden());
    }
}