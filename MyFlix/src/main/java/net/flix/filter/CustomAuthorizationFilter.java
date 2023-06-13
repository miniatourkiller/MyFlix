package net.flix.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthorizationFilter extends OncePerRequestFilter{

	Logger log = LoggerFactory.getLogger(CustomAuthorizationFilter.class);
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getServletPath().equals("/login") || request.getServletPath().equals("/refreshtoken")) {
			filterChain.doFilter(request, response);
		}else {
			log.info("Hit the main authorization body");
			String authorizationHeader = request.getHeader("Authorization");
			if(authorizationHeader != null&& authorizationHeader.startsWith("Bearer ")) {
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algo = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algo).build();
					DecodedJWT decoded = verifier.verify(token);
					String username = decoded.getSubject();
					String[] roles = decoded.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					for(String role: roles) {
						authorities.add(new SimpleGrantedAuthority(role));
					}
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);	
					filterChain.doFilter(request, response);
				}catch(Exception e) {
					log.error("There was an error: {}", e.getMessage());
					response.setContentType("ApplicationJson");
					Map<String, String> map= new HashMap<>();	
					map.put("error", e.getMessage());
					response.setStatus(403);
					JsonMapper mapper = new JsonMapper();
					mapper.writeValue(response.getOutputStream(), map);
				}
			}else {
				filterChain.doFilter(request, response);
				
				}
		}
		
	}

}
