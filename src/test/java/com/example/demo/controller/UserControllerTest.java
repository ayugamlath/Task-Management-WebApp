package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowRegistrationForm() {
        String viewName = userController.showRegistrationForm(model);
        assertEquals("register", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testRegisterUser_Success() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "Password123";
        String confirmPassword = "Password123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("redirect:/user/login", viewName);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        String username = "existingUser";
        String email = "test@example.com";
        String password = "Password123";
        String confirmPassword = "Password123";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Username already exists");
    }

    @Test
    public void testRegisterUser_EmailExists() {
        String username = "testUser";
        String email = "existing@example.com";
        String password = "Password123";
        String confirmPassword = "Password123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Email already exists");
    }

    @Test
    public void testRegisterUser_PasswordsDoNotMatch() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "Password123";
        String confirmPassword = "DifferentPassword";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Passwords do not match");
    }

    @Test
    public void testRegisterUser_MissingUsername() {
        String username = "";
        String email = "test@example.com";
        String password = "Password123";
        String confirmPassword = "Password123";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Username is required");
    }

    @Test
    public void testRegisterUser_MissingEmail() {
        String username = "testUser";
        String email = "";
        String password = "Password123";
        String confirmPassword = "Password123";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Email is required");
    }

    @Test
    public void testRegisterUser_MissingPassword() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "";
        String confirmPassword = "Password123";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Password is required");
    }

    @Test
    public void testRegisterUser_MissingConfirmPassword() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "Password123";
        String confirmPassword = "";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Confirm password is required");
    }

    @Test
    public void testRegisterUser_PasswordTooShort() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "Pass1";
        String confirmPassword = "Pass1";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Password must be at least 8 characters long");
    }

    @Test
    public void testRegisterUser_PasswordNoUppercase() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";
        String confirmPassword = "password123";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Password must contain at least one uppercase letter");
    }

    @Test
    public void testRegisterUser_PasswordNoNumber() {
        String username = "testUser";
        String email = "test@example.com";
        String password = "Password";
        String confirmPassword = "Password";

        String viewName = userController.registerUser(username, email, password, confirmPassword, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Password must contain at least one number");
    }
} 