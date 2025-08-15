package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.exception.ReleaseStreamException;
import bureau.release.system.service.ArtifactUploader;
import land.oras.ContainerRef;
import land.oras.LocalPath;
import land.oras.Manifest;
import land.oras.Registry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class OrasArtifactUploader implements ArtifactUploader {
    private final OciRegistryProperties properties;
    private final Registry registry;

    public OrasArtifactUploader(OciRegistryProperties properties) {
        this.properties = properties;
        registry = Registry.builder()
                .withInsecure(!properties.url().contains("https"))
                .defaults(properties.ecrUsername(), properties.ecrPassword())
                .build();
    }

    @Override
    public String uploadArtifact(ByteArrayOutputStream artifactBody, String artifactName, String ociName, String reference) {
        String digest;
        try {
            createFile(artifactBody, artifactName);
            log.debug("File created: {}", artifactName);

            LocalPath artifact = LocalPath.of(artifactName);

            log.debug("Artifact push");
            Manifest manifest = registry.pushArtifact(
                    ContainerRef.parse(
                            String.format("%s/%s:%s",
                                    properties.url().replace("http://", "").replace("https://", ""),
                                    ociName,
                                    reference)),
                    artifact);

            digest = manifest.getDescriptor().getDigest();
        } finally {
            if (deleteFile(artifactName)) {
                log.debug("File deleted: {}", artifactName);
            } else {
                log.error("Delete file failed: {}", artifactName);
            }
        }
        return digest;
    }

    private void createFile(ByteArrayOutputStream artifactBody, String artifactName) {
        log.debug("Creating file: {}", artifactName);
        File file = new File(artifactName);
        try (OutputStream out = new FileOutputStream(file)) {
            artifactBody.writeTo(out);
        } catch (IOException e) {
            throw new ReleaseStreamException("IO Exception: " + e.getMessage());
        }
    }

    private boolean deleteFile(String artifactName) {
        File file = new File(artifactName);
        return file.delete();
    }
}
