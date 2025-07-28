package bureau.release.system.service.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Manifest {
    String name;
    String reference;
    List<ManifestLayer> layers;
}
