package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        categories = new HashSet<>();

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Regular");

        categories.add(category1);
        categories.add(category2);
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
    }

    @Test
    void testNoArgsConstructor() {
        Customer newCustomer = new Customer();
        assertNotNull(newCustomer);
        assertNull(newCustomer.getId());
        assertNull(newCustomer.getName());
        assertNull(newCustomer.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        Customer newCustomer = new Customer(1L, "John Doe", "john@example.com", 1234567890,
                categories, "John", "Doe", "New York", "123 Main St", 1);

        assertNotNull(newCustomer);
        assertEquals(1L, newCustomer.getId());
        assertEquals("John Doe", newCustomer.getName());
        assertEquals("john@example.com", newCustomer.getEmail());
        assertEquals(1234567890, newCustomer.getPhone());
        assertEquals(categories, newCustomer.getCategories());
        assertEquals("John", newCustomer.getFirstName());
        assertEquals("Doe", newCustomer.getLastName());
        assertEquals("New York", newCustomer.getCity());
        assertEquals("123 Main St", newCustomer.getAddress());
        assertEquals(1, newCustomer.getEnabled());
    }

    @Test
    void testBuilderPattern() {
        Customer builtCustomer = Customer.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@example.com")
                .phone(987654321)
                .categories(categories)
                .firstName("Jane")
                .lastName("Doe")
                .city("Boston")
                .address("456 Oak Ave")
                .enabled(1)
                .build();

        assertNotNull(builtCustomer);
        assertEquals(1L, builtCustomer.getId());
        assertEquals("Jane Doe", builtCustomer.getName());
        assertEquals("jane@example.com", builtCustomer.getEmail());
        assertEquals(987654321, builtCustomer.getPhone());
        assertEquals(categories, builtCustomer.getCategories());
        assertEquals("Jane", builtCustomer.getFirstName());
        assertEquals("Doe", builtCustomer.getLastName());
        assertEquals("Boston", builtCustomer.getCity());
        assertEquals("456 Oak Ave", builtCustomer.getAddress());
        assertEquals(1, builtCustomer.getEnabled());
    }

    @Test
    void testSetAndGetId() {
        Long expectedId = 1L;
        customer.setId(expectedId);
        assertEquals(expectedId, customer.getId());
    }

    @Test
    void testSetAndGetName() {
        String expectedName = "John Smith";
        customer.setName(expectedName);
        assertEquals(expectedName, customer.getName());
    }

    @Test
    void testSetAndGetEmail() {
        String expectedEmail = "john.smith@example.com";
        customer.setEmail(expectedEmail);
        assertEquals(expectedEmail, customer.getEmail());
    }

    @Test
    void testSetAndGetPhone() {
        int expectedPhone = 1234567890;
        customer.setPhone(expectedPhone);
        assertEquals(expectedPhone, customer.getPhone());
    }

    @Test
    void testSetAndGetCategories() {
        customer.setCategories(categories);
        assertEquals(categories, customer.getCategories());
    }

    @Test
    void testSetAndGetFirstName() {
        String expectedFirstName = "John";
        customer.setFirstName(expectedFirstName);
        assertEquals(expectedFirstName, customer.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String expectedLastName = "Smith";
        customer.setLastName(expectedLastName);
        assertEquals(expectedLastName, customer.getLastName());
    }

    @Test
    void testSetAndGetCity() {
        String expectedCity = "New York";
        customer.setCity(expectedCity);
        assertEquals(expectedCity, customer.getCity());
    }

    @Test
    void testSetAndGetAddress() {
        String expectedAddress = "123 Main Street";
        customer.setAddress(expectedAddress);
        assertEquals(expectedAddress, customer.getAddress());
    }

    @Test
    void testSetAndGetEnabled() {
        int expectedEnabled = 1;
        customer.setEnabled(expectedEnabled);
        assertEquals(expectedEnabled, customer.getEnabled());
    }

    @Test
    void testSetNullValues() {
        customer.setName(null);
        customer.setEmail(null);
        customer.setCategories(null);
        customer.setFirstName(null);
        customer.setLastName(null);
        customer.setCity(null);
        customer.setAddress(null);

        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNull(customer.getCategories());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getCity());
        assertNull(customer.getAddress());
    }

    @Test
    void testSetEmptyStringValues() {
        customer.setName("");
        customer.setEmail("");
        customer.setFirstName("");
        customer.setLastName("");
        customer.setCity("");
        customer.setAddress("");

        assertEquals("", customer.getName());
        assertEquals("", customer.getEmail());
        assertEquals("", customer.getFirstName());
        assertEquals("", customer.getLastName());
        assertEquals("", customer.getCity());
        assertEquals("", customer.getAddress());
    }

    @Test
    void testEntityAnnotation() {
        assertTrue(Customer.class.isAnnotationPresent(Entity.class));
    }

    @Test
    void testDataAnnotation() {
        assertTrue(Customer.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testBuilderAnnotation() {
        assertTrue(Customer.class.isAnnotationPresent(Builder.class));
    }

    @Test
    void testNoArgsConstructorAnnotation() {
        assertTrue(Customer.class.isAnnotationPresent(NoArgsConstructor.class));
    }

    @Test
    void testAllArgsConstructorAnnotation() {
        assertTrue(Customer.class.isAnnotationPresent(AllArgsConstructor.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        var idField = Customer.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());
    }

    @Test
    void testNameFieldAnnotations() throws NoSuchFieldException {
        var nameField = Customer.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(Column.class));
        assertTrue(nameField.isAnnotationPresent(Size.class));

        Column column = nameField.getAnnotation(Column.class);
        assertFalse(column.nullable());
        assertTrue(column.unique());

        Size size = nameField.getAnnotation(Size.class);
        assertEquals(2, size.min());
    }

    @Test
    void testEmailFieldAnnotations() throws NoSuchFieldException {
        var emailField = Customer.class.getDeclaredField("email");
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
    void testPhoneFieldAnnotations() throws NoSuchFieldException {
        var phoneField = Customer.class.getDeclaredField("phone");
        assertTrue(phoneField.isAnnotationPresent(Digits.class));

        Digits digits = phoneField.getAnnotation(Digits.class);
        assertEquals(0, digits.fraction());
        assertEquals(20, digits.integer());
    }

    @Test
    void testCategoriesFieldAnnotations() throws NoSuchFieldException {
        var categoriesField = Customer.class.getDeclaredField("categories");
        assertTrue(categoriesField.isAnnotationPresent(ManyToMany.class));
        assertTrue(categoriesField.isAnnotationPresent(JoinTable.class));

        ManyToMany manyToMany = categoriesField.getAnnotation(ManyToMany.class);
        assertEquals(CascadeType.ALL, manyToMany.cascade()[0]);
        assertEquals(FetchType.EAGER, manyToMany.fetch());

        JoinTable joinTable = categoriesField.getAnnotation(JoinTable.class);
        assertEquals("customer_category", joinTable.name());
        assertEquals("customer_id", joinTable.joinColumns()[0].name());
        assertEquals("category_id", joinTable.inverseJoinColumns()[0].name());
    }

    @Test
    void testCustomerToString() {
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        String customerString = customer.toString();
        assertNotNull(customerString);
        assertTrue(customerString.contains("1"));
        assertTrue(customerString.contains("John Doe"));
        assertTrue(customerString.contains("john@example.com"));
    }

    @Test
    void testCustomerEquals() {
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        Customer customer2 = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        assertEquals(customer1, customer2);
    }

    @Test
    void testCustomerNotEquals() {
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        assertNotEquals(customer1, customer2);
    }

    @Test
    void testCustomerHashCode() {
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        int hashCode1 = customer.hashCode();
        int hashCode2 = customer.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testAddCategoriesToCustomer() {
        customer.setCategories(categories);
        assertEquals(2, customer.getCategories().size());
        assertTrue(customer.getCategories().stream().anyMatch(cat -> "VIP".equals(cat.getName())));
        assertTrue(customer.getCategories().stream().anyMatch(cat -> "Regular".equals(cat.getName())));
    }

    @Test
    void testCustomerWithEmptyCategories() {
        Set<Category> emptyCategories = new HashSet<>();
        customer.setCategories(emptyCategories);
        assertNotNull(customer.getCategories());
        assertTrue(customer.getCategories().isEmpty());
    }

    @Test
    void testCustomerEnabledValues() {
        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());

        customer.setEnabled(1);
        assertEquals(1, customer.getEnabled());

        customer.setEnabled(-1);
        assertEquals(-1, customer.getEnabled());
    }
}