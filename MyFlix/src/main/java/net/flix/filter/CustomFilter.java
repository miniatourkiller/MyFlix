package net.flix.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.flix.utils.TokensCreator;
@RequiredArgsConstructor
public class CustomFilter extends UsernamePasswordAuthenticationFilter{

	//this class is for authentication
	
	private final AuthenticationManager authManager;
	Logger log = LoggerFactory.getLogger(CustomFilter.class);
	
	
	
	public CustomFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String name =  request.getParameter("userName");
		String password = request.getParameter("password");
		log.info("name is: {}", name);
		UsernamePasswordAuthenticationToken authenticateToken = new UsernamePasswordAuthenticationToken(name, password);
		return authManager.authenticate(authenticateToken);
	}
	

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		
//				response.setHeader("access_token", access_token);
//				response.setHeader("refresh_token", refresh_token);
		response.setContentType("application/json");
		
		Map<String, String> tokens = new HashMap<>();
		
		tokens.put("refresh_token", this.createRefreshToken(user, request));
		tokens.put("access_token",this.createAccessToken(user, request));
				JsonMapper mapper = new JsonMapper();
				
				mapper.writeValue(response.getOutputStream(), tokens);
	}
	
	Algorithm algo = Algorithm.HMAC256("secret".getBytes());
	public String createAccessToken(User user, HttpServletRequest request) {
		
		return JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algo);
	
	}
	public String createRefreshToken(User user, HttpServletRequest request) {
		return  JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+30*60*1000))
				.withIssuer(request.getRequestURL().toString())
				.sign(algo);
	}
}
