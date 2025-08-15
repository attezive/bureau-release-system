package bureau.release.system.service;


import java.io.ByteArrayOutputStream;

public interface ArtifactUploader {
    String uploadArtifact(ByteArrayOutputStream artifactBody, String artifactName, String ociName, String reference);
}
