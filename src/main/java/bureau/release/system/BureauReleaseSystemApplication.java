package bureau.release.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients
@EnableTransactionManagement
public class BureauReleaseSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(BureauReleaseSystemApplication.class, args);
	}

}
