package pl.amerevent.amer.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.amerevent.amer.model.ERole;
import pl.amerevent.amer.repository.UserCredentialRepository;
import pl.amerevent.amer.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSecurity(debug = true)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SecurityConfig {

	private final String secret;
	private final UserDetailsServiceImpl userDetailsService;


	public SecurityConfig(SecretsManagerService secretsManagerService,UserDetailsServiceImpl userDetailsService,@Value("${secrets.location}") String secretLocation) {
		Map<String, String> secretData = secretsManagerService.getSecret(secretLocation);
		this.secret = Objects.nonNull(secretData.get("jwt_secret")) ? secretData.get("jwt_secret") : "a476ae367de672cb84eb8165eae5e7bd05a3c2300feb681c5ac7f4ecafe14fea";
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,UserDetailsServiceImpl userDetailsService) throws Exception {
		http.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(Arrays.asList(
					"http://localhost:4200",
					"http://amer-test-fronted.s3-website-us-east-1.amazonaws.com",
					"http://amer-event.s3-website.eu-central-1.amazonaws.com",
					"https://www.praca-amer-event.pl"));
			configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			configuration.setAllowedHeaders(List.of("*"));
//			configuration.setMaxAge(3600L);
//			configuration.setAllowCredentials(true);
			return configuration;
		}));
		http.authorizeHttpRequests(authorize ->
				authorize
						.requestMatchers("/").permitAll()
						.requestMatchers("/api/login").permitAll()
						.requestMatchers("/api/**").hasAnyAuthority(ERole.ROLE_ADMIN.name(), ERole.ROLE_USER.name())
						.anyRequest().authenticated());
		http.csrf(CsrfConfigurer::disable);
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilter(new JwtAuthorizationFilter(authenticationManager,userDetailsService,secret));
		return http.build();
	}


	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		return authenticationManagerBuilder.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public DaoAuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//		authProvider.setUserDetailsService(userDetailsService);
//		authProvider.setPasswordEncoder(passwordEncoder());
//
//		return authProvider;
//	}

	@Bean
	public UserDetailsService userDetailsService() {
//		UserDetails admin = User.withDefaultPasswordEncoder()
//				.username("admin@gmail.com")
//				.password("test")
//				.roles("admin")
//				.authorities("admin")
//				.build();
////		return new InMemoryUserDetailsManager(admin);
		return new UserDetailsServiceImpl();
	}
}
