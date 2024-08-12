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
import pl.amerevent.amer.model.ERole;

@Configuration
@EnableWebSecurity(debug = true)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SecurityConfig {

	private final String secret;
	private final UserDetailsServiceImpl userDetailsService;

	public SecurityConfig(@Value("${jwt.secret}") String secret,UserDetailsServiceImpl userDetailsService) {
		this.secret = secret;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,UserDetailsServiceImpl userDetailsService) throws Exception {
		http.authorizeHttpRequests(authorize ->
				authorize
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

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}
//	@Bean
//	public UserDetailsService userDetailsService(UserRepository userRepository) {
////		UserDetails admin = User.withDefaultPasswordEncoder()
////				.username("admin@gmail.com")
////				.password("test")
////				.roles("admin")
////				.authorities("admin")
////				.build();
//////		return new InMemoryUserDetailsManager(admin);
//		return new UserDetailsServiceImpl(userRepository);
//	}
}
