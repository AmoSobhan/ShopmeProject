package com.shopme.admin.user;

import com.shopme.commen.entity.Role;
import com.shopme.commen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/users")
    public String getUsersList(Model model) {
        List<User> listUsers = service.listAllUsers();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("users/new")
    public String newUser(Model model) {
        User user = new User();
        List<Role> rolesList = service.listAllRoles();

        model.addAttribute("user", user);
        model.addAttribute("rolesList", rolesList);
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @PostMapping("users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        System.out.println(user);
        service.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "The User has been saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = service.getUserById(id);
            List<Role> rolesList = service.listAllRoles();
            model.addAttribute("user", user);
            model.addAttribute("rolesList", rolesList);
            model.addAttribute("pageTitle", "Edit User");
            return "user_form";
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message",
                    "the User with ID: " + id + " deleted.");
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("users/{id}/enabled/{state}")
    public String updateUserEnableStatus(@PathVariable Integer id, @PathVariable boolean state,
                                         RedirectAttributes redirectAttributes) {
        service.updateUserEnableStatus(id, state);
        String newState = state ? "Enabled" : "Disabled";
        String message = "The User with ID: " + id + " has been " + newState + ".";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }
}