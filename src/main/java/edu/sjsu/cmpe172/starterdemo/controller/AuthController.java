package edu.sjsu.cmpe172.starterdemo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sjsu.cmpe172.starterdemo.model.Admin;
import edu.sjsu.cmpe172.starterdemo.model.User;
import edu.sjsu.cmpe172.starterdemo.service.AdminService;
import edu.sjsu.cmpe172.starterdemo.service.UserService;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    public AuthController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam(defaultValue = "user") String role,
                        HttpSession session,
                        RedirectAttributes ra) {
        if ("admin".equals(role)) {
            Optional<Admin> admin = adminService.authenticate(email, password);
            if (admin.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Invalid admin credentials.");
                return "redirect:/login";
            }
            session.setAttribute("user_email", email);
            session.setAttribute("user_role", "admin");
            return "redirect:/admin/dashboard";
        } else {
            Optional<User> user = userService.authenticate(email, password);
            if (user.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Invalid email or password.");
                return "redirect:/login";
            }
            session.setAttribute("user_email", email);
            session.setAttribute("user_role", "user");
            return "redirect:/appointments";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes ra) {
        boolean ok = userService.register(user);
        if (!ok) {
            ra.addFlashAttribute("errorMsg", "That email is already registered.");
            return "redirect:/register";
        }
        ra.addFlashAttribute("successMsg", "Account created — please log in.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
