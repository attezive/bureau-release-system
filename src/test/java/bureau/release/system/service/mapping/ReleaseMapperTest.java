package bureau.release.system.service.mapping;

import bureau.release.system.model.*;
import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseContentDto;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.dto.ReleaseStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReleaseMapperTest {

    @InjectMocks
    private ReleaseMapper releaseMapper = Mappers.getMapper(ReleaseMapper.class);

    @Spy
    private FirmwareVersionMapper firmwareVersionMapper = Mappers.getMapper(FirmwareVersionMapper.class);

    private ReleaseDto releaseDto;
    private Release release;
    private Mission mission;
    private ReleaseStatus releaseStatus;

    @BeforeEach
    void setUp() {
        releaseStatus = new ReleaseStatus(1, "CREATED");
        Firmware firmware = new Firmware(1L, "firmware", "ociName",
                new FirmwareType(1, "FPGA"), new ArrayList<>());
        Hardware hardware = new Hardware(1L, "Hardware", new ArrayList<>(), List.of(firmware));
        firmware.getHardwareList().add(hardware);
        mission = new Mission(1, "mission", List.of(hardware));
        hardware.getMissions().add(mission);
        FirmwareVersion firmwareVersion = new FirmwareVersion(1L, firmware, hardware,
                null, "firmwareVersion");

        release = new Release(1L, "release", LocalDate.now(), "ociName", "reference",
                "digest", releaseStatus, mission, List.of(firmwareVersion));
        firmwareVersion.setRelease(release);

        FirmwareVersionDto firmwareVersionDto = new FirmwareVersionDto(1L, "firmwareVersion", 1L, 1L, 1L);
        ReleaseContentDto releaseContentDto = new ReleaseContentDto(1L, List.of(firmwareVersionDto));
        releaseDto = new ReleaseDto(1L, "release", LocalDate.now(), "ociName", "reference",
                "digest", ReleaseStatusDto.CREATED, 1L, 1, List.of(releaseContentDto));
    }

    @Test
    void toDto() {
        ReleaseDto checkedReleaseDto = releaseMapper.toDto(release);

        assertEquals(releaseDto.getId(), checkedReleaseDto.getId());
        assertEquals(releaseDto.getName(), checkedReleaseDto.getName());
        assertEquals(releaseDto.getOciName(), checkedReleaseDto.getOciName());
        assertEquals(releaseDto.getReference(), checkedReleaseDto.getReference());
        assertEquals(releaseDto.getReleaseDate(), checkedReleaseDto.getReleaseDate());
        assertEquals(releaseDto.getStatus(), checkedReleaseDto.getStatus());
        assertEquals(releaseDto.getOriginId(), checkedReleaseDto.getOriginId());
        assertEquals(releaseDto.getDigest(), checkedReleaseDto.getDigest());
        assertEquals(releaseDto.getReleaseContent(), checkedReleaseDto.getReleaseContent());
    }

    @Test
    void toEntity() {
        Release checkedReleaseEntity = releaseMapper.toEntity(releaseDto, releaseStatus, mission);

        assertEquals(release.getName(), checkedReleaseEntity.getName());
        assertEquals(release.getOciName(), checkedReleaseEntity.getOciName());
        assertEquals(release.getReleaseDate(), checkedReleaseEntity.getReleaseDate());
        assertEquals(release.getStatus(), checkedReleaseEntity.getStatus());
        assertEquals(release.getMission().getId(), checkedReleaseEntity.getMission().getId());
    }
}