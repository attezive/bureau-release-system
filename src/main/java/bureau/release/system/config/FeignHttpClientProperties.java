package bureau.release.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feign.httpclient")
public record FeignHttpClientProperties(
        Integer maxConnections,
        Integer maxConnectionsPerRoute,
        Integer connectionTimeToLive
){
}
