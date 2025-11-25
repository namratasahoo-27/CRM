package crm.service;

import crm.entity.Role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceTest {

    @Test
    public void testRoleServiceInterface() {
        assertNotNull(RoleService.class);
    }

    @Test
    public void testListAllRolesMethodExists() throws NoSuchMethodException {
        assertNotNull(RoleService.class.getMethod("listAllRoles"));
    }

    @Test
    public void testListAllRolesReturnType() throws NoSuchMethodException {
        assertEquals(Iterable.class, RoleService.class.getMethod("listAllRoles").getReturnType());
    }

    @Test
    public void testRoleServiceIsInterface() {
        assertTrue(RoleService.class.isInterface());
    }
}
