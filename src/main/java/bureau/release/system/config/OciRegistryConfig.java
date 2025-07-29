package bureau.release.system.config;

import bureau.release.system.exception.ClientNotFoundException;
import feign.Client;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OciRegistryProperties.class)
public class OciRegistryConfig {
    private final OciRegistryProperties properties;

    @Bean
    public Client feignClient() {
        return new ApacheHttpClient(
                HttpClients.custom()
                        .setConnectionTimeToLive(10, TimeUnit.SECONDS)
                        .setMaxConnTotal(200)
                        .setMaxConnPerRoute(20)
                        .build()
        );
    }

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
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                Request request = response.request();
                return new ClientNotFoundException("Not founded: " + request.httpMethod() + " " + request.url());
            }
            return new RuntimeException("OCI registry error");
        };
    }

    @Bean
    public Encoder feignEncoder() {
        return new OutputStreamEncoder();
    }
}
