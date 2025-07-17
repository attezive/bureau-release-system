package bureau.release.system.service.impl;

import bureau.release.system.config.HarborRegistryProperties;
import bureau.release.system.service.ArtifactLoader;
import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.Manifest;
import bureau.release.system.service.dto.RepositoryReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HarborArtifactLoader implements ArtifactLoader {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final HarborRegistryProperties properties;

    @Override
    public Manifest loadManifest(RepositoryReference source) {
        String uri = String.format("http://%s/v2/%s/manifests/%s", source.host(), source.name(), source.reference());

        byte[] data = restClient.get()
                .uri(uri)
                .accept(MediaType.valueOf("application/vnd.oci.image.index.v1+json"),
                        MediaType.valueOf("application/vnd.oci.image.manifest.v1+json"))
                .header("Authorization", getBasicAuthHeader())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), (request, response) -> {
                    log.error("Failed to fetch manifest by url: {}", request.getURI());
                    log.error("Headers: {}", request.getHeaders());
                    throw new RuntimeException("Failed to fetch manifest: " + response.getStatusText());
                })
                .body(byte[].class);

        JsonNode manifestContent = null;
        try{
            manifestContent = objectMapper.readTree(data);
        } catch (IOException e) {
            log.error("Failed to parse manifest content: {}", e.getMessage());
        }

        return new Manifest(manifestContent);
    }

    @Override
    public Blob loadBlob(RepositoryReference source, String digest) {
        return null;
    }

    @Override
    public List<Blob> loadBlobs(RepositoryReference source, Manifest manifest) {
        return List.of();
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
