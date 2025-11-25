package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.error.ErrorController;

import static org.junit.jupiter.api.Assertions.*;

public class MyErrorControllerTest {

    private MyErrorController myErrorController;

    @BeforeEach
    public void setUp() {
        myErrorController = new MyErrorController();
    }

    @Test
    public void testHandleError() {
        String viewName = myErrorController.handleError();
        assertNotNull(viewName);
        assertEquals("error", viewName);
    }

    @Test
    public void testImplementsErrorController() {
        assertTrue(myErrorController instanceof ErrorController);
    }

    @Test
    public void testHandleErrorReturnsString() {
        String result = myErrorController.handleError();
        assertTrue(result instanceof String);
    }

    @Test
    public void testHandleErrorNotNull() {
        assertNotNull(myErrorController.handleError());
    }

    @Test
    public void testHandleErrorNotEmpty() {
        assertFalse(myErrorController.handleError().isEmpty());
    }
}
