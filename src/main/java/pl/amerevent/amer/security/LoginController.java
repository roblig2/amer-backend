package pl.amerevent.amer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.amerevent.amer.security.login.LoginAttemptService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final long expirationTime;
	private final String secret;
	private final LoginAttemptService loginAttemptService;


	public LoginController(AuthenticationManager authenticationManager,SecretsManagerService secretsManagerService,@Value("${jwt.expirationTime}") long expirationTime,@Value("${secrets.location}") String secretLocation, LoginAttemptService loginAttemptService) {
		this.authenticationManager = authenticationManager;
		this.expirationTime = expirationTime;
		Map<String, String> secretData = secretsManagerService.getSecret(secretLocation);
		this.secret = Objects.nonNull(secretData.get("jwt_secret")) ? secretData.get("jwt_secret") : "a476ae367de672cb84eb8165eae5e7bd05a3c2300feb681c5ac7f4ecafe14fea";
		this.loginAttemptService = loginAttemptService;

	}

	@PostMapping("/login")
	public ResponseEntity<Token> login(@RequestBody LoginCredentials loginCredentials) {
		try {
			if (loginAttemptService.isBlocked(loginCredentials.getUsername())) {
				return new ResponseEntity<>(null, HttpStatus.LOCKED);
			}
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getUsername(), loginCredentials.getPassword()));
			// Sprawdzenie, czy użytkownik nie jest zablokowany
			loginAttemptService.loginSucceeded(loginCredentials.getUsername());
			UserDetails principal = (UserDetails) authenticate.getPrincipal();
			List<String> authorities = principal.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.toList();
			String token = JWT.create()
					.withSubject(principal.getUsername())
					.withClaim("autorities",authorities)
					.withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
					.sign(Algorithm.HMAC256(secret));

			return new ResponseEntity<>(new Token(token), HttpStatus.OK);
		} catch (AuthenticationException e) {
			loginAttemptService.loginFailed(loginCredentials.getUsername());
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}

	@Getter
	private static class LoginCredentials {
		private String username;
		private String password;
	}
	@Getter
	@AllArgsConstructor
	private static class Token {
		private String token;
	}
}
