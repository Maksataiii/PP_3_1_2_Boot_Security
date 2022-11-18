package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(params = "logout")
    public String logout() {
        return "redirect:/logout";
    }

    //--- CREATE

    /***
     * Подготовить объект User для сохранения в базу
     */
    @GetMapping("/new")
    public String createForm(@ModelAttribute("user") User user) {
        return "user-create";
    }

    /***
     * Сохранить в базу
     */
    @PostMapping
    public String create(@ModelAttribute("user") User user) {
        user.setRoles(Collections.singleton(roleService.createRole("USER")));
        user.setPassword(passwordEncoder.encode("0000"));
        user.setUsername(user.getUsername().toLowerCase());
        if (userService.loadUserByUsername(user.getUsername()) == null) {
            userService.createOrUpdate(user);
            return "redirect:/admin";
        } else {
            return "redirect:/admin/new?error=login is exists";
        }
    }

    //--- READ

    /***
     * Получить всех пользователей
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("users", userService.getList());
        return "user_list";
    }

    /***
     * Получить одного пользователя
     */
    @GetMapping("/{id}")
    public String read(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("user", userService.get(id));
        return "profile";
    }

    //--- UPDATE

    /***
     * Подготовить изменения для объекта User
     */
    @GetMapping("/{id}/edit")
    public String editForm(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("user", userService.get(id));
        return "user-update";
    }

    /***
     * Сохранить изменённого пользователя
     */
    @PatchMapping()
    public String edit(@ModelAttribute("user") User user) {
        User oldUser = userService.get(user.getId());
        user.setUsername(oldUser.getUsername());
        user.setPassword(oldUser.getPassword());
        user.setRoles(oldUser.getRoles());
        userService.createOrUpdate(user);
        return "redirect:/admin";
    }

    //--- DELETE

    /***
     * Удалить пользователя (подготовки объекта User не требуется)
     */
    @DeleteMapping()
    public String delete(Model model, @Param("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}