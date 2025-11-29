package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import static org.junit.jupiter.api.Assertions.*;

class PdfTest {

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = new Pdf();
    }

    @Test
    void testPdfCreation() {
        assertNotNull(pdf);
    }

    @Test
    void testNoArgsConstructor() {
        Pdf newPdf = new Pdf();
        assertNotNull(newPdf);
        assertNull(newPdf.getId());
        assertNull(newPdf.getName());
        assertNull(newPdf.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        Pdf newPdf = new Pdf(1L, "test-document.pdf", "PDF content");

        assertNotNull(newPdf);
        assertEquals(1L, newPdf.getId());
        assertEquals("test-document.pdf", newPdf.getName());
        assertEquals("PDF content", newPdf.getContent());
    }

    @Test
    void testBuilderPattern() {
        Pdf builtPdf = Pdf.builder()
                .id(2L)
                .name("invoice.pdf")
                .content("Invoice PDF content")
                .build();

        assertNotNull(builtPdf);
        assertEquals(2L, builtPdf.getId());
        assertEquals("invoice.pdf", builtPdf.getName());
        assertEquals("Invoice PDF content", builtPdf.getContent());
    }

    @Test
    void testSetAndGetId() {
        Long expectedId = 1L;
        pdf.setId(expectedId);
        assertEquals(expectedId, pdf.getId());
    }

    @Test
    void testSetAndGetName() {
        String expectedName = "document.pdf";
        pdf.setName(expectedName);
        assertEquals(expectedName, pdf.getName());
    }

    @Test
    void testSetAndGetContent() {
        String expectedContent = "PDF file content";
        pdf.setContent(expectedContent);
        assertEquals(expectedContent, pdf.getContent());
    }

    @Test
    void testSetIdWithNull() {
        pdf.setId(null);
        assertNull(pdf.getId());
    }

    @Test
    void testSetIdWithZero() {
        pdf.setId(0L);
        assertEquals(0L, pdf.getId());
    }

    @Test
    void testSetIdWithNegativeValue() {
        pdf.setId(-1L);
        assertEquals(-1L, pdf.getId());
    }

    @Test
    void testSetIdWithLargeValue() {
        Long largeId = Long.MAX_VALUE;
        pdf.setId(largeId);
        assertEquals(largeId, pdf.getId());
    }

    @Test
    void testSetNameWithNull() {
        pdf.setName(null);
        assertNull(pdf.getName());
    }

    @Test
    void testSetNameWithEmptyString() {
        pdf.setName("");
        assertEquals("", pdf.getName());
    }

    @Test
    void testSetNameWithMinimumSize() {
        pdf.setName("ab");
        assertEquals("ab", pdf.getName());
    }

    @Test
    void testSetNameWithWhitespace() {
        String nameWithWhitespace = "  document.pdf  ";
        pdf.setName(nameWithWhitespace);
        assertEquals(nameWithWhitespace, pdf.getName());
    }

    @Test
    void testSetNameWithSpecialCharacters() {
        String specialName = "document-2023@company.pdf";
        pdf.setName(specialName);
        assertEquals(specialName, pdf.getName());
    }

    @Test
    void testSetNameWithLongString() {
        String longName = "this_is_a_very_long_pdf_document_name_that_might_exceed_normal_length_expectations.pdf";
        pdf.setName(longName);
        assertEquals(longName, pdf.getName());
    }

    @Test
    void testSetContentWithNull() {
        pdf.setContent(null);
        assertNull(pdf.getContent());
    }

    @Test
    void testSetContentWithEmptyString() {
        pdf.setContent("");
        assertEquals("", pdf.getContent());
    }

    @Test
    void testSetContentWithLargeContent() {
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("PDF content line ").append(i).append("\n");
        }
        String content = largeContent.toString();

        pdf.setContent(content);
        assertEquals(content, pdf.getContent());
    }

    @Test
    void testPdfToString() {
        pdf.setId(1L);
        pdf.setName("test.pdf");
        pdf.setContent("Test content");

        String pdfString = pdf.toString();
        assertNotNull(pdfString);
        assertTrue(pdfString.contains("1"));
        assertTrue(pdfString.contains("test.pdf"));
        assertTrue(pdfString.contains("Test content"));
    }

    @Test
    void testPdfEquals() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("document.pdf")
                .content("Content")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(1L)
                .name("document.pdf")
                .content("Content")
                .build();

        assertEquals(pdf1, pdf2);
    }

    @Test
    void testPdfNotEquals() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("document1.pdf")
                .content("Content1")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(2L)
                .name("document2.pdf")
                .content("Content2")
                .build();

        assertNotEquals(pdf1, pdf2);
    }

    @Test
    void testPdfHashCode() {
        pdf.setId(1L);
        pdf.setName("test.pdf");
        pdf.setContent("Test content");

        int hashCode1 = pdf.hashCode();
        int hashCode2 = pdf.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testPdfHashCodeWithDifferentValues() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("doc1.pdf")
                .content("Content1")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(2L)
                .name("doc2.pdf")
                .content("Content2")
                .build();

        int hashCode1 = pdf1.hashCode();
        int hashCode2 = pdf2.hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testEntityAnnotation() {
        assertTrue(Pdf.class.isAnnotationPresent(Entity.class));
        Entity entity = Pdf.class.getAnnotation(Entity.class);
        assertEquals("pdf", entity.name());
    }

    @Test
    void testDataAnnotation() {
        assertTrue(Pdf.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testBuilderAnnotation() {
        assertTrue(Pdf.class.isAnnotationPresent(Builder.class));
    }

    @Test
    void testNoArgsConstructorAnnotation() {
        assertTrue(Pdf.class.isAnnotationPresent(NoArgsConstructor.class));
    }

    @Test
    void testAllArgsConstructorAnnotation() {
        assertTrue(Pdf.class.isAnnotationPresent(AllArgsConstructor.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        var idField = Pdf.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());
    }

    @Test
    void testNameFieldAnnotations() throws NoSuchFieldException {
        var nameField = Pdf.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(Column.class));
        assertTrue(nameField.isAnnotationPresent(Size.class));

        Column column = nameField.getAnnotation(Column.class);
        assertFalse(column.nullable());

        Size size = nameField.getAnnotation(Size.class);
        assertEquals(2, size.min());
    }

    @Test
    void testContentFieldAnnotations() throws NoSuchFieldException {
        var contentField = Pdf.class.getDeclaredField("content");
        assertTrue(contentField.isAnnotationPresent(Transient.class));
    }

    @Test
    void testPdfWithMultipleOperations() {
        pdf.setId(1L);
        pdf.setName("document.pdf");
        pdf.setContent("Initial content");

        assertEquals(1L, pdf.getId());
        assertEquals("document.pdf", pdf.getName());
        assertEquals("Initial content", pdf.getContent());

        pdf.setId(2L);
        pdf.setName("updated-document.pdf");
        pdf.setContent("Updated content");

        assertEquals(2L, pdf.getId());
        assertEquals("updated-document.pdf", pdf.getName());
        assertEquals("Updated content", pdf.getContent());
    }

    @Test
    void testDefaultValues() {
        Pdf newPdf = new Pdf();

        assertNull(newPdf.getId());
        assertNull(newPdf.getName());
        assertNull(newPdf.getContent());
    }

    @Test
    void testPdfEqualsWithNull() {
        pdf.setId(1L);
        pdf.setName("test.pdf");

        assertNotEquals(pdf, null);
    }

    @Test
    void testPdfEqualsWithDifferentClass() {
        pdf.setId(1L);
        pdf.setName("test.pdf");

        assertNotEquals(pdf, "Not a Pdf");
    }

    @Test
    void testPdfEqualsWithSameReference() {
        pdf.setId(1L);
        pdf.setName("test.pdf");

        assertEquals(pdf, pdf);
    }

    @Test
    void testPdfFieldTypes() throws NoSuchFieldException {
        var idField = Pdf.class.getDeclaredField("id");
        var nameField = Pdf.class.getDeclaredField("name");
        var contentField = Pdf.class.getDeclaredField("content");

        assertEquals(Long.class, idField.getType());
        assertEquals(String.class, nameField.getType());
        assertEquals(String.class, contentField.getType());
    }

    @Test
    void testPdfWithCommonFileExtensions() {
        String[] commonExtensions = {".pdf", ".PDF", ".Pdf"};

        for (String extension : commonExtensions) {
            pdf.setName("document" + extension);
            assertEquals("document" + extension, pdf.getName());
        }
    }

    @Test
    void testPdfContentTransientBehavior() {
        // Content field is marked as @Transient, meaning it won't be persisted
        pdf.setContent("This content should not be persisted");
        assertEquals("This content should not be persisted", pdf.getContent());

        // Verify the field has @Transient annotation
        try {
            var contentField = Pdf.class.getDeclaredField("content");
            assertTrue(contentField.isAnnotationPresent(Transient.class));
        } catch (NoSuchFieldException e) {
            fail("Content field should exist");
        }
    }

    @Test
    void testPdfNameSizeValidation() throws NoSuchFieldException {
        var nameField = Pdf.class.getDeclaredField("name");
        Size sizeAnnotation = nameField.getAnnotation(Size.class);

        assertNotNull(sizeAnnotation);
        assertEquals(2, sizeAnnotation.min());
        assertEquals(Integer.MAX_VALUE, sizeAnnotation.max()); // Default max value
    }
}