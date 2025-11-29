package crm.view;

import crm.entity.Role;
import crm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvViewTest {

    private CsvView csvView;
    private Map<String, Object> model;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private List<User> users;

    @BeforeEach
    void setUp() {
        csvView = new CsvView();
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
    void testCsvViewCreation() {
        assertNotNull(csvView);
    }

    @Test
    void testExtendsAbstractCsvView() {
        assertTrue(csvView instanceof AbstractCsvView);
    }

    @Test
    void testBuildCsvDocument() throws Exception {
        csvView.buildCsvDocument(model, request, response);

        // Verify content disposition header is set
        assertEquals("attachment; filename=\"my-csv-file.csv\"",
                    response.getHeader("Content-Disposition"));

        // Verify CSV content is generated
        String csvContent = response.getContentAsString();
        assertNotNull(csvContent);
        assertFalse(csvContent.isEmpty());

        // Verify CSV headers are present
        assertTrue(csvContent.contains("FirstName"));
        assertTrue(csvContent.contains("LastName"));
        assertTrue(csvContent.contains("Username"));
        assertTrue(csvContent.contains("Email"));
        assertTrue(csvContent.contains("Password"));
        assertTrue(csvContent.contains("Enabled"));
        assertTrue(csvContent.contains("Role_id"));
        assertTrue(csvContent.contains("Role_name"));

        // Verify user data is present
        assertTrue(csvContent.contains("Admin"));
        assertTrue(csvContent.contains("admin@example.com"));
        assertTrue(csvContent.contains("Regular"));
        assertTrue(csvContent.contains("user1@example.com"));
    }

    @Test
    void testBuildCsvDocumentWithEmptyUserList() throws Exception {
        model.put("users", Arrays.asList());

        assertDoesNotThrow(() -> {
            csvView.buildCsvDocument(model, request, response);
        });

        String csvContent = response.getContentAsString();
        assertNotNull(csvContent);
        // Should still contain headers even with empty list
        assertTrue(csvContent.contains("FirstName"));
    }

    @Test
    void testBuildCsvDocumentWithNullUserList() {
        model.put("users", null);

        assertThrows(NullPointerException.class, () -> {
            csvView.buildCsvDocument(model, request, response);
        });
    }

    @Test
    void testBuildCsvDocumentWithSingleUser() throws Exception {
        model.put("users", Arrays.asList(users.get(0)));

        csvView.buildCsvDocument(model, request, response);

        String csvContent = response.getContentAsString();
        assertNotNull(csvContent);

        // Verify single user data is present
        assertTrue(csvContent.contains("Admin"));
        assertTrue(csvContent.contains("admin@example.com"));

        // Verify second user data is not present
        assertFalse(csvContent.contains("Regular"));
        assertFalse(csvContent.contains("user1@example.com"));
    }

    @Test
    void testBuildCsvDocumentSetsCorrectHeaders() throws Exception {
        csvView.buildCsvDocument(model, request, response);

        assertEquals("attachment; filename=\"my-csv-file.csv\"",
                    response.getHeader("Content-Disposition"));
    }

    @Test
    void testBuildCsvDocumentWithUsersHavingNullFields() throws Exception {
        User userWithNulls = User.builder()
                .id(3L)
                .username("nulluser")
                .email(null)
                .firstName(null)
                .lastName(null)
                .password("password")
                .enabled(0)
                .role(users.get(0).getRole())
                .build();

        model.put("users", Arrays.asList(userWithNulls));

        assertDoesNotThrow(() -> {
            csvView.buildCsvDocument(model, request, response);
        });

        String csvContent = response.getContentAsString();
        assertNotNull(csvContent);
        assertTrue(csvContent.contains("nulluser"));
    }

    @Test
    void testCsvHeaderOrder() throws Exception {
        csvView.buildCsvDocument(model, request, response);

        String csvContent = response.getContentAsString();
        String[] lines = csvContent.split("\n");
        assertTrue(lines.length > 0);

        String headerLine = lines[0];
        String[] headers = headerLine.split(",");

        // Verify header order matches expected
        assertEquals("FirstName", headers[0].trim().replace("\"", ""));
        assertEquals("LastName", headers[1].trim().replace("\"", ""));
        assertEquals("Username", headers[2].trim().replace("\"", ""));
        assertEquals("Email", headers[3].trim().replace("\"", ""));
        assertEquals("Password", headers[4].trim().replace("\"", ""));
        assertEquals("Enabled", headers[5].trim().replace("\"", ""));
        assertEquals("Role_id", headers[6].trim().replace("\"", ""));
        assertEquals("Role_name", headers[7].trim().replace("\"", ""));
    }

    @Test
    void testBuildCsvDocumentWithMultipleUsers() throws Exception {
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

        csvView.buildCsvDocument(model, request, response);

        String csvContent = response.getContentAsString();
        assertNotNull(csvContent);

        // Verify all users are present
        assertTrue(csvContent.contains("Admin"));
        assertTrue(csvContent.contains("Regular"));
        assertTrue(csvContent.contains("Manager"));
    }

    @Test
    void testMethodIsOverridden() throws NoSuchMethodException {
        // Verify that buildCsvDocument method is properly overridden
        var method = CsvView.class.getDeclaredMethod("buildCsvDocument",
                Map.class, jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class);

        assertNotNull(method);
        assertFalse(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
    }

    @Test
    void testCsvContentStructure() throws Exception {
        csvView.buildCsvDocument(model, request, response);

        String csvContent = response.getContentAsString();
        String[] lines = csvContent.split("\n");

        // Should have header line + number of users
        assertTrue(lines.length >= users.size() + 1);

        // First line should be headers
        assertTrue(lines[0].contains("FirstName"));

        // Subsequent lines should contain user data
        for (int i = 1; i < lines.length && i <= users.size(); i++) {
            assertFalse(lines[i].trim().isEmpty());
        }
    }

    @Test
    void testResponseContentTypeHandling() throws Exception {
        // Note: Content type is set by parent AbstractCsvView class
        csvView.buildCsvDocument(model, request, response);

        // Verify the method completes without error
        assertNotNull(response.getContentAsString());
    }
}