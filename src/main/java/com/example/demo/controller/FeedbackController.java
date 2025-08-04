package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.Task;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/add")
    public String addFeedback(@RequestParam Long taskId,
                            @RequestParam String comment,
                            @RequestParam int rating,
                            RedirectAttributes redirectAttributes) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task ID: " + taskId));

        if (!task.getStatus().equals("COMPLETED")) {
            redirectAttributes.addFlashAttribute("error", "You can only add feedback for completed tasks.");
            return "redirect:/feedback/task/" + taskId;
        }

        Feedback feedback = new Feedback();
        feedback.setTask(task);
        feedback.setComment(comment);
        feedback.setRating(rating);
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        return "redirect:/feedback/task/" + taskId;
    }

    @PostMapping("/edit/{id}")
    public String editFeedback(@PathVariable Long id,
                             @RequestParam String comment,
                             @RequestParam int rating,
                             RedirectAttributes redirectAttributes) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Feedback not found.");
            return "redirect:/tasks";
        }

        Feedback feedback = feedbackOptional.get();
        feedback.setComment(comment);
        feedback.setRating(rating);
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        return "redirect:/feedback/task/" + feedback.getTask().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Feedback not found.");
            return "redirect:/tasks";
        }

        Feedback feedback = feedbackOptional.get();
        Long taskId = feedback.getTask().getId();
        feedbackRepository.delete(feedback);
        return "redirect:/feedback/task/" + taskId;
    }

    @GetMapping("/task/{taskId}")
    public String viewTaskFeedback(@PathVariable Long taskId, Model model) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            return "redirect:/tasks";
        }

        Task task = taskOptional.get();
        List<Feedback> feedbacks = feedbackRepository.findByTask(task);
        model.addAttribute("task", task);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("canAddFeedback", task.getStatus().equals("COMPLETED"));
        return "task-feedback";
    }
} 