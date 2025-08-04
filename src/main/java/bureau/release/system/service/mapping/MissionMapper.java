package bureau.release.system.service.mapping;

import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.MissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MissionMapper {
    @Mapping(target = "hardwareIds", qualifiedByName = "getHardwareIds", source = "hardwareList")
    MissionDto toDto(Mission mission);

    @Mapping(target = "id", ignore = true)
    Mission toEntity(MissionDto missionDto, List<Hardware> hardwareList);

    @Named("getHardwareIds")
    default List<Long> getHardwareIds(List<Hardware> hardwareList) {
        return hardwareList.stream().map(Hardware::getId).toList();
    }
}
