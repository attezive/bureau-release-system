package bureau.release.system.service.impl;

import bureau.release.system.dal.UserDao;
import bureau.release.system.model.Authority;
import bureau.release.system.model.UserEntity;
import bureau.release.system.service.dto.UserDto;
import bureau.release.system.service.dto.UsernameListDto;
import bureau.release.system.service.mapping.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsManager userDetailsManager;

    @Test
    void getAllUsers_returnUsers() {
        Authority adminAuthority = new Authority("admin", "ADMIN");
        Authority userAuthority = new Authority("user", "USER");

        UserEntity firstUser = new UserEntity("admin", "admin=", true, List.of(adminAuthority));
        UserEntity secondUser = new UserEntity("user", "user=", true, List.of(userAuthority));

        List<UserDto> expectedUserDtoList = List.of(userMapper.toUserDto(firstUser), userMapper.toUserDto(secondUser));

        when(userDao.findAll()).thenReturn(List.of(firstUser, secondUser));

        List<UserDto> actualUserDtoList = userService.getAllUsers();

        assertEquals(expectedUserDtoList, actualUserDtoList, "Incorrect user DTO");
        verify(userDao, times(1)).findAll();
    }

    @Test
    void getUserByUsername_returnUser() {
        Authority userAuthority = new Authority("user", "USER");
        UserEntity user = new UserEntity("user", "user=", true, List.of(userAuthority));

        UserDto expectedUserDto = userMapper.toUserDto(user);

        when(userDao.findById(user.getUsername())).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUserByUsername(user.getUsername());

        assertEquals(expectedUserDto, actualUserDto, "Incorrect user DTO");
        verify(userDao, times(1)).findById(user.getUsername());
    }

    @Test
    void getUserByUsername_failedByUnknownUsername_throwsEntityNotFoundException() {
        when(userDao.findById("user")).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserByUsername("user"));

        assertEquals("User not found", thrown.getMessage(), "Incorrect message");
        verify(userDao, times(1)).findById("user");
    }

    @Test
    void getUsernamesByAuthority_returnUsernameList() {
        Authority userAuthority = new Authority("user", "USER");
        UserEntity user = new UserEntity("user", "user=", true, List.of(userAuthority));

        UsernameListDto expectedUsernames = new UsernameListDto(List.of(user.getUsername()));

        when(userDao.findNamesByAuthorities(user.getAuthorities().getFirst().getAuthority()))
                .thenReturn(List.of(user.getUsername()));

        UsernameListDto actualUsernames = userService.getUsernamesByAuthority(user.getAuthorities().getFirst().getAuthority());

        assertEquals(expectedUsernames, actualUsernames, "Incorrect usernames");
        verify(userDao, times(1))
                .findNamesByAuthorities(user.getAuthorities().getFirst().getAuthority());
    }

    @Test
    void getUsernamesByAuthority_whenUsernamesEmpty_returnEmptyList() {
        when(userDao.findNamesByAuthorities("user")).thenReturn(List.of());

        UsernameListDto actualUsernames = userService.getUsernamesByAuthority("user");

        assertEquals(new UsernameListDto(List.of()), actualUsernames, "Incorrect usernames");
        verify(userDao, times(1)).findNamesByAuthorities("user");
    }

    @Test
    void saveUser_saveAndReturn() {
        UserDto requestUserDto = new UserDto();
        requestUserDto.setUsername("user");
        requestUserDto.setPassword("user");
        requestUserDto.setAuthorities(List.of("user"));

        when(passwordEncoder.encode(requestUserDto.getPassword())).thenReturn("user=");
        when(userDetailsManager.userExists(requestUserDto.getUsername())).thenReturn(false);

        UserDto expectedUserDto = new UserDto("user", "user=", true, List.of("user"));

        UserDto actualUserDto = userService.saveUser(requestUserDto);

        assertEquals(expectedUserDto, actualUserDto, "Incorrect user DTO");
        verify(passwordEncoder, times(1)).encode(requestUserDto.getPassword());
        verify(userDetailsManager, times(1)).userExists(requestUserDto.getUsername());
        verify(userDetailsManager, times(1)).createUser(any(UserDetails.class));
    }

    @Test
    void saveUser_updateAndReturn() {
        UserDto requestUserDto = new UserDto();
        requestUserDto.setUsername("user");
        requestUserDto.setPassword("user");
        requestUserDto.setAuthorities(List.of("user"));

        when(passwordEncoder.encode(requestUserDto.getPassword())).thenReturn("user=");
        when(userDetailsManager.userExists(requestUserDto.getUsername())).thenReturn(true);

        UserDto expectedUserDto = new UserDto("user", "user=", true, List.of("user"));

        UserDto actualUserDto = userService.saveUser(requestUserDto);

        assertEquals(expectedUserDto, actualUserDto, "Incorrect user DTO");
        verify(passwordEncoder, times(1)).encode(requestUserDto.getPassword());
        verify(userDetailsManager, times(1)).userExists(requestUserDto.getUsername());
        verify(userDetailsManager, times(1)).updateUser(any(UserDetails.class));
    }
}