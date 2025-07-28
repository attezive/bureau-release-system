package bureau.release.system.service.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ManifestLayer {
    String mediaType;
    String digest;
    Integer size;
    LayerAnnotation annotations;
}
