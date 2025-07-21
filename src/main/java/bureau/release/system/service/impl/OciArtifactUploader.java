package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.network.OciRegistryClient;
import bureau.release.system.service.ArtifactUploader;
import bureau.release.system.service.dto.Blob;
import bureau.release.system.service.dto.Location;
import bureau.release.system.service.dto.Manifest;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OciArtifactUploader implements ArtifactUploader {
    private final OciRegistryClient ociClient;
    private final OciRegistryProperties properties;

    @Override
    public String uploadManifest(String repositoryName, Manifest manifest, List<Blob> blobsLayer){
        return "";
    }

    @Override
    public String uploadBlob(String repositoryName, Blob blob) {
        Response response = ociClient.getCsrfToken(getBasicAuthHeader());
        String sid = response.headers().get("set-cookie").stream().findFirst().get();
        sid = "sid="+sid.split("sid=")[1].split(";")[0];

        response = ociClient.getCsrfToken(getBasicAuthHeader());
        String csrfToken = response.headers().get("X-Harbor-Csrf-Token").stream().findFirst().get();
        String gorillaCsrf = response.headers().get("set-cookie").stream().findFirst().get();
        gorillaCsrf = "_gorilla_csrf="+gorillaCsrf.split("_gorilla_csrf=")[1].split(";")[0];

        response = ociClient.uploadBlobInit(
                repositoryName,
                getBasicAuthHeader(),
                gorillaCsrf,
                csrfToken);

        csrfToken = response.headers().get("X-Harbor-Csrf-Token").stream().findFirst().get();
        String locationUrl = response.headers().get(HttpHeaders.LOCATION).toString();
        Location location = Location.parse(locationUrl);

        try {
            byte[] blobData = Files.readAllBytes(blob.path());
            response = ociClient.uploadBlobChunk(
                    repositoryName,
                    location.uuid(),
                    location.state(),
                    blobData,
                    getBasicAuthHeader(),
                    gorillaCsrf,
                    csrfToken
            );
            log.info("uploadBlobChunk response body={}", new String(response.body().asInputStream().readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(response.status());
    }

    @Override
    public List<String> uploadBlobs(String repositoryName, List<Blob> blobs) {
        return List.of();
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
