package bureau.release.system.service;

import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.Manifest;

import java.nio.file.Path;
import java.util.List;

public interface ArtifactDownloader {
    Manifest loadManifest(String repositoryName, String reference);
    Blob loadBlob(String repositoryName, String digest, Path path);
    List<Blob> loadBlobs(String repositoryName, Manifest manifest, String pathPrefix);
}
