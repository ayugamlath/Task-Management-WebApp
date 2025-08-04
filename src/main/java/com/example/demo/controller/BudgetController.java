package com.example.demo.controller;

import com.example.demo.model.Budget;
import com.example.demo.model.User;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String budgetPage(@RequestParam String username, Model model) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login?error=User not found";
        }

        User user = userOptional.get();
        List<Budget> incomes = budgetRepository.findByUserAndType(user, "income");
        List<Budget> expenses = budgetRepository.findByUserAndType(user, "expense");

        double totalIncome = incomes.stream().mapToDouble(Budget::getAmount).sum();
        double totalExpense = expenses.stream().mapToDouble(Budget::getAmount).sum();
        double balance = totalIncome - totalExpense;

        model.addAttribute("username", username);
        model.addAttribute("incomes", incomes);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        return "budget";
    }

    @PostMapping("/add")
    public String addBudgetItem(@RequestParam String username,
                              @RequestParam String type,
                              @RequestParam String category,
                              @RequestParam double amount,
                              @RequestParam String date) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login?error=User not found";
        }

        User user = userOptional.get();
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setType(type);
        budget.setCategory(category);
        budget.setAmount(amount);
        budget.setDate(LocalDate.parse(date));

        budgetRepository.save(budget);
        return "redirect:/budget?username=" + username;
    }

    @PostMapping("/delete/{id}")
    public String deleteBudgetItem(@PathVariable Long id, @RequestParam String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login?error=User not found";
        }

        budgetRepository.deleteById(id);
        return "redirect:/budget?username=" + username;
    }
} 