package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    
    default long countCompletedTasksByUser(User user) {
        return findByUser(user).stream()
            .filter(task -> task.getStatus() != null && task.getStatus().equalsIgnoreCase("COMPLETED"))
            .count();
    }
    
    default long countPendingTasksByUser(User user) {
        return findByUser(user).stream()
            .filter(task -> task.getStatus() != null && task.getStatus().equalsIgnoreCase("PENDING"))
            .count();
    }
    
    default long countTasksByPriority(User user, String priority) {
        return findByUser(user).stream()
            .filter(task -> {
                // For High and Medium priority, only count non-completed tasks
                if (("High".equalsIgnoreCase(priority) || "Medium".equalsIgnoreCase(priority)) 
                    && task.getStatus() != null 
                    && task.getStatus().equalsIgnoreCase("COMPLETED")) {
                    return false;
                }
                return priority.equalsIgnoreCase(task.getPriority());
            })
            .count();
    }
} 