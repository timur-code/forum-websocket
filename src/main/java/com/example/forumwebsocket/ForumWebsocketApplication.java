package com.example.forumwebsocket;

import com.example.forumwebsocket.Services.UserService;
import com.example.forumwebsocket.models.UserRequest;
import com.example.forumwebsocket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.forumwebsocket.helpers.ConstantRoles.ROLE_ADMIN;
import static com.example.forumwebsocket.helpers.ConstantRoles.ROLE_USER;

@SpringBootApplication
public class ForumWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForumWebsocketApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService, UserRepository userRepository) {
		return args -> {
			userService.saveRole(ROLE_ADMIN);
			userService.saveRole(ROLE_USER);

			userService.addUser(new UserRequest("timur@gmail.com", "timur", "", "123456"));

			userService.addRoleToUser("timur", "ROLE_ADMIN");

			System.out.println(userRepository.findByUserName("timur"));
			/*System.out.println(userRepository.findById(3L).get());
			System.out.println(userRepository.findById(3L).isPresent());*/

			System.out.println(userService.getUser("timur").getRoles().contains(ROLE_ADMIN));
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
