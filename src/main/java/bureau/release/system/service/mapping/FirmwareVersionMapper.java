package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseContentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface FirmwareVersionMapper {
    @Mapping(target = "firmwareId", qualifiedByName = "getFirmwareId", source = "firmware")
    @Mapping(target = "hardwareId", qualifiedByName = "getHardwareId", source = "hardware")
    @Mapping(target = "releaseId", qualifiedByName = "getReleaseId", source = "release")
    FirmwareVersionDto toDto(FirmwareVersion firmwareVersion);

    @Mapping(target = "id", ignore = true)
    FirmwareVersion toEntity(FirmwareVersionDto firmwareVersionDto, Firmware firmware,
                             Hardware hardware, Release release);

    @Named("getFirmwareId")
    default Long getFirmwareId(Firmware firmware) {
        return firmware.getId();
    }

    @Named("getHardwareId")
    default Long getHardwareId(Hardware hardware) {
        return hardware.getId();
    }

    @Named("getReleaseId")
    default Long getReleaseId(Release release) {
        return release.getId();
    }

    @Named("getReleaseContentList")
    default List<ReleaseContentDto> getReleaseContentList(List<FirmwareVersion> firmwareVersions) {
        Map<Long, List<FirmwareVersionDto>> releaseContentMap = new HashMap<>();
        for (FirmwareVersion firmwareVersion : firmwareVersions) {
            if (!releaseContentMap.containsKey(firmwareVersion.getHardware().getId())) {
                releaseContentMap.put(firmwareVersion.getHardware().getId(), new ArrayList<>());
            }
            releaseContentMap.get(firmwareVersion.getHardware().getId())
                    .add(toDto(firmwareVersion));
        }
        List<ReleaseContentDto> releaseContentList = new ArrayList<>();
        releaseContentMap.forEach((hardwareId, firmwareVersionDtos) ->
                releaseContentList.add(new ReleaseContentDto(hardwareId, firmwareVersionDtos)));
        return releaseContentList;
    }
}
