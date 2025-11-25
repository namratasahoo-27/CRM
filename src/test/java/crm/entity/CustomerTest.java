package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private Customer customer;
    private Set<Category> categories;

    @BeforeEach
    public void setUp() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Technology");

        categories = new HashSet<>();
        categories.add(category1);

        customer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("customer@test.com")
                .phone("+1234567890")
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .categories(categories)
                .build();
    }

    @Test
    public void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals(1L, customer.getId());
        assertEquals("Test Customer", customer.getName());
        assertEquals("customer@test.com", customer.getEmail());
        assertEquals("+1234567890", customer.getPhone());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("New York", customer.getCity());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals(1, customer.getEnabled());
        assertNotNull(customer.getCategories());
        assertEquals(1, customer.getCategories().size());
    }

    @Test
    public void testCustomerBuilder() {
        Customer builtCustomer = Customer.builder()
                .name("New Customer")
                .email("new@test.com")
                .phone("+9876543210")
                .build();

        assertNotNull(builtCustomer);
        assertEquals("New Customer", builtCustomer.getName());
        assertEquals("new@test.com", builtCustomer.getEmail());
        assertEquals("+9876543210", builtCustomer.getPhone());
    }

    @Test
    public void testSetters() {
        customer.setName("Updated Customer");
        customer.setEmail("updated@test.com");
        customer.setPhone("+1111111111");
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setCity("Los Angeles");
        customer.setAddress("456 Oak Ave");
        customer.setEnabled(0);

        assertEquals("Updated Customer", customer.getName());
        assertEquals("updated@test.com", customer.getEmail());
        assertEquals("+1111111111", customer.getPhone());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("Los Angeles", customer.getCity());
        assertEquals("456 Oak Ave", customer.getAddress());
        assertEquals(0, customer.getEnabled());
    }

    @Test
    public void testNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNotNull(emptyCustomer);
        assertNull(emptyCustomer.getId());
        assertNull(emptyCustomer.getName());
    }

    @Test
    public void testAllArgsConstructor() {
        Set<Category> cats = new HashSet<>();
        Customer constructedCustomer = new Customer(
                2L,
                "Constructor Customer",
                "constructor@test.com",
                "+2222222222",
                cats,
                "Alice",
                "Brown",
                "Chicago",
                "789 Elm St",
                1
        );

        assertNotNull(constructedCustomer);
        assertEquals(2L, constructedCustomer.getId());
        assertEquals("Constructor Customer", constructedCustomer.getName());
        assertEquals("constructor@test.com", constructedCustomer.getEmail());
    }

    @Test
    public void testCustomerWithNullValues() {
        Customer nullCustomer = Customer.builder().build();
        assertNull(nullCustomer.getName());
        assertNull(nullCustomer.getEmail());
        assertNull(nullCustomer.getPhone());
    }

    @Test
    public void testCategoriesModification() {
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Finance");

        customer.getCategories().add(newCategory);
        assertEquals(2, customer.getCategories().size());
    }

    @Test
    public void testCustomerWithMultipleCategories() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Tech");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Finance");

        Set<Category> multiCategories = new HashSet<>();
        multiCategories.add(cat1);
        multiCategories.add(cat2);

        customer.setCategories(multiCategories);
        assertEquals(2, customer.getCategories().size());
    }

    @Test
    public void testPhoneValidation() {
        String validPhone = "+1234567890";
        customer.setPhone(validPhone);
        assertEquals(validPhone, customer.getPhone());
        assertTrue(customer.getPhone().matches("^\\+?[0-9]{10,15}$"));
    }

    @Test
    public void testEnabledFlag() {
        customer.setEnabled(1);
        assertEquals(1, customer.getEnabled());

        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());
    }
}
