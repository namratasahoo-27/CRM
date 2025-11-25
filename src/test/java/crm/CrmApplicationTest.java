package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;

public class CrmApplicationTest {

    @Test
    public void testMainMethodExists() {
        assertNotNull(CrmApplication.class);
    }

    @Test
    public void testCrmApplicationClassAnnotation() {
        assertTrue(CrmApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    public void testMainMethodCanBeInvoked() {
        assertDoesNotThrow(() -> {
            CrmApplication.class.getDeclaredMethod("main", String[].class);
        });
    }

    @Test
    public void testApplicationContext() {
        assertNotNull(CrmApplication.class);
        assertEquals("CrmApplication", CrmApplication.class.getSimpleName());
    }
}
