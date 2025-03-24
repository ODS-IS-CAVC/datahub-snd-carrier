package datahub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import datahub.config.ApiConfig;

/**
 * メイン処理
 */
@SpringBootApplication
@EnableConfigurationProperties(ApiConfig.class)
public class DatahubJsontocsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatahubJsontocsvApplication.class, args);
	}
}
