package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(role)
                .build();
    }

    @Test
    public void testUserCreation() {
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
        assertEquals(1, user.getEnabled());
        assertNotNull(user.getRole());
    }

    @Test
    public void testUserBuilder() {
        User builtUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        assertNotNull(builtUser);
        assertEquals("newuser", builtUser.getUsername());
        assertEquals("new@example.com", builtUser.getEmail());
    }

    @Test
    public void testGetColumnCount() {
        int columnCount = user.getColumnCount();
        assertTrue(columnCount > 0);
    }

    @Test
    public void testGetRoleId() {
        assertEquals(1, user.getRole_id());
    }

    @Test
    public void testGetRoleName() {
        assertEquals("ROLE_USER", user.getRole_name());
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    public void testSetters() {
        user.setUsername("updateduser");
        user.setEmail("updated@example.com");
        user.setFirstName("UpdatedJohn");
        user.setLastName("UpdatedDoe");
        user.setPassword("newpassword");
        user.setEnabled(0);

        assertEquals("updateduser", user.getUsername());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("UpdatedJohn", user.getFirstName());
        assertEquals("UpdatedDoe", user.getLastName());
        assertEquals("newpassword", user.getPassword());
        assertEquals(0, user.getEnabled());
    }

    @Test
    public void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
    }

    @Test
    public void testAllArgsConstructor() {
        User constructedUser = new User(2L, "user2", "user2@example.com", "Alice", "Brown", "pass", 1, role);
        assertNotNull(constructedUser);
        assertEquals(2L, constructedUser.getId());
        assertEquals("user2", constructedUser.getUsername());
        assertEquals("user2@example.com", constructedUser.getEmail());
    }

    @Test
    public void testUserWithNullRole() {
        User userWithoutRole = User.builder()
                .username("noroleuser")
                .email("norole@example.com")
                .build();

        assertNull(userWithoutRole.getRole());
        assertThrows(NullPointerException.class, () -> userWithoutRole.getRole_id());
    }

    @Test
    public void testGetNameWithNullValues() {
        User userWithNulls = User.builder().build();
        String name = userWithNulls.getName();
        assertEquals("null null", name);
    }
}
