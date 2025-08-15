package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.ReleaseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = FirmwareVersionMapper.class)
public interface ReleaseMapper {
    @Mapping(target = "originId", source = "id")
    @Mapping(target = "status", qualifiedByName = "getReleaseStatus", source = "status")
    @Mapping(target = "missionId", qualifiedByName = "getMission", source = "mission")
    @Mapping(target = "releaseContent", qualifiedByName = "getReleaseContentList", source = "firmwareVersions")
    ReleaseDto toDto(Release release);

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
}
