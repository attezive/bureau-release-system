package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseContentDto;
import bureau.release.system.service.dto.ReleaseDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {
    @Mapping(target = "originId", source = "id")
    @Mapping(target = "status", qualifiedByName = "getReleaseStatus", source = "status")
    @Mapping(target = "missionId", qualifiedByName = "getMission", source = "mission")
    @Mapping(target = "releaseContent", qualifiedByName = "getReleaseContentList", source = "firmwareVersions")
    ReleaseDto toDto(Release release, @Context FirmwareVersionMapper firmwareVersionMapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firmwareVersions", ignore = true)
    @Mapping(target = "name", source = "releaseDto.name")
    @Mapping(target = "status", source = "status")
    Release toEntity(ReleaseDto releaseDto, ReleaseStatus status, Mission mission);

    @Named("getReleaseStatus")
    default String getReleaseStatus(ReleaseStatus releaseStatus) {
        return releaseStatus.getName();
    }

    @Named("getMission")
    default Integer getMission(Mission mission) {
        return mission.getId();
    }

    @Named("getReleaseContentList")
    default List<ReleaseContentDto> getReleaseContentList(List<FirmwareVersion> firmwareVersions,
                                                          @Context FirmwareVersionMapper firmwareVersionMapper) {
        Map<Long, List<FirmwareVersionDto>> releaseContentMap = new HashMap<>();
        for (FirmwareVersion firmwareVersion : firmwareVersions) {
            if (!releaseContentMap.containsKey(firmwareVersion.getHardware().getId())) {
                releaseContentMap.put(firmwareVersion.getHardware().getId(), new ArrayList<>());
            }
            releaseContentMap.get(firmwareVersion.getHardware().getId())
                    .add(firmwareVersionMapper.toDto(firmwareVersion));
        }
        List<ReleaseContentDto> releaseContentList = new ArrayList<>();
        releaseContentMap.forEach((hardwareId, firmwareVersionDtos) ->
                releaseContentList.add(new ReleaseContentDto(hardwareId, firmwareVersionDtos)));
        return releaseContentList;
    }
}
