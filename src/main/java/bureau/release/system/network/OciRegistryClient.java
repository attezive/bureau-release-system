package bureau.release.system.network;

import bureau.release.system.config.OciRegistryConfig;
import bureau.release.system.service.dto.Artifact;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "oci-registry-client",
        url = "${oci.registry.url}" + "/api/v2.0",
        configuration = OciRegistryConfig.class
)
public interface OciRegistryClient {
    @GetMapping(value = "/api/v2.0/users/current")
    Response getCsrfToken(@RequestHeader("Authorization") String authHeader);

    @GetMapping(
            value = "/{name}/manifests/{reference}",
            produces = {
                    "application/vnd.oci.image.index.v1+json",
                    "application/vnd.oci.image.manifest.v1+json"
            }
    )
    Response getManifest(@PathVariable("name") String repositoryName,
                         @PathVariable("reference") String manifestReference,
                         @RequestHeader("Authorization") String authHeader);

    @GetMapping("/{name}/blobs/{digest}")
    Response getBlob(@PathVariable("name") String repositoryName,
                     @PathVariable("digest") String blobDigest,
                     @RequestHeader("Authorization") String authHeader);

    @PostMapping(value = "/{name}/blobs/uploads/", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Response uploadBlobInit(@PathVariable("name") String repositoryName,
                            @RequestHeader("Authorization") String authHeader,
                            @RequestHeader("Cookie") String cookie,
                            @RequestHeader("X-Harbor-Csrf-Token") String csrfToken);

    @PatchMapping(value = "/{name}/blobs/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Headers({"Content-Type: application/octet-stream"})
    Response uploadBlobChunk(@PathVariable("name") String repositoryName,
                             @PathVariable("uuid") String uploadUuid,
                             @RequestParam("_state") String state,
                             @RequestBody byte[] blobData,
                             @RequestHeader("Authorization") String authHeader,
                             @RequestHeader("Cookie") String cookie,
                             @RequestHeader("X-Harbor-Csrf-Token") String csrfToken);

    @PutMapping(value = "/{name}/blobs/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Response uploadBlobComplete(@PathVariable("name") String repositoryName,
                                @PathVariable("uuid") String uploadUuid,
                                @RequestParam("_state") String state,
                                @RequestParam("digest") String blobDigest,
                                @RequestHeader("Authorization") String authHeader,
                                @RequestHeader("Cookie") String cookie,
                                @RequestHeader("X-Harbor-Csrf-Token") String csrfToken);

    @PutMapping(value = "/{name}/manifests/{reference}", consumes = "application/vnd.oci.image.manifest.v1+json")
    Response uploadManifest(@PathVariable("name") String repositoryName,
                            @PathVariable("reference") String manifestReference,
                            @RequestHeader("Authorization") String authHeader,
                            @RequestHeader("Cookie") String cookie,
                            @RequestHeader("X-Harbor-Csrf-Token") String csrfToken);

    @GetMapping(
            value = "/projects/{project_name}/repositories/{repository_name}/artifacts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Artifact>> getArtifacts(@PathVariable("project_name") String projectName,
                                                @PathVariable("repository_name") String repositoryName,
                                                @RequestHeader("Authorization") String authHeader);
}
