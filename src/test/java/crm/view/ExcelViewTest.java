package crm.view;

import crm.entity.Role;
import crm.entity.User;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewTest {

    private ExcelView excelView;
    private Map<String, Object> model;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Workbook workbook;
    private List<User> users;

    @BeforeEach
    void setUp() {
        excelView = new ExcelView();
        model = new HashMap<>();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        workbook = new XSSFWorkbook();

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
    void testExcelViewCreation() {
        assertNotNull(excelView);
    }

    @Test
    void testExtendsAbstractXlsView() {
        assertTrue(excelView instanceof AbstractXlsView);
    }

    @Test
    void testBuildExcelDocument() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        // Verify content disposition header is set
        assertEquals("attachment; filename=\"my-xls-file.xls\"",
                    response.getHeader("Content-Disposition"));

        // Verify workbook has a sheet
        assertEquals(1, workbook.getNumberOfSheets());
        assertEquals("User Detail", workbook.getSheetName(0));

        var sheet = workbook.getSheetAt(0);

        // Verify sheet has data
        assertTrue(sheet.getPhysicalNumberOfRows() > 0);

        // Verify header row
        var headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals("FirstName", headerRow.getCell(0).getStringCellValue());
        assertEquals("LastName", headerRow.getCell(1).getStringCellValue());
        assertEquals("Username", headerRow.getCell(2).getStringCellValue());
        assertEquals("Email", headerRow.getCell(3).getStringCellValue());
        assertEquals("Password", headerRow.getCell(4).getStringCellValue());
        assertEquals("Enabled", headerRow.getCell(5).getStringCellValue());
        assertEquals("Role_id", headerRow.getCell(6).getStringCellValue());
        assertEquals("Role_name", headerRow.getCell(7).getStringCellValue());

        // Verify user data rows
        var firstUserRow = sheet.getRow(1);
        assertNotNull(firstUserRow);
        assertEquals("Admin", firstUserRow.getCell(0).getStringCellValue());
        assertEquals("User", firstUserRow.getCell(1).getStringCellValue());
        assertEquals("admin", firstUserRow.getCell(2).getStringCellValue());
        assertEquals("admin@example.com", firstUserRow.getCell(3).getStringCellValue());

        var secondUserRow = sheet.getRow(2);
        assertNotNull(secondUserRow);
        assertEquals("Regular", secondUserRow.getCell(0).getStringCellValue());
        assertEquals("User", secondUserRow.getCell(1).getStringCellValue());
        assertEquals("user1", secondUserRow.getCell(2).getStringCellValue());
        assertEquals("user1@example.com", secondUserRow.getCell(3).getStringCellValue());
    }

    @Test
    void testBuildExcelDocumentWithEmptyUserList() throws Exception {
        model.put("users", Arrays.asList());

        assertDoesNotThrow(() -> {
            excelView.buildExcelDocument(model, workbook, request, response);
        });

        // Verify workbook still has sheet with headers
        assertEquals(1, workbook.getNumberOfSheets());
        var sheet = workbook.getSheetAt(0);
        var headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals("FirstName", headerRow.getCell(0).getStringCellValue());
    }

    @Test
    void testBuildExcelDocumentWithNullUserList() {
        model.put("users", null);

        assertThrows(NullPointerException.class, () -> {
            excelView.buildExcelDocument(model, workbook, request, response);
        });
    }

    @Test
    void testBuildExcelDocumentWithSingleUser() throws Exception {
        model.put("users", Arrays.asList(users.get(0)));

        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);

        // Should have header row + 1 user row
        assertEquals(2, sheet.getPhysicalNumberOfRows());

        var userRow = sheet.getRow(1);
        assertNotNull(userRow);
        assertEquals("Admin", userRow.getCell(0).getStringCellValue());
        assertEquals("admin@example.com", userRow.getCell(3).getStringCellValue());
    }

    @Test
    void testBuildExcelDocumentSetsCorrectHeaders() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        assertEquals("attachment; filename=\"my-xls-file.xls\"",
                    response.getHeader("Content-Disposition"));
    }

    @Test
    void testSheetConfiguration() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);

        // Verify sheet name
        assertEquals("User Detail", sheet.getSheetName());

        // Verify default column width is set
        assertEquals(30, sheet.getDefaultColumnWidth());
    }

    @Test
    void testHeaderRowStyling() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);
        var headerRow = sheet.getRow(0);

        // Verify all header cells have style applied
        for (int i = 0; i < 8; i++) {
            var cell = headerRow.getCell(i);
            assertNotNull(cell);
            assertNotNull(cell.getCellStyle());
        }
    }

    @Test
    void testUserDataRows() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);

        // Verify correct number of rows (header + users)
        assertEquals(users.size() + 1, sheet.getPhysicalNumberOfRows());

        // Verify each user row has correct data
        for (int i = 0; i < users.size(); i++) {
            var userRow = sheet.getRow(i + 1); // +1 for header row
            var user = users.get(i);

            assertEquals(user.getFirstName(), userRow.getCell(0).getStringCellValue());
            assertEquals(user.getLastName(), userRow.getCell(1).getStringCellValue());
            assertEquals(user.getUsername(), userRow.getCell(2).getStringCellValue());
            assertEquals(user.getEmail(), userRow.getCell(3).getStringCellValue());
            assertEquals(user.getPassword(), userRow.getCell(4).getStringCellValue());
            assertEquals(user.getEnabled(), (int) userRow.getCell(5).getNumericCellValue());
            assertEquals(user.getRole().getId(), (int) userRow.getCell(6).getNumericCellValue());
            assertEquals(user.getRole().getName(), userRow.getCell(7).getStringCellValue());
        }
    }

    @Test
    void testBuildExcelDocumentWithUsersHavingNullFields() throws Exception {
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

        assertDoesNotThrow(() -> {
            excelView.buildExcelDocument(model, workbook, request, response);
        });

        var sheet = workbook.getSheetAt(0);
        var userRow = sheet.getRow(1);

        // Verify null fields are handled (should be empty cells or blank values)
        assertEquals("", userRow.getCell(0).getStringCellValue());
        assertEquals("", userRow.getCell(1).getStringCellValue());
        assertEquals("nulluser", userRow.getCell(2).getStringCellValue());
    }

    @Test
    void testWorkbookNotModifiedExternally() throws Exception {
        int initialSheets = workbook.getNumberOfSheets();

        excelView.buildExcelDocument(model, workbook, request, response);

        // Should have added exactly one sheet
        assertEquals(initialSheets + 1, workbook.getNumberOfSheets());
    }

    @Test
    void testMethodIsOverridden() throws NoSuchMethodException {
        // Verify that buildExcelDocument method is properly overridden
        var method = ExcelView.class.getDeclaredMethod("buildExcelDocument",
                Map.class, Workbook.class, jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class);

        assertNotNull(method);
        assertFalse(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
    }

    @Test
    void testBuildExcelDocumentWithMultipleUsers() throws Exception {
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

        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);

        // Verify correct number of rows (header + 3 users)
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        // Verify third user data
        var thirdUserRow = sheet.getRow(3);
        assertEquals("Manager", thirdUserRow.getCell(0).getStringCellValue());
        assertEquals("Person", thirdUserRow.getCell(1).getStringCellValue());
        assertEquals("manager", thirdUserRow.getCell(2).getStringCellValue());
    }

    @Test
    void testAllHeaderCellsArePresent() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);
        var headerRow = sheet.getRow(0);

        // Verify all 8 header cells exist and have correct values
        String[] expectedHeaders = {"FirstName", "LastName", "Username", "Email",
                                   "Password", "Enabled", "Role_id", "Role_name"};

        for (int i = 0; i < expectedHeaders.length; i++) {
            var cell = headerRow.getCell(i);
            assertNotNull(cell);
            assertEquals(expectedHeaders[i], cell.getStringCellValue());
        }
    }

    @Test
    void testRowCounterIncrementsCorrectly() throws Exception {
        excelView.buildExcelDocument(model, workbook, request, response);

        var sheet = workbook.getSheetAt(0);

        // Header row should be at index 0
        assertNotNull(sheet.getRow(0));

        // User rows should start at index 1 and continue sequentially
        for (int i = 0; i < users.size(); i++) {
            assertNotNull(sheet.getRow(i + 1));
        }

        // No additional rows beyond users + header
        assertNull(sheet.getRow(users.size() + 1));
    }
}