package ru.kata.spring.boot_security.demo.controller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;

    public AuthController(PasswordEncoder passwordEncoder, UserService userService, RoleService roleService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(User formUser, Model model) {
        String err = "Пароли не совпадают";
        User user = new User();
        user.setUsername(formUser.getUsername());
        user.setNickname(formUser.getUsername().toUpperCase());
        user.setPassword(passwordEncoder.encode(formUser.getPassword()));
        user.setRoles(Collections.singleton(roleService.createRole("ROLE_USER")));

        if (formUser.getPassword().equals(formUser.getPasswordConfirm())) {
            if (userService.loadUserByUsername(user.getUsername()) == null) {
                userService.createOrUpdate(user);
                System.out.println(user);
                return "redirect:/login";
            }
            err = "Логин занят";
        }
        model.addAttribute("error", err);
        return "register";
    }


    @GetMapping(params = "/login/logout")
    public String logout1() {
        return "redirect:/logout";
    }

    @GetMapping(params = "/register/logout")
    public String logout2() {
        return "redirect:/logout";
    }
}