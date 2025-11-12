package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserService userService,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role roleUser = createRoleIfNotFound("ROLE_USER");
        Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN");

        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setLastname("Adminov");
            admin.setAge(35);
            admin.setUsername("admin");
            admin.setEmail("admin@mail.ru");
            admin.setPassword("admin");
            admin.setRoles(Set.of(roleAdmin, roleUser));
            userService.saveUser(admin);
            System.out.println("=== Created admin user: admin/admin ===");
        }

        if (!userService.existsByUsername("user")) {
            User user = new User();
            user.setName("User");
            user.setLastname("Userov");
            user.setAge(30);
            user.setUsername("user");
            user.setEmail("user@mail.ru");
            user.setPassword("user");
            user.setRoles(Set.of(roleUser));
            userService.saveUser(user);
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