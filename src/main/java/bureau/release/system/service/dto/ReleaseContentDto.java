package bureau.release.system.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReleaseContentDto {
    private Long hardwareId;
    private List<FirmwareVersionDto> firmwareVersions;
}
