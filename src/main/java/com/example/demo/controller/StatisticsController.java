package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class StatisticsController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/statistics")
    public String statisticsPage(@RequestParam String username, Model model) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login?error=User not found";
        }
        
        User user = userOptional.get();
        List<Task> tasks = taskRepository.findByUser(user);
        
        long completedTasks = taskRepository.countCompletedTasksByUser(user);
        long pendingTasks = taskRepository.countPendingTasksByUser(user);
        long highPriority = taskRepository.countTasksByPriority(user, "High");
        long mediumPriority = taskRepository.countTasksByPriority(user, "Medium");
        long lowPriority = taskRepository.countTasksByPriority(user, "Low");
        
        model.addAttribute("username", username);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("highPriority", highPriority);
        model.addAttribute("mediumPriority", mediumPriority);
        model.addAttribute("lowPriority", lowPriority);
        model.addAttribute("tasks", tasks);
        
        return "statistics";
    }
} 