package bureau.release.system.service.dto;

import java.nio.file.Path;

public record Blob(
        String title,
        String digest,
        Path path
) {
}
