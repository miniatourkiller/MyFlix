package net.flix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import net.flix.filter.CustomAuthorizationFilter;
import net.flix.filter.CustomFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{
	
	
	@Autowired
	UserDetailsService userDetailsService;
	
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

public void configurer(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
}

@Bean
public SecurityFilterChain configurer(HttpSecurity http) throws Exception {
	return http.csrf((csrf)->csrf.disable())
			.authorizeHttpRequests((authorizeHttpRequests)->authorizeHttpRequests.requestMatchers("/start/**", "/login","/refreshtoken").permitAll())
			.authorizeHttpRequests((authorizeHttpRequests)->authorizeHttpRequests.requestMatchers("/client/**").hasAnyAuthority("Client"))
			.authorizeHttpRequests((authorizeHttpRequests)->authorizeHttpRequests.requestMatchers("/controller/**").hasAnyAuthority("Controller"))
			.authorizeHttpRequests((authorizeHttpRequests)->authorizeHttpRequests.anyRequest().authenticated())
			.sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(new CustomFilter(authenticationManagerBean(authenticationConfiguration)),BasicAuthenticationFilter.class)
			.build();
}


@Bean
public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration)throws Exception{
	return authenticationConfiguration.getAuthenticationManager();
}
}
