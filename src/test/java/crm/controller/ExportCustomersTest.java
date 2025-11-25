package crm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportCustomersTest {

    @Test
    public void testExportCustomersClassExists() {
        assertNotNull(ExportCustomers.class);
    }

    @Test
    public void testExportCustomersCanBeInstantiated() {
        ExportCustomers exportCustomers = new ExportCustomers();
        assertNotNull(exportCustomers);
    }

    @Test
    public void testExportCustomersIsNotAnnotatedAsController() {
        assertFalse(ExportCustomers.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }

    @Test
    public void testExportCustomersClassName() {
        assertEquals("ExportCustomers", ExportCustomers.class.getSimpleName());
    }
}
