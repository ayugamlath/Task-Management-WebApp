package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask() {
        Task task = new Task();
        when(taskService.createTask(task)).thenReturn(task);

        ResponseEntity<Task> response = taskController.createTask(task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService).createTask(task);
    }

    @Test
    public void testGetAllTasks() {
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskService.getAllTasks()).thenReturn(tasks);

        ResponseEntity<List<Task>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(taskService).getAllTasks();
    }

    @Test
    public void testGetTaskById_Found() {
        Task task = new Task();
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<Task> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService).getTaskById(1L);
    }

    @Test
    public void testGetTaskById_NotFound() {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Task> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).getTaskById(1L);
    }

    @Test
    public void testUpdateTask_Found() {
        Task task = new Task();
        when(taskService.updateTask(1L, task)).thenReturn(Optional.of(task));

        ResponseEntity<Task> response = taskController.updateTask(1L, task);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService).updateTask(1L, task);
    }

    @Test
    public void testUpdateTask_NotFound() {
        Task task = new Task();
        when(taskService.updateTask(1L, task)).thenReturn(Optional.empty());

        ResponseEntity<Task> response = taskController.updateTask(1L, task);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).updateTask(1L, task);
    }

    @Test
    public void testDeleteTask_Success() {
        when(taskService.deleteTask(1L)).thenReturn(true);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).deleteTask(1L);
    }

    @Test
    public void testDeleteTask_NotFound() {
        when(taskService.deleteTask(1L)).thenReturn(false);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).deleteTask(1L);
    }
} 