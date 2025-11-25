package crm.service;

import crm.entity.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testUserServiceInterface() {
        assertNotNull(UserService.class);
    }

    @Test
    public void testFindByUsernameMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("findByUsername", String.class));
    }

    @Test
    public void testListAllUsersMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("listAllUsers"));
    }

    @Test
    public void testShowUserMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("showUser", Long.class));
    }

    @Test
    public void testSaveUserMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("saveUser", User.class));
    }

    @Test
    public void testEditUserMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("editUser", User.class));
    }

    @Test
    public void testDeleteUserMethodExists() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("deleteUser", User.class));
    }

    @Test
    public void testUserServiceIsInterface() {
        assertTrue(UserService.class.isInterface());
    }
}
