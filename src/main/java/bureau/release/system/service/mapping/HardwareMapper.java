package bureau.release.system.service.mapping;

import bureau.release.system.model.Firmware;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Mission;
import bureau.release.system.service.dto.HardwareDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HardwareMapper {
    @Mapping(target = "missionsIds", qualifiedByName = "getMissionsIds", source = "missions")
    @Mapping(target = "firmwareIds", qualifiedByName = "getFirmwareIds", source = "firmwareList")
    HardwareDto toDto(Hardware hardware);

    @Mapping(target = "id", ignore = true)
    Hardware toEntity(HardwareDto hardwareDto, List<Firmware> firmwareList, List<Mission> missionList);

    @Named("getMissionsIds")
    default List<Integer> getMissionsIds(List<Mission> missionList) {
        if (missionList == null)
            return new ArrayList<>();
        return missionList.stream().map(Mission::getId).toList();
    }

    @Named("getFirmwareIds")
    default List<Long> getFirmwareIds(List<Firmware> firmwareList) {
        return firmwareList.stream().map(Firmware::getId).toList();
    }
}
