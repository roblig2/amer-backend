package pl.amerevent.amer.config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new java.text.SimpleDateFormat("dd.MM.yyyy"));
		objectMapper.getFactory().setStreamWriteConstraints(
				StreamWriteConstraints.builder().maxNestingDepth(2500).build()
		);
		objectMapper.getFactory().setStreamReadConstraints(
				StreamReadConstraints.builder().maxNestingDepth(2500).build()
		);
		return objectMapper;
	}

}
