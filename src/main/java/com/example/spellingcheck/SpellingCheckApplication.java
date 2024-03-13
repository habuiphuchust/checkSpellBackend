package com.example.spellingcheck;

import com.example.spellingcheck.model.entity.Role;
import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.repository.RoleRepository;
import com.example.spellingcheck.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.modelmapper.ModelMapper;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@AllArgsConstructor
public class SpellingCheckApplication {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpellingCheckApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            List<User> users = (List<User>) userRepository.findAll();
            List<Role> roleList = (List<Role>) roleRepository.findAll();
            if (roleList.isEmpty()) {
                Role roleAdmin = new Role();
                roleAdmin.setId(1L);
                roleAdmin.setName("ROLE_ADMIN");
                roleRepository.save(roleAdmin);
                Role roleUser = new Role();
                roleUser.setId(2L);
                roleUser.setName("ROLE_USER");
                roleRepository.save(roleUser);
            }
            if (users.isEmpty()) {
                User admin = new User();
                Set<Role> roles = new HashSet<>();
                Role roleAdmin = new Role();
                roleAdmin.setId(1L);
                roleAdmin.setName("ROLE_ADMIN");
                roles.add(roleAdmin);
                admin.setUsername("admin");
                admin.setFullName("ha bui phuc");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRoles(roles);

                Set<Role> roleSet = new HashSet<>();
                Role roleUser = new Role();
                roleUser.setName("ROLE_USER");
                roleUser.setId(2L);
                roleSet.add(roleUser);
                User user = User.builder().username("habuiphuc")
                                .fullName("Hà Bùi Phúc")
                                        .password(passwordEncoder.encode("123456"))
                                                .roles(roleSet).build();
                userRepository.save(admin);
                userRepository.save(user);
            }

        };


    }
}
