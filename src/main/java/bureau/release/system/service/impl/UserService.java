package bureau.release.system.service.impl;

import bureau.release.system.dal.UserDao;
import bureau.release.system.service.dto.UserDto;
import bureau.release.system.service.dto.UsernameListDto;
import bureau.release.system.service.mapping.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;

    @Transactional
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Transactional
    public UserDto getUserByUsername(@NotBlank String username) {
        return userMapper.toUserDto(userDao.findById(username).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        ));
    }

    @Transactional
    public UsernameListDto getUsernamesByAuthority(@NotBlank String authority) {
        return new UsernameListDto(userDao.findNamesByAuthorities(authority));
    }

    public UserDto saveUser(@Valid UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        UserDetails userDetails = User.builder()
                .username(userDto.getUsername())
                .password(encodedPassword)
                .authorities(userDto.getAuthorities().toArray(new String[]{}))
                .build();

        if (userDetailsManager.userExists(userDto.getUsername())) {
            userDetailsManager.updateUser(userDetails);
        } else {
            userDetailsManager.createUser(userDetails);
        }

        return new UserDto(userDto.getUsername(), encodedPassword,
                true, userDto.getAuthorities());
    }
}
