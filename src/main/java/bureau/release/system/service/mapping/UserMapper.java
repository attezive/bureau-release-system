package bureau.release.system.service.mapping;

import bureau.release.system.model.Authority;
import bureau.release.system.model.UserEntity;
import bureau.release.system.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "authorities", qualifiedByName = "getAuthoritiesNames", source = "authorities")
    UserDto toUserDto(UserEntity userEntity);

    @Named("getAuthoritiesNames")
    default List<String> getAuthoritiesNames(List<Authority> authorities) {
        return authorities.stream().map(Authority::getAuthority).toList();
    }
}
