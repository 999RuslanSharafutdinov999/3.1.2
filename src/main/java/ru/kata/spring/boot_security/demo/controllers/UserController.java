package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/user";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/users";
    }

    @PostMapping("/admin/users")
    public String addUser(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          @RequestParam("roleNames") List<String> roleNames,
                          RedirectAttributes redirectAttributes) {

        if (roleNames == null || roleNames.isEmpty()) {
            bindingResult.rejectValue("roles", "error.user", "At least one role must be selected");
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

    @GetMapping("/admin/users/edit")
    public String editUser(@RequestParam("id") Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/editUser";
    }

    @PostMapping("/admin/users/update")
    public String updateUser(@RequestParam("id") Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam("roleNames") List<String> roleNames,
                             RedirectAttributes redirectAttributes) {

        if (roleNames == null || roleNames.isEmpty()) {
            bindingResult.rejectValue("roles", "error.user", "At least one role must be selected");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin/users/edit?id=" + id;
        }

        try {
            Set<Role> roles = roleNames.stream()
                    .map(userService::findRoleByName)
                    .collect(Collectors.toSet());
            user.setRoles(roles);

            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/user")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }
}