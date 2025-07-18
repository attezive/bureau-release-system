package bureau.release.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "oci.registry")
public record OciRegistryProperties(
        String url,
        String name,
        String ecrUsername,
        String ecrPassword,

        @DefaultValue({
                "application/vnd.oci.image.index.v1+json",
                "application/vnd.oci.image.manifest.v1+json"
        })
        List<String> acceptManifestTypes
) {
}


