package bureau.release.system.service.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record Manifest(
        JsonNode content
) {
}
