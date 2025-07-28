package bureau.release.system.service;

import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.service.dto.client.Artifact;
import bureau.release.system.service.dto.client.Manifest;

import java.io.OutputStream;
import java.util.List;

public interface ArtifactDownloader {
    Manifest getManifest(String repositoryName, String reference);
    void loadReleaseContent(ReleaseDto release, OutputStream outputStream);
    List<Artifact> getArtifacts(String harborProjectName, String harborRepositoryName);
}
