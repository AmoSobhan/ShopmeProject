package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.commen.entity.Role;
import com.shopme.commen.entity.User;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    final
    UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public String getUsersFirstPage(Model model) {
        return getUserslistByPage(1, model, "firstName", "asc", null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String getUserslistByPage(@PathVariable("pageNum") Integer pageNum, Model model,
                                     @Param("sortField") String sortField, @Param("sordtDir") String sortDir,
                                     @Param("keyword") String keyword) {
        if (pageNum <= 0) {
            pageNum = 1;
        }

        Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNum - 1) * UserService.USER_PER_PAGE + 1;
        long endCount = startCount + page.getNumberOfElements() - 1;
//		long endCount = startCount + UserService.USER_PER_PAGE - 1;
//		if (endCount > page.getTotalElements()) {
//			endCount = page.getTotalElements();
//		}
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

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
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);

            User savedUser = service.saveUser(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {

            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            service.saveUser(user);
        }

        redirectAttributes.addFlashAttribute("message", "The User has been saved successfully.");

        String userEmail = user.getEmail();
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + userEmail;
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
            redirectAttributes.addFlashAttribute("message", "the User with ID: " + id + " deleted.");
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