package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }




    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println("=== Trying to authenticate: " + login + " ===");

        // Пытаемся найти пользователя по username
        User user = userService.findByUsername(login);

        // ЕСЛИ НЕ НАЙДЕН ПО USERNAME - ИЩЕМ ПО EMAIL
        if (user == null) {
            System.out.println("=== Not found by username, trying email: " + login + " ===");
            user = userService.findByEmail(login);
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