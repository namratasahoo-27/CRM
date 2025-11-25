package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PdfTest {

    private Pdf pdf;

    @BeforeEach
    public void setUp() {
        pdf = Pdf.builder()
                .id(1L)
                .name("test-document")
                .content("PDF content here")
                .build();
    }

    @Test
    public void testPdfCreation() {
        assertNotNull(pdf);
        assertEquals(1L, pdf.getId());
        assertEquals("test-document", pdf.getName());
        assertEquals("PDF content here", pdf.getContent());
    }

    @Test
    public void testPdfBuilder() {
        Pdf builtPdf = Pdf.builder()
                .name("new-document")
                .content("New PDF content")
                .build();

        assertNotNull(builtPdf);
        assertEquals("new-document", builtPdf.getName());
        assertEquals("New PDF content", builtPdf.getContent());
    }

    @Test
    public void testSetters() {
        pdf.setName("updated-document");
        pdf.setContent("Updated content");

        assertEquals("updated-document", pdf.getName());
        assertEquals("Updated content", pdf.getContent());
    }

    @Test
    public void testNoArgsConstructor() {
        Pdf emptyPdf = new Pdf();
        assertNotNull(emptyPdf);
        assertNull(emptyPdf.getId());
        assertNull(emptyPdf.getName());
    }

    @Test
    public void testAllArgsConstructor() {
        Pdf constructedPdf = new Pdf(2L, "constructor-pdf", "Constructor content");
        assertNotNull(constructedPdf);
        assertEquals(2L, constructedPdf.getId());
        assertEquals("constructor-pdf", constructedPdf.getName());
        assertEquals("Constructor content", constructedPdf.getContent());
    }

    @Test
    public void testPdfWithNullValues() {
        Pdf nullPdf = Pdf.builder().build();
        assertNull(nullPdf.getName());
        assertNull(nullPdf.getContent());
    }

    @Test
    public void testPdfWithMinimumName() {
        Pdf minPdf = Pdf.builder()
                .name("ab")
                .build();

        assertEquals("ab", minPdf.getName());
        assertEquals(2, minPdf.getName().length());
    }

    @Test
    public void testContentIsTransient() {
        Pdf pdfWithContent = Pdf.builder()
                .name("transient-test")
                .content("This is transient")
                .build();

        assertNotNull(pdfWithContent.getContent());
        assertEquals("This is transient", pdfWithContent.getContent());
    }
}
