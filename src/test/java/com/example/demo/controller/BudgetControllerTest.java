package com.example.demo.controller;

import com.example.demo.model.Budget;
import com.example.demo.model.User;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BudgetControllerTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private BudgetController budgetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBudgetPage_UserFound() {
        String username = "testUser";
        User user = new User();
        List<Budget> incomes = Arrays.asList(new Budget(), new Budget());
        List<Budget> expenses = Arrays.asList(new Budget());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndType(user, "income")).thenReturn(incomes);
        when(budgetRepository.findByUserAndType(user, "expense")).thenReturn(expenses);

        String viewName = budgetController.budgetPage(username, model);

        assertEquals("budget", viewName);
        verify(model).addAttribute("username", username);
        verify(model).addAttribute("incomes", incomes);
        verify(model).addAttribute("expenses", expenses);
    }

    @Test
    public void testBudgetPage_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String viewName = budgetController.budgetPage(username, model);

        assertEquals("redirect:/login?error=User not found", viewName);
    }

    @Test
    public void testAddBudgetItem_UserFound() {
        String username = "testUser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String viewName = budgetController.addBudgetItem(username, "income", "Salary", 1000.0, "2023-01-01");

        assertEquals("redirect:/budget?username=" + username, viewName);
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    public void testAddBudgetItem_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String viewName = budgetController.addBudgetItem(username, "income", "Salary", 1000.0, "2023-01-01");

        assertEquals("redirect:/login?error=User not found", viewName);
    }

    @Test
    public void testDeleteBudgetItem_UserFound() {
        String username = "testUser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String viewName = budgetController.deleteBudgetItem(1L, username);

        assertEquals("redirect:/budget?username=" + username, viewName);
        verify(budgetRepository).deleteById(1L);
    }

    @Test
    public void testDeleteBudgetItem_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String viewName = budgetController.deleteBudgetItem(1L, username);

        assertEquals("redirect:/login?error=User not found", viewName);
    }
} 