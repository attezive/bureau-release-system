package bureau.release.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "release.system.admin")
public record ReleaseSystemProperties(
        String username,
        String password,
        String authority
) {
}
