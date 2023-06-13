package net.flix.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.flix.entities.MyUser;
import net.flix.entities.Role;
import net.flix.services.UserServiceImp;
import net.flix.utils.TokensCreator;



@RestController
public class MyController {
	org.slf4j.Logger log = LoggerFactory.getLogger(MyController.class);
@RequestMapping("/start")
public String hello() {
	log.info("Accessed controller");
	return "Hello james";
}
@Autowired
UserServiceImp userService;
@RequestMapping(value="client/saveuser", method = RequestMethod.POST)
public ResponseEntity<MyUser> save(@RequestBody MyUser user) {
	
	log.info("Saved A User") ;
	return ResponseEntity.created(null).body(userService.saveUser(user));
}
@RequestMapping(value = "controller/saverole", method = RequestMethod.POST)
public  ResponseEntity<Role>  save2(@RequestBody Role role ) {
	log.info("Saved A role") ;
	return ResponseEntity.created(null).body(userService.saveRole(role));
}
@RequestMapping(value = "start/addrole/{username}/{rolename}" , method = RequestMethod.POST)
public void addrole(@PathVariable("username" ) String userName, @PathVariable("rolename") String roleName) {
	log.info("Adding role to user");
	userService.AddRoleToUser(userName, roleName);
}
@RequestMapping(value = "controller/getusers")
public ResponseEntity<List<MyUser>> getUsers(){
	return ResponseEntity.ok().body(userService.getUsers());
}
@RequestMapping(value="getrole/{rolename}")
public ResponseEntity<Role> getrole(@PathVariable("rolename") String roleName){
	return ResponseEntity.ok().body(userService.getRole(roleName));
}
@RequestMapping(value="encode/{username}")
public void encodePass(@PathVariable("username") String userName) {
	userService.updateOldPasswords(userName);
}

@RequestMapping(value = "/tryme/{name}")
	public String tryme(@PathVariable("name") String name){
	return "hello "+name;
}
@RequestMapping(value = "/client/{name}")
	public String clientTry(@PathVariable("name") String name){
	return "Hello client "+name;
}
@RequestMapping(value = "/controller/{name}")
	public String controllerTry(@PathVariable("name") String name){
	return "Hello controller: "+ name;
}
@Autowired
UserServiceImp imp;
@Autowired
TokensCreator tokensCreator;
JsonMapper mapper = new	JsonMapper();
Map<String, String> map  = new HashMap<>();
@RequestMapping(value = "/refreshtoken")
public void refreshToken(HttpServletRequest request, HttpServletResponse response)throws Exception {
	String authorization = request.getHeader("Authorization");
	if(request.getServletPath().equals("/refreshtoken")) {
		if(authorization != null&&authorization.startsWith("Bearer ")) {
			try {
				Algorithm algo = Algorithm.HMAC256("secret".getBytes());
				String refresh_token = authorization.substring("Bearer ".length());
				JWTVerifier verifier = JWT.require(algo).build();
				DecodedJWT decoded = verifier.verify(refresh_token);
				String username = decoded.getSubject();
				MyUser user = imp.getUser(username);
				Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				for(Role role: user.getRoles()) {
					authorities.add(new SimpleGrantedAuthority(role.getName()));
				}
				User user2 = new User(user.getUserName(), user.getPassword(), authorities);
				Map<String, String> tokens = new HashMap<>();
				response.setContentType("application/json");
				tokens.put("refresh_token",refresh_token);
				tokens.put("access_token",tokensCreator.createAccessToken(user2, request));
				mapper.writeValue(response.getOutputStream(), tokens);
			}catch(Exception e) {
				map.put("error", e.getMessage());
				response.setStatus(211);
				mapper.writeValue(response.getOutputStream(), map);
			}
		}else {
			map.put("error", "No authorization set");
			response.setStatus(403);
			mapper.writeValue(response.getOutputStream(), map);
		}
	}
	
}
}
