package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String adminUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/users";
    }

    @PostMapping("/users")
    public String addUser(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                          RedirectAttributes redirectAttributes) {

        // ✅ Используем сервис вместо прямого доступа к репозиторию
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
        }

        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email already exists");
        }

        if (roleNames == null || roleNames.isEmpty()) {
            bindingResult.rejectValue("roles", "error.user",
                    "At least one role must be selected");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin/users";
        }

        try {
            Set<Role> roles = roleNames.stream()
                    .map(userService::findRoleByName)
                    .collect(Collectors.toSet());
            user.setRoles(roles);

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit")
    public String editUser(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("allRoles", userService.getAllRoles());
            return "admin/editUser";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found: " + id);
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam("id") Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                             RedirectAttributes redirectAttributes) {

        try {
            User existingUser = userService.getById(id);

            if (!existingUser.getUsername().equals(user.getUsername()) &&
                    userService.existsByUsername(user.getUsername())) {
                bindingResult.rejectValue("username", "error.user", "Username already exists");
            }

            if (!existingUser.getEmail().equals(user.getEmail()) &&
                    userService.existsByEmail(user.getEmail())) {
                bindingResult.rejectValue("email", "error.user", "Email already exists");
            }

            if (roleNames == null || roleNames.isEmpty()) {
                bindingResult.rejectValue("roles", "error.user",
                        "At least one role must be selected");
            }

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute(
                        "org.springframework.validation.BindingResult.user", bindingResult);
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/users/edit?id=" + id;
            }

            Set<Role> roles = roleNames.stream()
                    .map(userService::findRoleByName)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            user.setId(id);

            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}