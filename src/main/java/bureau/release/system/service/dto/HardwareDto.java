package bureau.release.system.service.dto;

import bureau.release.system.model.Hardware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HardwareDto {
    private long id;
    private String name;
    private List<Integer> missionsIds;
    private List<Long> firmwareIds;
}
