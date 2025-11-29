package crm.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractPdfViewTest {

    private AbstractPdfView abstractPdfView;
    private Map<String, Object> model;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        // Create a concrete implementation for testing
        abstractPdfView = new TestPdfView();
        model = new HashMap<>();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testConstructorSetsContentType() {
        assertEquals("application/pdf", abstractPdfView.getContentType());
    }

    @Test
    void testGeneratesDownloadContent() {
        assertTrue(abstractPdfView.generatesDownloadContent());
    }

    @Test
    void testGetViewerPreferences() {
        int viewerPreferences = abstractPdfView.getViewerPreferences();
        assertEquals(PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage, viewerPreferences);
    }

    @Test
    void testRenderMergedOutputModel() throws Exception {
        model.put("testData", "test value");

        assertDoesNotThrow(() -> {
            abstractPdfView.renderMergedOutputModel(model, request, response);
        });

        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getContentAsByteArray().length > 0);
    }

    @Test
    void testRenderMergedOutputModelWithEmptyModel() throws Exception {
        assertDoesNotThrow(() -> {
            abstractPdfView.renderMergedOutputModel(model, request, response);
        });

        assertEquals("application/pdf", response.getContentType());
    }

    @Test
    void testPrepareWriter() throws DocumentException {
        PdfWriter mockWriter = mock(PdfWriter.class);

        assertDoesNotThrow(() -> {
            abstractPdfView.prepareWriter(model, mockWriter, request);
        });

        verify(mockWriter).setViewerPreferences(PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage);
    }

    @Test
    void testPrepareWriterWithNullModel() throws DocumentException {
        PdfWriter mockWriter = mock(PdfWriter.class);

        assertDoesNotThrow(() -> {
            abstractPdfView.prepareWriter(null, mockWriter, request);
        });

        verify(mockWriter).setViewerPreferences(anyInt());
    }

    @Test
    void testBuildPdfMetadataDoesNotThrow() {
        Document mockDocument = mock(Document.class);

        assertDoesNotThrow(() -> {
            abstractPdfView.buildPdfMetadata(model, mockDocument, request);
        });
    }

    @Test
    void testBuildPdfMetadataWithNullParameters() {
        assertDoesNotThrow(() -> {
            abstractPdfView.buildPdfMetadata(null, null, null);
        });
    }

    @Test
    void testAbstractMethodIsAbstract() throws NoSuchMethodException {
        var buildPdfDocumentMethod = AbstractPdfView.class.getDeclaredMethod(
                "buildPdfDocument", Map.class, Document.class, PdfWriter.class,
                HttpServletRequest.class, HttpServletResponse.class);

        assertTrue(java.lang.reflect.Modifier.isAbstract(buildPdfDocumentMethod.getModifiers()));
    }

    @Test
    void testClassIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractPdfView.class.getModifiers()));
    }

    @Test
    void testExtendsAbstractView() {
        assertTrue(org.springframework.web.servlet.view.AbstractView.class.isAssignableFrom(AbstractPdfView.class));
    }

    @Test
    void testModelPassedToBuildPdfDocument() throws Exception {
        TestPdfView testView = new TestPdfView();
        model.put("key1", "value1");
        model.put("key2", "value2");

        testView.renderMergedOutputModel(model, request, response);

        assertEquals(model, testView.getReceivedModel());
    }

    @Test
    void testRequestPassedToBuildPdfDocument() throws Exception {
        TestPdfView testView = new TestPdfView();
        request.setParameter("testParam", "testValue");

        testView.renderMergedOutputModel(model, request, response);

        assertEquals(request, testView.getReceivedRequest());
    }

    @Test
    void testResponsePassedToBuildPdfDocument() throws Exception {
        TestPdfView testView = new TestPdfView();

        testView.renderMergedOutputModel(model, request, response);

        assertEquals(response, testView.getReceivedResponse());
    }

    @Test
    void testDocumentAndWriterPassedToBuildPdfDocument() throws Exception {
        TestPdfView testView = new TestPdfView();

        testView.renderMergedOutputModel(model, request, response);

        assertNotNull(testView.getReceivedDocument());
        assertNotNull(testView.getReceivedWriter());
    }

    @Test
    void testDefaultViewerPreferencesValue() {
        int expected = PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
        assertEquals(expected, abstractPdfView.getViewerPreferences());
    }

    @Test
    void testPdfContentTypeIsSet() {
        assertEquals("application/pdf", abstractPdfView.getContentType());
    }

    @Test
    void testMethodsAreProtected() throws NoSuchMethodException {
        var prepareWriterMethod = AbstractPdfView.class.getDeclaredMethod(
                "prepareWriter", Map.class, PdfWriter.class, HttpServletRequest.class);
        var buildPdfMetadataMethod = AbstractPdfView.class.getDeclaredMethod(
                "buildPdfMetadata", Map.class, Document.class, HttpServletRequest.class);
        var getViewerPreferencesMethod = AbstractPdfView.class.getDeclaredMethod("getViewerPreferences");

        assertTrue(java.lang.reflect.Modifier.isProtected(prepareWriterMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isProtected(buildPdfMetadataMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isProtected(getViewerPreferencesMethod.getModifiers()));
    }

    @Test
    void testRenderMergedOutputModelIsFinal() throws NoSuchMethodException {
        var renderMethod = AbstractPdfView.class.getDeclaredMethod(
                "renderMergedOutputModel", Map.class, HttpServletRequest.class, HttpServletResponse.class);

        assertTrue(java.lang.reflect.Modifier.isFinal(renderMethod.getModifiers()));
    }

    // Test implementation of AbstractPdfView for testing purposes
    private static class TestPdfView extends AbstractPdfView {
        private Map<String, Object> receivedModel;
        private Document receivedDocument;
        private PdfWriter receivedWriter;
        private HttpServletRequest receivedRequest;
        private HttpServletResponse receivedResponse;

        @Override
        protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
            this.receivedModel = model;
            this.receivedDocument = document;
            this.receivedWriter = writer;
            this.receivedRequest = request;
            this.receivedResponse = response;

            // Add minimal content to create valid PDF
            document.add(new com.itextpdf.text.Paragraph("Test PDF Content"));
        }

        // Getters for testing
        public Map<String, Object> getReceivedModel() { return receivedModel; }
        public Document getReceivedDocument() { return receivedDocument; }
        public PdfWriter getReceivedWriter() { return receivedWriter; }
        public HttpServletRequest getReceivedRequest() { return receivedRequest; }
        public HttpServletResponse getReceivedResponse() { return receivedResponse; }
    }

    // Test subclass with custom viewer preferences
    private static class CustomViewerPreferencesPdfView extends AbstractPdfView {
        @Override
        protected int getViewerPreferences() {
            return PdfWriter.ALLOW_COPY | PdfWriter.PageLayoutTwoColumnLeft;
        }

        @Override
        protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
            document.add(new com.itextpdf.text.Paragraph("Custom viewer preferences"));
        }
    }

    @Test
    void testCustomViewerPreferences() {
        CustomViewerPreferencesPdfView customView = new CustomViewerPreferencesPdfView();
        int expected = PdfWriter.ALLOW_COPY | PdfWriter.PageLayoutTwoColumnLeft;
        assertEquals(expected, customView.getViewerPreferences());
    }

    @Test
    void testPrepareWriterUsesCustomViewerPreferences() throws DocumentException {
        CustomViewerPreferencesPdfView customView = new CustomViewerPreferencesPdfView();
        PdfWriter mockWriter = mock(PdfWriter.class);

        customView.prepareWriter(model, mockWriter, request);

        verify(mockWriter).setViewerPreferences(PdfWriter.ALLOW_COPY | PdfWriter.PageLayoutTwoColumnLeft);
    }
}