package bureau.release.system.service.mapping;

import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareType;
import bureau.release.system.model.Hardware;
import bureau.release.system.service.dto.FirmwareDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FirmwareMapper {
    @Mapping(target = "type", qualifiedByName = "getFirmwareTypeName", source = "firmwareType")
    @Mapping(target = "hardwareIds", qualifiedByName = "getHardwareIds", source = "hardwareList")
    FirmwareDto toDto(Firmware firmware);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "firmwareDto.name")
    Firmware toEntity(FirmwareDto firmwareDto, FirmwareType firmwareType);

    @Named("getHardwareIds")
    default List<Long> getHardwareIds(List<Hardware> hardwareList) {
        if (hardwareList == null)
            return new ArrayList<>();
        return hardwareList.stream().map(Hardware::getId).toList();
    }

    @Named("getFirmwareTypeName")
    default String getFirmwareTypeName(FirmwareType firmwareType) {
        return firmwareType.getName();
    }
}
