package com.example.reservation_concert;

import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.entity.UserRole;
import com.example.reservation_concert.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ReservationConcertApplication implements CommandLineRunner {

    public ReservationConcertApplication(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(ReservationConcertApplication.class, args);
	}

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {
		if(!userRepository.existsByEmail("admin@gmail.com")) {
			User firstAdmin = new User("admin@gmail.com", "admin", passwordEncoder.encode("1234"), UserRole.ADMIN, 30);
			userRepository.save(firstAdmin);
		}
	}
}
