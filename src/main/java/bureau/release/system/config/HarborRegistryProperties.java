package bureau.release.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "harbor.registry")
public record HarborRegistryProperties(
        String source,
        String ecrUsername,
        String ecrPassword
) {
}
