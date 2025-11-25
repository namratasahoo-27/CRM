package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
    }

    @Test
    public void testRoleCreation() {
        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    public void testSetters() {
        role.setId(2);
        role.setName("ROLE_ADMIN");

        assertEquals(2, role.getId());
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    public void testGetters() {
        assertEquals(1, role.getId());
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    public void testRoleWithNullValues() {
        Role nullRole = new Role();
        assertEquals(0, nullRole.getId());
        assertNull(nullRole.getName());
    }

    @Test
    public void testRoleEquality() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(1);
        role2.setName("ROLE_USER");

        assertEquals(role1.getId(), role2.getId());
        assertEquals(role1.getName(), role2.getName());
    }

    @Test
    public void testRoleInequality() {
        Role role1 = new Role();
        role1.setId(1);

        Role role2 = new Role();
        role2.setId(2);

        assertNotEquals(role1.getId(), role2.getId());
    }

    @Test
    public void testDifferentRoleNames() {
        role.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", role.getName());

        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());

        role.setName("ROLE_OWNER");
        assertEquals("ROLE_OWNER", role.getName());
    }
}
