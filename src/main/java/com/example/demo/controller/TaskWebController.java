package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskWebController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskWebController.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping
    public String taskPage(@RequestParam(required = false) String username, Model model) {
        if (username == null) {
            return "redirect:/login?error=Please login first";
        }
        
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
        
        logger.debug("Task counts - Completed: {}, Pending: {}, High: {}, Medium: {}, Low: {}", 
                    completedTasks, pendingTasks, highPriority, mediumPriority, lowPriority);
        
        model.addAttribute("username", username);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("highPriority", highPriority);
        model.addAttribute("mediumPriority", mediumPriority);
        model.addAttribute("lowPriority", lowPriority);
        model.addAttribute("tasks", tasks);
        
        return "task";
    }

    @PostMapping("/create")
    @Transactional
    public String createTask(@RequestParam String username,
                           @RequestParam String title,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String dueDate,
                           @RequestParam String priority,
                           @RequestParam String status) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login?error=User not found";
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        if (dueDate != null && !dueDate.isEmpty()) {
            task.setDueDate(LocalDate.parse(dueDate));
        }
        task.setPriority(priority);
        task.setStatus(status.toUpperCase());
        task.setUser(userOptional.get());

        taskRepository.save(task);
        return "redirect:/tasks?username=" + username;
    }

    @PostMapping("/{id}/complete")
    @Transactional
    public String completeTask(@PathVariable Long id, @RequestParam String username) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (taskOptional.isEmpty() || userOptional.isEmpty()) {
            return "redirect:/tasks?username=" + username + "&error=Task or user not found";
        }

        Task task = taskOptional.get();
        User user = userOptional.get();
        
        // Verify task belongs to user
        if (!task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks?username=" + username + "&error=Unauthorized";
        }

        logger.debug("Before completing task - ID: {}, Status: {}, Priority: {}", 
                    task.getId(), task.getStatus(), task.getPriority());

        task.setStatus("COMPLETED");
        task.calculatePriority();
        taskRepository.save(task);
        taskRepository.flush();

        // Verify the changes after save
        Task updatedTask = taskRepository.findById(id).orElse(null);
        if (updatedTask != null) {
            logger.debug("After completing task - ID: {}, Status: {}, Priority: {}", 
                        updatedTask.getId(), updatedTask.getStatus(), updatedTask.getPriority());
        }

        // Clear persistence context to force a refresh
        taskRepository.flush();
        entityManager.clear();
        
        return "redirect:/tasks?username=" + username;
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String deleteTask(@PathVariable Long id, @RequestParam String username) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (taskOptional.isEmpty() || userOptional.isEmpty()) {
            return "redirect:/tasks?username=" + username + "&error=Task or user not found";
        }

        Task task = taskOptional.get();
        User user = userOptional.get();
        
        // Verify task belongs to user
        if (!task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks?username=" + username + "&error=Unauthorized";
        }

        try {
            taskRepository.delete(task);
            taskRepository.flush();
            return "redirect:/tasks?username=" + username;
        } catch (Exception e) {
            logger.error("Error deleting task: {}", e.getMessage());
            return "redirect:/tasks?username=" + username + "&error=Error deleting task";
        }
    }

    @GetMapping("/debug/{username}")
    @ResponseBody
    public String debugTasks(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();
        List<Task> tasks = taskRepository.findByUser(user);
        
        StringBuilder debug = new StringBuilder();
        debug.append("Task States:\n");
        
        for (Task task : tasks) {
            debug.append(String.format("Task ID: %d, Title: %s, Status: %s, Priority: %s\n",
                task.getId(), task.getTitle(), task.getStatus(), task.getPriority()));
        }
        
        debug.append("\nCounts:\n");
        debug.append(String.format("Completed: %d\n", taskRepository.countCompletedTasksByUser(user)));
        debug.append(String.format("Pending: %d\n", taskRepository.countPendingTasksByUser(user)));
        debug.append(String.format("High Priority: %d\n", taskRepository.countTasksByPriority(user, "High")));
        debug.append(String.format("Medium Priority: %d\n", taskRepository.countTasksByPriority(user, "Medium")));
        debug.append(String.format("Low Priority: %d\n", taskRepository.countTasksByPriority(user, "Low")));
        
        return debug.toString();
    }
} 