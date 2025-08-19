package bureau.release.system.service.mapping;

import bureau.release.system.model.Authority;
import bureau.release.system.model.UserEntity;
import bureau.release.system.service.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserDto() {
        Authority userAuthority = new Authority("user", "USER");
        UserEntity userEntity = new UserEntity("user", "user=", true, List.of(userAuthority));

        UserDto expectedUserDto = new UserDto("user", "user=", true, List.of("USER"));

        UserDto actualUserDto = userMapper.toUserDto(userEntity);

        assertEquals(expectedUserDto, actualUserDto, "Incorrect userDto");
    }
}