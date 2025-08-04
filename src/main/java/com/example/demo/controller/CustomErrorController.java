package com.example.demo.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Page not found");
                model.addAttribute("status", "404");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Access denied");
                model.addAttribute("status", "403");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Internal server error");
                model.addAttribute("status", "500");
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("error", "Bad request");
                model.addAttribute("status", "400");
            } else {
                model.addAttribute("error", "An error s");
                model.addAttribute("status", statusCode);
            }
        }
        
        return "error";
    }
} 