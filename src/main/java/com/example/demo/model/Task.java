package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @Column
    private String description;
    
    @Column
    private LocalDate dueDate;
    
    @Column
    private String priority;
    
    @NotNull(message = "Status is required")
    @Column
    private String status = "PENDING";
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
    
    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Calculate priority based on due date and status
    public void calculatePriority() {
        if (dueDate == null) {
            this.priority = "Low";
            return;
        }
        
        LocalDate today = LocalDate.now();
        long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
        
        if (daysUntilDue <= 0) {
            this.priority = "High";
        } else if (daysUntilDue <= 3) {
            this.priority = "Medium";
        } else {
            this.priority = "Low";
        }
    }
} 