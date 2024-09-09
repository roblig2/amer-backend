package pl.amerevent.amer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration

public class PostgresqlConfig {
	private final SecretsManagerService secretsManagerService;
	private final String secretLocation;


	public PostgresqlConfig(SecretsManagerService secretsManagerService,@Value("${secrets.location}") String secretLocation) {
		this.secretsManagerService = secretsManagerService;
		this.secretLocation = secretLocation;
	}


	private Map<String, String> getSerets() {
		return secretsManagerService.getSecret(secretLocation);
	}


	@Bean
	public DataSource dataSource() throws Exception {
		// Pobierz dane uwierzytelniające z AWS Secrets Manager
		Map<String, String> dbCredentials = getSerets();

		// Utwórz źródło danych (DataSource) z użyciem pobranych danych
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver"); // lub inny sterownik JDBC
		dataSource.setUrl(dbCredentials.get("postgresql_url"));

		return dataSource;
	}
}
