package net.flix.main;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.flix.entities.MyUser;
import net.flix.repo.MyUserRepo;
import net.flix.repo.RoleRepo;
import net.flix.services.UserService;
import net.flix.utils.TokensCreator;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {MyUserRepo.class, RoleRepo.class})
@ComponentScan("net.flix.*")
@EntityScan("net.flix.*")
public class MyFlixApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyFlixApplication.class, args);
	}

	@Bean
	PasswordEncoder setPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	TokensCreator setTokensCreator() {
		return new TokensCreator();
	}
//	@Bean
//	CommandLineRunner run(UserService userService) {
//		return args->{
//			userService.saveUser(new MyUser("jack@gmail.com", "07214101"));
//			userService.saveUser(new MyUser("kgathiru@gmail.com", "07214101"));
//			userService.saveUser(new MyUser("reninson@gmail.com", "07214101"));
//		};
//	}
}
