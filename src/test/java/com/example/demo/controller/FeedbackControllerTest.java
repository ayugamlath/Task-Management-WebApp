package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.Task;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FeedbackControllerTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private FeedbackController feedbackController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFeedback_TaskCompleted() {
        Long taskId = 1L;
        Task task = new Task();
        task.setStatus("COMPLETED");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        String viewName = feedbackController.addFeedback(taskId, "Great job!", 5, redirectAttributes);

        assertEquals("redirect:/feedback/task/" + taskId, viewName);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    public void testAddFeedback_TaskNotCompleted() {
        Long taskId = 1L;
        Task task = new Task();
        task.setStatus("IN_PROGRESS");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        String viewName = feedbackController.addFeedback(taskId, "Great job!", 5, redirectAttributes);

        assertEquals("redirect:/feedback/task/" + taskId, viewName);
        verify(redirectAttributes).addFlashAttribute("error", "You can only add feedback for completed tasks.");
    }

    @Test
    public void testEditFeedback_FeedbackFound() {
        Long feedbackId = 1L;
        Feedback feedback = new Feedback();
        Task task = new Task();
        feedback.setTask(task);
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        String viewName = feedbackController.editFeedback(feedbackId, "Updated comment", 4, redirectAttributes);

        assertEquals("redirect:/feedback/task/" + task.getId(), viewName);
        verify(feedbackRepository).save(feedback);
    }

    @Test
    public void testEditFeedback_FeedbackNotFound() {
        Long feedbackId = 1L;
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        String viewName = feedbackController.editFeedback(feedbackId, "Updated comment", 4, redirectAttributes);

        assertEquals("redirect:/tasks", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Feedback not found.");
    }

    @Test
    public void testDeleteFeedback_FeedbackFound() {
        Long feedbackId = 1L;
        Feedback feedback = new Feedback();
        Task task = new Task();
        feedback.setTask(task);
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        String viewName = feedbackController.deleteFeedback(feedbackId, redirectAttributes);

        assertEquals("redirect:/feedback/task/" + task.getId(), viewName);
        verify(feedbackRepository).delete(feedback);
    }

    @Test
    public void testDeleteFeedback_FeedbackNotFound() {
        Long feedbackId = 1L;
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        String viewName = feedbackController.deleteFeedback(feedbackId, redirectAttributes);

        assertEquals("redirect:/tasks", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Feedback not found.");
    }

    @Test
    public void testViewTaskFeedback_TaskFound() {
        Long taskId = 1L;
        Task task = new Task();
        List<Feedback> feedbacks = Arrays.asList(new Feedback(), new Feedback());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(feedbackRepository.findByTask(task)).thenReturn(feedbacks);

        String viewName = feedbackController.viewTaskFeedback(taskId, model);

        assertEquals("task-feedback", viewName);
        verify(model).addAttribute("task", task);
        verify(model).addAttribute("feedbacks", feedbacks);
        verify(model).addAttribute("canAddFeedback", task.getStatus().equals("COMPLETED"));
    }

    @Test
    public void testViewTaskFeedback_TaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        String viewName = feedbackController.viewTaskFeedback(taskId, model);

        assertEquals("redirect:/tasks", viewName);
    }
} 