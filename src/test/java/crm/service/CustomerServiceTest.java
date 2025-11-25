package crm.service;

import crm.entity.Category;
import crm.entity.Customer;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {

    @Test
    public void testCustomerServiceInterface() {
        assertNotNull(CustomerService.class);
    }

    @Test
    public void testGetMaxIdMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("getMaxId"));
    }

    @Test
    public void testListAllCustomersMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("listAllCustomers"));
    }

    @Test
    public void testShowCustomerMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("showCustomer", Long.class));
    }

    @Test
    public void testFindAllByEnabledTrueMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findAllByEnabledTrue"));
    }

    @Test
    public void testFindAllByEnabledFalseMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findAllByEnabledFalse"));
    }

    @Test
    public void testFindByEmailMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findByEmail", String.class));
    }

    @Test
    public void testFindByPhoneMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findByPhone", String.class));
    }

    @Test
    public void testFindByCategoriesMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findByCategories", Set.class));
    }

    @Test
    public void testSaveCustomerMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("saveCustomer", Customer.class));
    }

    @Test
    public void testCustomerServiceIsInterface() {
        assertTrue(CustomerService.class.isInterface());
    }

    @Test
    public void testFindByCityMethodExists() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("findByCity", String.class));
    }
}
