package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTest {

    private User user;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        user = new User();
        mockRole = mock(Role.class);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
    }

    @Test
    void testNoArgsConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getUsername());
        assertNull(newUser.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");

        User newUser = new User(1L, "testuser", "test@example.com",
                "John", "Doe", "password123", 1, role);

        assertNotNull(newUser);
        assertEquals(1L, newUser.getId());
        assertEquals("testuser", newUser.getUsername());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("password123", newUser.getPassword());
        assertEquals(1, newUser.getEnabled());
        assertEquals(role, newUser.getRole());
    }

    @Test
    void testBuilderPattern() {
        Role role = new Role();
        role.setId(2);
        role.setName("USER");

        User builtUser = User.builder()
                .id(2L)
                .username("jane.doe")
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("securepass")
                .enabled(1)
                .role(role)
                .build();

        assertNotNull(builtUser);
        assertEquals(2L, builtUser.getId());
        assertEquals("jane.doe", builtUser.getUsername());
        assertEquals("jane@example.com", builtUser.getEmail());
        assertEquals("Jane", builtUser.getFirstName());
        assertEquals("Doe", builtUser.getLastName());
        assertEquals("securepass", builtUser.getPassword());
        assertEquals(1, builtUser.getEnabled());
        assertEquals(role, builtUser.getRole());
    }

    @Test
    void testSetAndGetId() {
        Long expectedId = 1L;
        user.setId(expectedId);
        assertEquals(expectedId, user.getId());
    }

    @Test
    void testSetAndGetUsername() {
        String expectedUsername = "testuser";
        user.setUsername(expectedUsername);
        assertEquals(expectedUsername, user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        String expectedEmail = "test@example.com";
        user.setEmail(expectedEmail);
        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    void testSetAndGetFirstName() {
        String expectedFirstName = "John";
        user.setFirstName(expectedFirstName);
        assertEquals(expectedFirstName, user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String expectedLastName = "Doe";
        user.setLastName(expectedLastName);
        assertEquals(expectedLastName, user.getLastName());
    }

    @Test
    void testSetAndGetPassword() {
        String expectedPassword = "password123";
        user.setPassword(expectedPassword);
        assertEquals(expectedPassword, user.getPassword());
    }

    @Test
    void testSetAndGetEnabled() {
        int expectedEnabled = 1;
        user.setEnabled(expectedEnabled);
        assertEquals(expectedEnabled, user.getEnabled());
    }

    @Test
    void testSetAndGetRole() {
        Role expectedRole = new Role();
        expectedRole.setId(1);
        expectedRole.setName("ADMIN");

        user.setRole(expectedRole);
        assertEquals(expectedRole, user.getRole());
    }

    @Test
    void testGetName() {
        user.setFirstName("John");
        user.setLastName("Doe");

        String fullName = user.getName();
        assertEquals("John Doe", fullName);
    }

    @Test
    void testGetNameWithNullFirstName() {
        user.setFirstName(null);
        user.setLastName("Doe");

        String fullName = user.getName();
        assertEquals("null Doe", fullName);
    }

    @Test
    void testGetNameWithNullLastName() {
        user.setFirstName("John");
        user.setLastName(null);

        String fullName = user.getName();
        assertEquals("John null", fullName);
    }

    @Test
    void testGetNameWithBothNullNames() {
        user.setFirstName(null);
        user.setLastName(null);

        String fullName = user.getName();
        assertEquals("null null", fullName);
    }

    @Test
    void testGetNameWithEmptyNames() {
        user.setFirstName("");
        user.setLastName("");

        String fullName = user.getName();
        assertEquals(" ", fullName);
    }

    @Test
    void testGetRoleId() {
        when(mockRole.getId()).thenReturn(1);
        user.setRole(mockRole);

        int roleId = user.getRole_id();
        assertEquals(1, roleId);
    }

    @Test
    void testGetRoleIdWithNullRole() {
        user.setRole(null);

        assertThrows(NullPointerException.class, () -> {
            user.getRole_id();
        });
    }

    @Test
    void testGetRoleName() {
        when(mockRole.getName()).thenReturn("ADMIN");
        user.setRole(mockRole);

        String roleName = user.getRole_name();
        assertEquals("ADMIN", roleName);
    }

    @Test
    void testGetRoleNameWithNullRole() {
        user.setRole(null);

        assertThrows(NullPointerException.class, () -> {
            user.getRole_name();
        });
    }

    @Test
    void testGetColumnCount() {
        int columnCount = user.getColumnCount();
        assertTrue(columnCount > 0);
        assertEquals(User.class.getDeclaredFields().length, columnCount);
    }

    @Test
    void testSetNullValues() {
        user.setUsername(null);
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setPassword(null);
        user.setRole(null);

        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    void testSetEmptyStringValues() {
        user.setUsername("");
        user.setEmail("");
        user.setFirstName("");
        user.setLastName("");
        user.setPassword("");

        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getPassword());
    }

    @Test
    void testEntityAnnotation() {
        assertTrue(User.class.isAnnotationPresent(Entity.class));
        Entity entity = User.class.getAnnotation(Entity.class);
        assertEquals("users", entity.name());
    }

    @Test
    void testDataAnnotation() {
        assertTrue(User.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testBuilderAnnotation() {
        assertTrue(User.class.isAnnotationPresent(Builder.class));
    }

    @Test
    void testNoArgsConstructorAnnotation() {
        assertTrue(User.class.isAnnotationPresent(NoArgsConstructor.class));
    }

    @Test
    void testAllArgsConstructorAnnotation() {
        assertTrue(User.class.isAnnotationPresent(AllArgsConstructor.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        var idField = User.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());
    }

    @Test
    void testUsernameFieldAnnotations() throws NoSuchFieldException {
        var usernameField = User.class.getDeclaredField("username");
        assertTrue(usernameField.isAnnotationPresent(Column.class));

        Column column = usernameField.getAnnotation(Column.class);
        assertFalse(column.nullable());
        assertTrue(column.unique());
    }

    @Test
    void testEmailFieldAnnotations() throws NoSuchFieldException {
        var emailField = User.class.getDeclaredField("email");
        assertTrue(emailField.isAnnotationPresent(Column.class));
        assertTrue(emailField.isAnnotationPresent(Email.class));
        assertTrue(emailField.isAnnotationPresent(NotEmpty.class));

        Column column = emailField.getAnnotation(Column.class);
        assertEquals("email", column.name());
        assertFalse(column.nullable());
        assertTrue(column.unique());

        Email email = emailField.getAnnotation(Email.class);
        assertEquals("Please provide a valid e-mail", email.message());

        NotEmpty notEmpty = emailField.getAnnotation(NotEmpty.class);
        assertEquals("Please provide an e-mail", notEmpty.message());
    }

    @Test
    void testRoleFieldAnnotations() throws NoSuchFieldException {
        var roleField = User.class.getDeclaredField("role");
        assertTrue(roleField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void testUserToString() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("1"));
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("test@example.com"));
        assertTrue(userString.contains("John"));
        assertTrue(userString.contains("Doe"));
    }

    @Test
    void testUserEquals() {
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");

        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(role)
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(role)
                .build();

        assertEquals(user1, user2);
    }

    @Test
    void testUserNotEquals() {
        User user1 = User.builder()
                .id(1L)
                .username("testuser1")
                .email("test1@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("testuser2")
                .email("test2@example.com")
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    void testUserHashCode() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEnabledValues() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());

        user.setEnabled(1);
        assertEquals(1, user.getEnabled());

        user.setEnabled(-1);
        assertEquals(-1, user.getEnabled());
    }

    @Test
    void testUserWithCompleteData() {
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");

        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setPassword("adminpass");
        user.setEnabled(1);
        user.setRole(role);

        assertEquals(1L, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("Admin", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("adminpass", user.getPassword());
        assertEquals(1, user.getEnabled());
        assertEquals(role, user.getRole());
        assertEquals("Admin User", user.getName());
        assertEquals(1, user.getRole_id());
        assertEquals("ADMIN", user.getRole_name());
    }
}