package bureau.release.system.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OciRegistryProperties.class)
public class OciRegistryConfig {
    private final OciRegistryProperties properties;

    @Bean
    public RequestInterceptor ociAcceptHeaderInterceptor() {
        return template -> {
            if (template.methodMetadata().template().url().contains("/manifests/")) {
                template.header(
                        "Accept",
                        String.join(", ", properties.acceptManifestTypes())
                );
            }
        };
    }


    @Bean
    public Decoder feignDecoder() {
        return (response, type) -> response;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new RuntimeException("Manifest not found");
            }
            return new RuntimeException("OCI registry error");
        };
    }
}
