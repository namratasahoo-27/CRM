package crm.utils;

import crm.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WriteCsvToResponseTest {

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private List<Customer> customers;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        customer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("+1234567890")
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .categories(new HashSet<>())
                .build();

        customers = new ArrayList<>();
        customers.add(customer);
    }

    @Test
    public void testWriteCustomers() {
        WriteCsvToResponse.writeCustomers(printWriter, customers);
        printWriter.flush();

        String output = stringWriter.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }

    @Test
    public void testWriteCustomer() {
        WriteCsvToResponse.writeCustomer(printWriter, customer);
        printWriter.flush();

        String output = stringWriter.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }

    @Test
    public void testWriteCustomersWithMultiple() {
        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Customer 2")
                .email("customer2@example.com")
                .phone("+9876543210")
                .firstName("Jane")
                .lastName("Smith")
                .city("Los Angeles")
                .address("456 Oak Ave")
                .enabled(1)
                .categories(new HashSet<>())
                .build();

        customers.add(customer2);

        WriteCsvToResponse.writeCustomers(printWriter, customers);
        printWriter.flush();

        String output = stringWriter.toString();
        assertNotNull(output);
        assertTrue(output.length() > 0);
    }

    @Test
    public void testWriteCustomersWithEmptyList() {
        List<Customer> emptyList = new ArrayList<>();
        WriteCsvToResponse.writeCustomers(printWriter, emptyList);
        printWriter.flush();

        String output = stringWriter.toString();
        assertNotNull(output);
    }

    @Test
    public void testWriteCustomerWithNullFields() {
        Customer nullCustomer = Customer.builder()
                .id(3L)
                .enabled(1)
                .categories(new HashSet<>())
                .build();

        WriteCsvToResponse.writeCustomer(printWriter, nullCustomer);
        printWriter.flush();

        String output = stringWriter.toString();
        assertNotNull(output);
    }
}
