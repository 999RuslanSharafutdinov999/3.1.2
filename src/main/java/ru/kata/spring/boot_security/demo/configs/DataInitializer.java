package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role roleUser = createRoleIfNotFound("ROLE_USER");
        Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN");

        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setName("Admin");
            admin.setLastname("Adminov");
            admin.setYearOfRegistration(2024);
            admin.setAge(35);
            admin.setUsername("admin");
            admin.setEmail("admin@mail.ru");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(roleAdmin, roleUser));
            userRepository.save(admin);
            System.out.println("=== Created admin user: admin/admin ===");
        }

        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setName("User");
            user.setLastname("Userov");
            user.setYearOfRegistration(2024);
            user.setAge(30);
            user.setUsername("user");
            user.setEmail("user@mail.ru");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(Set.of(roleUser));
            userRepository.save(user);
            System.out.println("=== Created user: user/user ===");
        }

        System.out.println("=== Data initialization completed ===");
    }

    private Role createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
            System.out.println("Created role: " + name);
        }
        return role;
    }
}