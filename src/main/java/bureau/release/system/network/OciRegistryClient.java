package bureau.release.system.network;

import bureau.release.system.config.OciRegistryConfig;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.client.TagList;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;

@FeignClient(
        name = "oci-registry-client",
        url = "${oci.registry.url}/v2",
        configuration = OciRegistryConfig.class
)
public interface OciRegistryClient {
    @GetMapping(value = "/{name}/manifests/{reference}")
    Manifest getManifest(@PathVariable("name") String repositoryName,
                         @PathVariable("reference") String manifestReference);

    @GetMapping("/{name}/blobs/{digest}")
    Response getBlob(@PathVariable("name") String repositoryName,
                     @PathVariable("digest") String blobDigest);

    @GetMapping(value = "/{name}/tags/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TagList> getArtifactTagList(@PathVariable("name") String repositoryName);
}
