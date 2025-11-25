package crm.controller;

import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExportTest {

    private Export export;
    private UserService userService;
    private Model model;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        export = new Export(userService);
        model = mock(Model.class);
    }

    @Test
    public void testDownload() {
        String viewName = export.download(model);
        assertNotNull(viewName);
        assertEquals("", viewName);
    }

    @Test
    public void testDownloadCallsUserService() {
        export.download(model);
        verify(userService).listAllUsers();
    }

    @Test
    public void testDownloadAddsUsersToModel() {
        export.download(model);
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    public void testConstructorWithUserService() {
        assertNotNull(export);
    }

    @Test
    public void testDownloadReturnsEmptyString() {
        String result = export.download(model);
        assertEquals("", result);
        assertTrue(result.isEmpty());
    }
}
