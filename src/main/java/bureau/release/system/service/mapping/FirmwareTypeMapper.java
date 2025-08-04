package bureau.release.system.service.mapping;

import bureau.release.system.model.FirmwareType;
import bureau.release.system.service.dto.FirmwareTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FirmwareTypeMapper {
    FirmwareTypeDto toDto(FirmwareType firmware);

    @Mapping(target = "id", ignore = true)
    FirmwareType toEntity(FirmwareTypeDto firmwareTypeDto);
}
