package pl.amerevent.amer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

	private static final String TOKEN_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";
	private final UserDetailsServiceImpl userDetailsService;
	private final String secret;
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
	                              UserDetailsServiceImpl userDetailsService,
	                              String secret) {
		super(authenticationManager);
		this.userDetailsService = userDetailsService;
		this.secret = secret;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain filterChain) throws IOException, ServletException {
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		if (authentication == null) {
			filterChain.doFilter(request, response);
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		authorities.forEach(authority -> System.out.println("Authority: " + authority.getAuthority()));

		filterChain.doFilter(request, response);
	}
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(TOKEN_HEADER);
		if (token != null && token.startsWith(TOKEN_PREFIX)) {
			String userName = JWT.require(Algorithm.HMAC256(secret))
					.build()
					.verify(token.replace(TOKEN_PREFIX, ""))
					.getSubject();
			if (userName != null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
				return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
			}
		}
		return null;
	}
}
