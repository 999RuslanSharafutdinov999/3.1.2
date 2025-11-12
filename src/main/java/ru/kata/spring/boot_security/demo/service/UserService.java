package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getById(Long id);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    Role findRoleByName(String name);
    List<Role> getAllRoles();
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
