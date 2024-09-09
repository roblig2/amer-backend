package pl.amerevent.amer.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class SecretsManagerService {

	private final SecretsManagerClient secretsManagerClient;


	public SecretsManagerService(@Value("${aws.region}") String region) {
		this.secretsManagerClient = SecretsManagerClient.builder()
//				.credentialsProvider(ProfileCredentialsProvider.create())
				.region(Region.of(region))
				.build();
	}

	public Map<String, String> getSecret(String secretName) {
		try {
			GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
					.secretId(secretName)
					.build();

			GetSecretValueResponse getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

			String secretString = getSecretValueResponse.secretString();

			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(secretString, Map.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get secret from AWS Secrets Manager", e);
		}
	}
}
