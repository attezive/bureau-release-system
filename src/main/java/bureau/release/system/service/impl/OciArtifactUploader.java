package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.exception.ClientNotFoundException;
import bureau.release.system.network.OciRegistryClient;
import bureau.release.system.service.ArtifactUploader;
import bureau.release.system.service.dto.client.Blob;
import bureau.release.system.service.dto.client.Location;
import bureau.release.system.service.dto.client.Manifest;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OciArtifactUploader implements ArtifactUploader {
    private final OciRegistryClient ociClient;
    private final OciRegistryProperties properties;
    private String csrfToken;
    private String gorillaCSRF;

    public String uploadManifest(String repositoryName, Manifest manifest, List<Blob> blobsLayer){
        return "";
    }

    public String uploadBlob(String repositoryName, String reference, StreamingResponseBody responseBody) {
        Location location = initBlob(repositoryName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            responseBody.writeTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Response response = ociClient.uploadBlobChunk(
                repositoryName,
                location.uuid(),
                location.state(),
                outputStream,
                getBasicAuthHeader(),
                gorillaCSRF,
                csrfToken
        );

        return String.valueOf(response.status());
    }

    private Location initBlob(String repositoryName){
        Location location;
        try (Response firstResponse =
                     ociClient.uploadBlobInit(repositoryName, getBasicAuthHeader(), gorillaCSRF, csrfToken);
             Response response = isResponseCorrect(firstResponse) ? firstResponse :
                     ociClient.uploadBlobInit(repositoryName, getBasicAuthHeader(), gorillaCSRF, csrfToken)) {
            String locationUrl = response.headers().get(HttpHeaders.LOCATION).toString();
            location = Location.parse(locationUrl);
        }
        return location;
    }

    private boolean isResponseCorrect(Response response) {
        if (response.status() == HttpStatus.FORBIDDEN.value()) {
            csrfToken = response.headers().get("X-Harbor-Csrf-Token").stream().findFirst()
                    .orElseThrow(() -> new ClientNotFoundException("X-Harbor-Csrf-Token not found"));
            gorillaCSRF = response.headers().get("set-cookie").stream().findFirst()
                    .orElseThrow(() -> new ClientNotFoundException("X-Harbor-Csrf-Token not found"));
            gorillaCSRF = "_gorilla_csrf="+gorillaCSRF.split("_gorilla_csrf=")[1].split(";")[0];
            return false;
        }
        return true;
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
