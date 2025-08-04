package com.example.demo.repository;

import com.example.demo.model.Feedback;
import com.example.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByTask(Task task);
    List<Feedback> findByTaskId(Long taskId);
} 