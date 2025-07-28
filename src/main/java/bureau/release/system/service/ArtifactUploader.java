package bureau.release.system.service;

import bureau.release.system.service.dto.client.Blob;
import bureau.release.system.service.dto.client.Manifest;

import java.util.List;

public interface ArtifactUploader {
    String uploadManifest(String repositoryName, Manifest manifest, List<Blob> blobsLayer);
    String uploadBlob(String repositoryName, Blob blob);
    List<String> uploadBlobs (String repositoryName, List<Blob> blobs);
}
