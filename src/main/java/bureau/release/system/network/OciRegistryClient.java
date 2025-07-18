package bureau.release.system.network;

import bureau.release.system.config.OciRegistryConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(
        name = "oci-registry-client",
        url = "${oci.registry.url}",
        configuration = OciRegistryConfig.class
)
public interface OciRegistryClient {
    @GetMapping(
            value = "/v2/{name}/manifests/{reference}",
            produces = {
                    "application/vnd.oci.image.index.v1+json",
                    "application/vnd.oci.image.manifest.v1+json"
            }
    )
    Response getManifest(@PathVariable("name") String repositoryName,
                         @PathVariable("reference") String manifestReference,
                         @RequestHeader("Authorization") String authHeader);

    @GetMapping("/v2/{name}/blobs/{digest}")
    Response getBlob(@PathVariable("name") String repositoryName,
                     @PathVariable("digest") String blobDigest,
                     @RequestHeader("Authorization") String authHeader);
}
