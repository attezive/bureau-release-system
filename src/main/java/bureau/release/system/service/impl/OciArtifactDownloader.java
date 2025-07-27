package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.network.OciRegistryClient;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.Artifact;
import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.ClientNotFoundException;
import bureau.release.system.service.dto.Manifest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OciArtifactDownloader implements ArtifactDownloader {
    private final OciRegistryClient ociClient;
    private final OciRegistryProperties properties;

    @Override
    public Manifest loadManifest(String repositoryName, String reference) {
        try (
                Response response = ociClient.getManifest(
                        repositoryName,
                        reference,
                        getBasicAuthHeader()
                )
        ) {
            return new Manifest(
                    parseManifest(response.body().asInputStream().readAllBytes()), reference
            );
        } catch (IOException e) {
            log.error("Failed to load manifest content: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Blob loadBlob(String repositoryName, String digest, Path path) {
        Blob blob = null;
        try (Response response = ociClient.getBlob(repositoryName, digest, getBasicAuthHeader());
             InputStream is = response.body().asInputStream()) {
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            blob = new Blob(path.getFileName().toString(), digest, path);
        } catch (IOException e) {
            log.error("Failed to load blob content: {}", e.getMessage());
        }
        return blob;
    }

    @Override
    public List<Blob> loadBlobs(String repositoryName, Manifest manifest, String pathPrefix) {
        List<JsonNode> layers = new ArrayList<>();
        List<Blob> blobs = new ArrayList<>();
        manifest.content().get("layers").forEach(layers::add);
        for (JsonNode layer : layers) {
            String digest = layer.get("digest").asText();
            String path = pathPrefix + layer.get("annotations").get("org.opencontainers.image.title").asText();
            blobs.add(loadBlob(repositoryName, digest, Paths.get(path)));
        }
        return blobs;
    }

    @Override
    public List<Artifact> getArtifacts(String harborProjectName, String harborRepositoryName) {
        ResponseEntity<List<Artifact>> response = ociClient
                .getArtifacts(
                        harborProjectName,
                        harborRepositoryName,
                        getBasicAuthHeader()
                );
        List<Artifact> artifacts = response.getBody();
        if (artifacts == null || artifacts.isEmpty()) {
            throw new ClientNotFoundException("No repository artifacts found: " + harborProjectName + "/" + harborRepositoryName);
        }
        return artifacts;
    }

    private JsonNode parseManifest(byte[] data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(data);
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
