package crm.service;

import crm.entity.Pdf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceTest {

    @Test
    public void testPdfServiceInterface() {
        assertNotNull(PdfService.class);
    }

    @Test
    public void testFindByNameMethodExists() throws NoSuchMethodException {
        assertNotNull(PdfService.class.getMethod("findByName", String.class));
    }

    @Test
    public void testSavePdfMethodExists() throws NoSuchMethodException {
        assertNotNull(PdfService.class.getMethod("savePdf", Pdf.class));
    }

    @Test
    public void testPdfServiceIsInterface() {
        assertTrue(PdfService.class.isInterface());
    }

    @Test
    public void testFindByNameReturnType() throws NoSuchMethodException {
        assertEquals(Pdf.class, PdfService.class.getMethod("findByName", String.class).getReturnType());
    }

    @Test
    public void testSavePdfReturnType() throws NoSuchMethodException {
        assertEquals(void.class, PdfService.class.getMethod("savePdf", Pdf.class).getReturnType());
    }
}
