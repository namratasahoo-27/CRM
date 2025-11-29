package crm.utils;

import crm.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WriteCsvToResponseTest {

    private Customer testCustomer;
    private List<Customer> testCustomers;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone(123456);
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("Customer");
        testCustomer.setCity("Test City");
        testCustomer.setAddress("123 Test St");
        testCustomer.setEnabled(1);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Customer Two");
        customer2.setEmail("customer2@example.com");
        customer2.setPhone(987654);
        customer2.setFirstName("Customer");
        customer2.setLastName("Two");
        customer2.setCity("Test City 2");
        customer2.setAddress("456 Test Ave");
        customer2.setEnabled(0);

        testCustomers = Arrays.asList(testCustomer, customer2);
    }

    @Test
    void testWriteCustomersWithValidList() {
        // Act
        WriteCsvToResponse.writeCustomers(printWriter, testCustomers);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();
        assertNotNull(csvOutput);
        assertFalse(csvOutput.isEmpty());
    }

    @Test
    void testWriteCustomersWithEmptyList() {
        // Arrange
        List<Customer> emptyList = new ArrayList<>();

        // Act
        WriteCsvToResponse.writeCustomers(printWriter, emptyList);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();
        assertNotNull(csvOutput);
    }

    @Test
    void testWriteCustomersWithNullList() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, null);
        });
    }

    @Test
    void testWriteCustomersWithNullPrintWriter() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(null, testCustomers);
        });
    }

    @Test
    void testWriteCustomerWithValidCustomer() {
        // Act
        WriteCsvToResponse.writeCustomer(printWriter, testCustomer);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();
        assertNotNull(csvOutput);
        assertFalse(csvOutput.isEmpty());
    }

    @Test
    void testWriteCustomerWithNullCustomer() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, null);
        });
    }

    @Test
    void testWriteCustomerWithNullPrintWriter() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(null, testCustomer);
        });
    }

    @Test
    void testWriteCustomersColumnMapping() {
        // Act
        WriteCsvToResponse.writeCustomers(printWriter, testCustomers);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();

        // Verify that the CSV contains some expected values
        assertTrue(csvOutput.contains("Test Customer") || csvOutput.contains("Customer Two"));
    }

    @Test
    void testWriteCustomerColumnMapping() {
        // Act
        WriteCsvToResponse.writeCustomer(printWriter, testCustomer);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();

        // Verify that the CSV contains expected values
        assertTrue(csvOutput.contains("Test Customer"));
    }

    @Test
    void testWriteCustomersWithSingleCustomer() {
        // Arrange
        List<Customer> singleCustomerList = Arrays.asList(testCustomer);

        // Act
        WriteCsvToResponse.writeCustomers(printWriter, singleCustomerList);

        // Assert
        printWriter.flush();
        String csvOutput = stringWriter.toString();
        assertNotNull(csvOutput);
        assertFalse(csvOutput.isEmpty());
    }

    @Test
    void testWriteCustomersWithCustomerHavingNullFields() {
        // Arrange
        Customer customerWithNulls = new Customer();
        customerWithNulls.setId(3L);
        customerWithNulls.setName(null);
        customerWithNulls.setEmail(null);
        List<Customer> customersWithNulls = Arrays.asList(customerWithNulls);

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, customersWithNulls);
        });
    }

    @Test
    void testWriteCustomerWithCustomerHavingNullFields() {
        // Arrange
        Customer customerWithNulls = new Customer();
        customerWithNulls.setId(3L);
        customerWithNulls.setName(null);
        customerWithNulls.setEmail(null);

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, customerWithNulls);
        });
    }
}