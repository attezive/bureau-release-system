package bureau.release.system.service;

import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.Manifest;
import bureau.release.system.service.dto.RepositoryReference;

import java.util.List;

public interface ArtifactLoader {
    Manifest loadManifest(RepositoryReference source);
    Blob loadBlob(RepositoryReference source, String digest);
    List<Blob> loadBlobs(RepositoryReference source, Manifest manifest);
}
