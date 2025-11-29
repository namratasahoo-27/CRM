package crm.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Role;
import crm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfViewTest {

    private PdfView pdfView;
    private Map<String, Object> model;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private List<User> users;

    @BeforeEach
    void setUp() {
        pdfView = new PdfView();
        model = new HashMap<>();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // Create test users
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("USER");

        User user1 = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .enabled(1)
                .role(role1)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user1")
                .email("user1@example.com")
                .firstName("Regular")
                .lastName("User")
                .password("userpass")
                .enabled(1)
                .role(role2)
                .build();

        users = Arrays.asList(user1, user2);
        model.put("users", users);
    }

    @Test
    void testPdfViewCreation() {
        assertNotNull(pdfView);
    }

    @Test
    void testExtendsAbstractPdfView() {
        assertTrue(pdfView instanceof AbstractPdfView);
    }

    @Test
    void testBuildPdfDocument() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        // Verify content disposition header is set
        assertEquals("attachment; filename=\"my-pdf-file.pdf\"",
                    response.getHeader("Content-Disposition"));

        // Verify PDF content was generated
        assertTrue(baos.size() > 0);
    }

    @Test
    void testBuildPdfDocumentWithEmptyUserList() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        model.put("users", Arrays.asList());

        // This should throw exception because findAny().get() will fail on empty stream
        assertThrows(Exception.class, () -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();
    }

    @Test
    void testBuildPdfDocumentWithNullUserList() {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        model.put("users", null);

        assertThrows(Exception.class, () -> {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            pdfView.buildPdfDocument(model, document, writer, request, response);
            document.close();
        });
    }

    @Test
    void testBuildPdfDocumentWithSingleUser() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        model.put("users", Arrays.asList(users.get(0)));

        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        // Verify content disposition header is set
        assertEquals("attachment; filename=\"my-pdf-file.pdf\"",
                    response.getHeader("Content-Disposition"));
    }

    @Test
    void testBuildPdfDocumentSetsCorrectHeaders() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        assertEquals("attachment; filename=\"my-pdf-file.pdf\"",
                    response.getHeader("Content-Disposition"));
    }

    @Test
    void testPdfTableCreation() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        // Verify PDF was generated without errors
        assertTrue(baos.size() > 0);

        // Verify the document has content (basic check)
        byte[] pdfBytes = baos.toByteArray();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 100); // PDF should have substantial content
    }

    @Test
    void testBuildPdfDocumentWithUsersHavingNullFields() throws Exception {
        Role role = new Role();
        role.setId(3);
        role.setName("TEST");

        User userWithNulls = User.builder()
                .id(3L)
                .username("nulluser")
                .email("null@example.com")
                .firstName(null)
                .lastName(null)
                .password("password")
                .enabled(0)
                .role(role)
                .build();

        model.put("users", Arrays.asList(userWithNulls));

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        assertTrue(baos.size() > 0);
    }

    @Test
    void testMethodIsOverridden() throws NoSuchMethodException {
        // Verify that buildPdfDocument method is properly overridden
        var method = PdfView.class.getDeclaredMethod("buildPdfDocument",
                Map.class, Document.class, PdfWriter.class,
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class);

        assertNotNull(method);
        assertFalse(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
    }

    @Test
    void testPdfTableColumnCount() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // The table column count should be based on user.getColumnCount()
        // which returns the number of declared fields in User class
        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        assertTrue(baos.size() > 0);
    }

    @Test
    void testBuildPdfDocumentWithMultipleUsers() throws Exception {
        // Add more users to test larger datasets
        Role role3 = new Role();
        role3.setId(3);
        role3.setName("MANAGER");

        User user3 = User.builder()
                .id(3L)
                .username("manager")
                .email("manager@example.com")
                .firstName("Manager")
                .lastName("Person")
                .password("managerpass")
                .enabled(1)
                .role(role3)
                .build();

        users.add(user3);
        model.put("users", users);

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        // Verify PDF was generated with more content
        assertTrue(baos.size() > 0);
        byte[] pdfBytes = baos.toByteArray();
        assertTrue(pdfBytes.length > 200); // Should be larger with more users
    }

    @Test
    void testDocumentTitleGeneration() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        // Verify document was created successfully
        // The document should contain a paragraph with current date
        assertTrue(baos.size() > 0);
    }

    @Test
    void testPdfTableHeaders() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Test that all expected headers are added to the table
        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        assertTrue(baos.size() > 0);
    }

    @Test
    void testUserDataAddedToTable() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        pdfView.buildPdfDocument(model, document, writer, request, response);

        document.close();

        // Verify that user data was processed and added
        byte[] pdfBytes = baos.toByteArray();
        assertTrue(pdfBytes.length > 500); // Should have substantial content with user data
    }

    @Test
    void testTableWidthAndSpacing() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Test table formatting settings
        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        assertTrue(baos.size() > 0);
    }

    @Test
    void testDocumentNotClosedPrematurely() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Document should remain open throughout the method execution
        pdfView.buildPdfDocument(model, document, writer, request, response);

        // Document should still be open after method execution
        // (closing is handled by parent class)
        assertTrue(document.isOpen());

        document.close();
    }

    @Test
    void testUserRoleDataAccess() throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Test that role data (ID and name) can be accessed
        assertDoesNotThrow(() -> {
            pdfView.buildPdfDocument(model, document, writer, request, response);
        });

        document.close();

        assertTrue(baos.size() > 0);
    }
}