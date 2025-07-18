package bureau.release.system.service.dto;

import java.nio.file.Path;

public record Blob(
        String digest,
        Path path
) {
}
