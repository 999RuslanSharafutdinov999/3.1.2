package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println("=== Trying to authenticate: " + login + " ===");

        // Пытаемся найти пользователя по username
        User user = userRepository.findByUsername(login);

        // Если не нашли по username, ищем по email
        if (user == null) {
            user = userRepository.findByEmail(login);
        }

        if (user == null) {
            System.out.println("=== User NOT FOUND: " + login + " ===");
            throw new UsernameNotFoundException("User not found: " + login);
        }

        System.out.println("=== User FOUND: " + user.getUsername() + " ===");
        System.out.println("=== Email: " + user.getEmail() + " ===");

        // Инициализируем роли
        if (user.getRoles() != null) {
            user.getRoles().size();
            System.out.println("=== Roles: " + user.getRoles() + " ===");
        }

        return user;
    }
}