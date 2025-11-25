package crm.controller;

import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    private RegisterController registerController;
    private UserService userService;
    private Model model;
    private BindingResult bindingResult;
    private User user;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        registerController = new RegisterController(userService);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    public void testShowRegistrationPage() {
        String viewName = registerController.showRegistrationPage(model, user);
        assertEquals("register", viewName);
        verify(model).addAttribute("user", user);
    }

    @Test
    public void testProcessRegistrationFormSuccess() {
        when(userService.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("success", viewName);
        verify(userService).saveUser(user);
    }

    @Test
    public void testProcessRegistrationFormUserAlreadyExists() {
        User existingUser = User.builder().username("testuser").build();
        when(userService.findByUsername("testuser")).thenReturn(existingUser);

        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("register", viewName);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(user);
    }

    @Test
    public void testProcessRegistrationFormWithErrors() {
        when(userService.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("redirect:/register", viewName);
        verify(userService, never()).saveUser(user);
    }

    @Test
    public void testConstructor() {
        assertNotNull(registerController);
    }

    @Test
    public void testShowRegistrationPageAddsUserAttribute() {
        registerController.showRegistrationPage(model, user);
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testProcessRegistrationFormWithNullUser() {
        when(userService.findByUsername(null)).thenReturn(null);
        User nullUser = User.builder().build();

        assertDoesNotThrow(() -> registerController.processRegistrationForm(model, nullUser, bindingResult));
    }
}
