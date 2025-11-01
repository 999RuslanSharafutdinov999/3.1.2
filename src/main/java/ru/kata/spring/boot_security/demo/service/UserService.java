package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.getRoles().size());
        return users;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.getRoles().size();
        return user;
    }

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User updatedUser) {
        User existingUser = getById(updatedUser.getId());

        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("Username already exists: " + updatedUser.getUsername());
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setYearOfRegistration(updatedUser.getYearOfRegistration());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setRoles(updatedUser.getRoles());

        String newPassword = updatedUser.getPassword();
        if (newPassword != null && !newPassword.trim().isEmpty() && !isPasswordEncoded(newPassword)) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        } else if (newPassword == null || newPassword.trim().isEmpty()) {
            // Сохраняем старый пароль
            existingUser.setPassword(existingUser.getPassword());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.findByName("ROLE_" + name);
        }
        if (role == null) {
            throw new RuntimeException("Role not found: " + name);
        }
        return role;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.getRoles().size();
        }
        return user;
    }

    @Transactional
    public User createUser(String name, String lastname, Integer year, String username,
                           String rawPassword, Set<Role> roles) {
        User user = new User(name, lastname, year, username, rawPassword);
        user.setRoles(roles);
        return saveUser(user);
    }

    private boolean isPasswordEncoded(String password) {
        if (password == null || password.length() < 60) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    @Transactional(readOnly = true)
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}