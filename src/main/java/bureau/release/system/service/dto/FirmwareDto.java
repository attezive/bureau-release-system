package bureau.release.system.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FirmwareDto {
    private long id;
    private String name;
    private String type;
    private String ociName;
    private List<Long> hardwareIds;
}
