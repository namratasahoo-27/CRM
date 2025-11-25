package crm.service;

import crm.entity.Pdf;
import crm.repository.PdfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfServiceImplTest {

    private PdfServiceImpl pdfService;
    private PdfRepository pdfRepository;

    @BeforeEach
    public void setUp() {
        pdfRepository = mock(PdfRepository.class);
        pdfService = new PdfServiceImpl(pdfRepository);
    }

    @Test
    public void testFindByName() {
        Pdf pdf = Pdf.builder()
                .id(1L)
                .name("test-pdf")
                .content("Test content")
                .build();

        when(pdfRepository.findByName("test-pdf")).thenReturn(pdf);

        Pdf result = pdfService.findByName("test-pdf");

        assertNotNull(result);
        assertEquals("test-pdf", result.getName());
        verify(pdfRepository).findByName("test-pdf");
    }

    @Test
    public void testFindByNameNotFound() {
        when(pdfRepository.findByName("nonexistent")).thenReturn(null);

        Pdf result = pdfService.findByName("nonexistent");

        assertNull(result);
        verify(pdfRepository).findByName("nonexistent");
    }

    @Test
    public void testSavePdf() {
        Pdf pdf = Pdf.builder()
                .name("new-pdf")
                .content("New content")
                .build();

        pdfService.savePdf(pdf);

        verify(pdfRepository).save(pdf);
    }

    @Test
    public void testSavePdfWithNullValues() {
        Pdf pdf = Pdf.builder().build();

        pdfService.savePdf(pdf);

        verify(pdfRepository).save(pdf);
    }

    @Test
    public void testConstructor() {
        assertNotNull(pdfService);
    }

    @Test
    public void testFindByNameWithEmptyString() {
        when(pdfRepository.findByName("")).thenReturn(null);

        Pdf result = pdfService.findByName("");

        assertNull(result);
        verify(pdfRepository).findByName("");
    }

    @Test
    public void testSavePdfMultipleTimes() {
        Pdf pdf1 = Pdf.builder().name("pdf1").build();
        Pdf pdf2 = Pdf.builder().name("pdf2").build();

        pdfService.savePdf(pdf1);
        pdfService.savePdf(pdf2);

        verify(pdfRepository, times(2)).save(any(Pdf.class));
    }
}
