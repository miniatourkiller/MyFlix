package net.flix.utils;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TokensCreator {
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
