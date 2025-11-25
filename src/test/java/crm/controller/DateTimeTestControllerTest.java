package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DateTimeTestControllerTest {

    private DateTimeTestController dateTimeTestController;
    private Model model;

    @BeforeEach
    public void setUp() {
        dateTimeTestController = new DateTimeTestController();
        model = mock(Model.class);
    }

    @Test
    public void testDateTimeTest() {
        String viewName = dateTimeTestController.dateTimeTest(model);
        assertNotNull(viewName);
        assertEquals("date/test", viewName);
    }

    @Test
    public void testDateTimeTestAddsAttributes() {
        dateTimeTestController.dateTimeTest(model);

        verify(model).addAttribute(eq("standardDate"), any());
        verify(model).addAttribute(eq("localDateTime"), any());
        verify(model).addAttribute(eq("localDate"), any());
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    public void testDateTimeTestReturnsCorrectView() {
        String result = dateTimeTestController.dateTimeTest(model);
        assertEquals("date/test", result);
    }

    @Test
    public void testDateTimeTestModelNotNull() {
        assertDoesNotThrow(() -> dateTimeTestController.dateTimeTest(model));
    }

    @Test
    public void testDateTimeTestAddsAllDateTypes() {
        dateTimeTestController.dateTimeTest(model);
        verify(model, times(4)).addAttribute(anyString(), any());
    }
}
