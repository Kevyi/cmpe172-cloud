package edu.sjsu.cmpe172.starterdemo.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("sessionUser")
    public String sessionUser(HttpSession session) {
        return (String) session.getAttribute("user_email");
    }

    @ModelAttribute("sessionRole")
    public String sessionRole(HttpSession session) {
        return (String) session.getAttribute("user_role");
    }
}
