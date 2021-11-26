package messenger;

import messenger.entities.Role;
import messenger.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner demoData(RoleRepository roleRepository) {
        if (roleRepository.findAll().isEmpty()) {
            return args -> roleRepository.saveAll(List.of(new Role(1L, "ADMIN"),
                    new Role(2L, "USER")));
        }
        return args -> {
        };
    }
}
