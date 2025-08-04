package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam(required = false) String username,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String confirmPassword,
                             Model model) {
        // Debug logging
        System.out.println("Registration attempt - Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Confirm Password: " + confirmPassword);

        // Check if any required field is missing
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "register";
        }
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            return "register";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password is required");
            return "register";
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            model.addAttribute("error", "Confirm password is required");
            return "register";
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        
        // Check password requirements
        if (password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long");
            return "register";
        }
        
        if (!password.matches(".*[A-Z].*")) {
            model.addAttribute("error", "Password must contain at least one uppercase letter");
            return "register";
        }
        
        if (!password.matches(".*[0-9].*")) {
            model.addAttribute("error", "Password must contain at least one number");
            return "register";
        }
        
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // In a real app, you should hash the password
        userRepository.save(user);
        
        return "redirect:/user/login";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                          @RequestParam String password,
                          Model model) {
        User user = userRepository.findByUsername(username)
            .orElse(null);
            
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        
        // Redirect to tasks page with username parameter
        return "redirect:/tasks?username=" + username;
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/user/login";
    }
}
