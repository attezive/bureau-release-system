package bureau.release.system.config;

import bureau.release.system.exception.ClientException;
import bureau.release.system.exception.ClientNotFoundException;
import feign.Client;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({OciRegistryProperties.class, FeignHttpClientProperties.class})
@Slf4j
public class OciRegistryConfig {
    private final OciRegistryProperties ociRegistryProperties;
    private final FeignHttpClientProperties feignHttpClientProperties;

    @Bean
    public Client feignClient() {
        log.debug("Feign client initializing: {}", feignHttpClientProperties);
        return new ApacheHttpClient(
                HttpClients.custom()
                        .setConnectionTimeToLive(feignHttpClientProperties.connectionTimeToLive(), TimeUnit.SECONDS)
                        .setMaxConnTotal(feignHttpClientProperties.maxConnections())
                        .setMaxConnPerRoute(feignHttpClientProperties.maxConnectionsPerRoute())
                        .build()
        );
    }

    @Bean
    public RequestInterceptor ociAcceptHeaderInterceptor() {
        return template -> {
            if (template.methodMetadata().template().url().contains("/manifests/")) {
                template.header(
                        "Accept",
                        String.join(", ", ociRegistryProperties.acceptManifestTypes())
                );
            }
            template.header(
                    "Authorization",
                    getBasicAuthHeader()
            );
        };
    }

    private String getBasicAuthHeader() {
        String credentials = ociRegistryProperties.ecrUsername() + ":" + ociRegistryProperties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                Request request = response.request();
                return new ClientNotFoundException("Not founded: " + request.httpMethod() + " " + request.url());
            }
            log.error("Feign error occurred: HttpStatus {}, HttpHeaders {}", response.status(), response.headers());
            try {
                log.error("Response body: {}", new String(response.body().asInputStream().readAllBytes()));
            } catch (IOException e) {
                throw new ClientException("OCI registry error");
            }
            return new ClientException("OCI registry error");
        };
    }
}
