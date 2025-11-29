package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    private Contract contract;
    private Customer mockCustomer;
    private User mockUser;

    @BeforeEach
    void setUp() {
        contract = new Contract();

        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setName("Test Customer");
        mockCustomer.setEmail("customer@example.com");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("user@example.com");
    }

    @Test
    void testContractCreation() {
        assertNotNull(contract);
    }

    @Test
    void testNoArgsConstructor() {
        Contract newContract = new Contract();
        assertNotNull(newContract);
        assertNull(newContract.getId());
        assertNull(newContract.getName());
        assertNull(newContract.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        BigDecimal value = new BigDecimal("10000.00");

        Contract newContract = new Contract(1L, "Test Contract", "Contract content",
                value, beginDate, endDate, Status.IMPLEMENTED, mockCustomer, mockUser);

        assertNotNull(newContract);
        assertEquals(1L, newContract.getId());
        assertEquals("Test Contract", newContract.getName());
        assertEquals("Contract content", newContract.getContent());
        assertEquals(value, newContract.getValue());
        assertEquals(beginDate, newContract.getBeginDate());
        assertEquals(endDate, newContract.getEndDate());
        assertEquals(Status.IMPLEMENTED, newContract.getStatus());
        assertEquals(mockCustomer, newContract.getCustomer());
        assertEquals(mockUser, newContract.getUser());
    }

    @Test
    void testBuilderPattern() {
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        BigDecimal value = new BigDecimal("15000.00");

        Contract builtContract = Contract.builder()
                .id(2L)
                .name("Built Contract")
                .content("Built contract content")
                .value(value)
                .beginDate(beginDate)
                .endDate(endDate)
                .status(Status.NEGOTIATED)
                .customer(mockCustomer)
                .user(mockUser)
                .build();

        assertNotNull(builtContract);
        assertEquals(2L, builtContract.getId());
        assertEquals("Built Contract", builtContract.getName());
        assertEquals("Built contract content", builtContract.getContent());
        assertEquals(value, builtContract.getValue());
        assertEquals(beginDate, builtContract.getBeginDate());
        assertEquals(endDate, builtContract.getEndDate());
        assertEquals(Status.NEGOTIATED, builtContract.getStatus());
        assertEquals(mockCustomer, builtContract.getCustomer());
        assertEquals(mockUser, builtContract.getUser());
    }

    @Test
    void testSetAndGetId() {
        Long expectedId = 1L;
        contract.setId(expectedId);
        assertEquals(expectedId, contract.getId());
    }

    @Test
    void testSetAndGetName() {
        String expectedName = "Service Contract";
        contract.setName(expectedName);
        assertEquals(expectedName, contract.getName());
    }

    @Test
    void testSetAndGetContent() {
        String expectedContent = "This is the contract content";
        contract.setContent(expectedContent);
        assertEquals(expectedContent, contract.getContent());
    }

    @Test
    void testSetAndGetValue() {
        BigDecimal expectedValue = new BigDecimal("25000.50");
        contract.setValue(expectedValue);
        assertEquals(expectedValue, contract.getValue());
    }

    @Test
    void testSetAndGetBeginDate() {
        LocalDate expectedDate = LocalDate.of(2023, 6, 1);
        contract.setBeginDate(expectedDate);
        assertEquals(expectedDate, contract.getBeginDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate expectedDate = LocalDate.of(2024, 5, 31);
        contract.setEndDate(expectedDate);
        assertEquals(expectedDate, contract.getEndDate());
    }

    @Test
    void testSetAndGetStatus() {
        Status expectedStatus = Status.DONE;
        contract.setStatus(expectedStatus);
        assertEquals(expectedStatus, contract.getStatus());
    }

    @Test
    void testSetAndGetCustomer() {
        contract.setCustomer(mockCustomer);
        assertEquals(mockCustomer, contract.getCustomer());
    }

    @Test
    void testSetAndGetUser() {
        contract.setUser(mockUser);
        assertEquals(mockUser, contract.getUser());
    }

    @Test
    void testSetNullValues() {
        contract.setName(null);
        contract.setContent(null);
        contract.setValue(null);
        contract.setBeginDate(null);
        contract.setEndDate(null);
        contract.setStatus(null);
        contract.setCustomer(null);
        contract.setUser(null);

        assertNull(contract.getName());
        assertNull(contract.getContent());
        assertNull(contract.getValue());
        assertNull(contract.getBeginDate());
        assertNull(contract.getEndDate());
        assertNull(contract.getStatus());
        assertNull(contract.getCustomer());
        assertNull(contract.getUser());
    }

    @Test
    void testSetEmptyStringValues() {
        contract.setName("");
        contract.setContent("");

        assertEquals("", contract.getName());
        assertEquals("", contract.getContent());
    }

    @Test
    void testBigDecimalValueOperations() {
        BigDecimal value1 = new BigDecimal("1000.00");
        BigDecimal value2 = new BigDecimal("1000.0");

        contract.setValue(value1);
        assertEquals(value1, contract.getValue());

        // Test BigDecimal equality
        assertEquals(0, value1.compareTo(value2));
    }

    @Test
    void testLocalDateOperations() {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusMonths(12);

        contract.setBeginDate(today);
        contract.setEndDate(future);

        assertEquals(today, contract.getBeginDate());
        assertEquals(future, contract.getEndDate());
        assertTrue(contract.getEndDate().isAfter(contract.getBeginDate()));
    }

    @Test
    void testEntityAnnotation() {
        assertTrue(Contract.class.isAnnotationPresent(Entity.class));
    }

    @Test
    void testDataAnnotation() {
        assertTrue(Contract.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testBuilderAnnotation() {
        assertTrue(Contract.class.isAnnotationPresent(Builder.class));
    }

    @Test
    void testNoArgsConstructorAnnotation() {
        assertTrue(Contract.class.isAnnotationPresent(NoArgsConstructor.class));
    }

    @Test
    void testAllArgsConstructorAnnotation() {
        assertTrue(Contract.class.isAnnotationPresent(AllArgsConstructor.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        var idField = Contract.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());
    }

    @Test
    void testNameFieldAnnotations() throws NoSuchFieldException {
        var nameField = Contract.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(Column.class));

        Column column = nameField.getAnnotation(Column.class);
        assertFalse(column.nullable());
        assertTrue(column.unique());
    }

    @Test
    void testBeginDateFieldAnnotations() throws NoSuchFieldException {
        var beginDateField = Contract.class.getDeclaredField("beginDate");
        assertTrue(beginDateField.isAnnotationPresent(DateTimeFormat.class));

        DateTimeFormat dateTimeFormat = beginDateField.getAnnotation(DateTimeFormat.class);
        assertEquals(DateTimeFormat.ISO.DATE, dateTimeFormat.iso());
    }

    @Test
    void testEndDateFieldAnnotations() throws NoSuchFieldException {
        var endDateField = Contract.class.getDeclaredField("endDate");
        assertTrue(endDateField.isAnnotationPresent(DateTimeFormat.class));

        DateTimeFormat dateTimeFormat = endDateField.getAnnotation(DateTimeFormat.class);
        assertEquals(DateTimeFormat.ISO.DATE, dateTimeFormat.iso());
    }

    @Test
    void testStatusFieldAnnotations() throws NoSuchFieldException {
        var statusField = Contract.class.getDeclaredField("status");
        assertTrue(statusField.isAnnotationPresent(Enumerated.class));

        Enumerated enumerated = statusField.getAnnotation(Enumerated.class);
        assertEquals(EnumType.STRING, enumerated.value());
    }

    @Test
    void testCustomerFieldAnnotations() throws NoSuchFieldException {
        var customerField = Contract.class.getDeclaredField("customer");
        assertTrue(customerField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void testUserFieldAnnotations() throws NoSuchFieldException {
        var userField = Contract.class.getDeclaredField("user");
        assertTrue(userField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void testContractToString() {
        contract.setId(1L);
        contract.setName("Test Contract");
        contract.setContent("Contract content");

        String contractString = contract.toString();
        assertNotNull(contractString);
        assertTrue(contractString.contains("1"));
        assertTrue(contractString.contains("Test Contract"));
        assertTrue(contractString.contains("Contract content"));
    }

    @Test
    void testContractEquals() {
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        BigDecimal value = new BigDecimal("10000.00");

        Contract contract1 = Contract.builder()
                .id(1L)
                .name("Contract 1")
                .content("Content 1")
                .value(value)
                .beginDate(beginDate)
                .endDate(endDate)
                .status(Status.IMPLEMENTED)
                .build();

        Contract contract2 = Contract.builder()
                .id(1L)
                .name("Contract 1")
                .content("Content 1")
                .value(value)
                .beginDate(beginDate)
                .endDate(endDate)
                .status(Status.IMPLEMENTED)
                .build();

        assertEquals(contract1, contract2);
    }

    @Test
    void testContractNotEquals() {
        Contract contract1 = Contract.builder()
                .id(1L)
                .name("Contract 1")
                .build();

        Contract contract2 = Contract.builder()
                .id(2L)
                .name("Contract 2")
                .build();

        assertNotEquals(contract1, contract2);
    }

    @Test
    void testContractHashCode() {
        contract.setId(1L);
        contract.setName("Test Contract");

        int hashCode1 = contract.hashCode();
        int hashCode2 = contract.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testAllStatusValues() {
        for (Status status : Status.values()) {
            contract.setStatus(status);
            assertEquals(status, contract.getStatus());
        }
    }

    @Test
    void testContractWithCompleteData() {
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        BigDecimal value = new BigDecimal("50000.00");

        contract.setId(1L);
        contract.setName("Complete Contract");
        contract.setContent("Complete contract content");
        contract.setValue(value);
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setStatus(Status.IMPLEMENTED);
        contract.setCustomer(mockCustomer);
        contract.setUser(mockUser);

        assertEquals(1L, contract.getId());
        assertEquals("Complete Contract", contract.getName());
        assertEquals("Complete contract content", contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(Status.IMPLEMENTED, contract.getStatus());
        assertEquals(mockCustomer, contract.getCustomer());
        assertEquals(mockUser, contract.getUser());
    }

    @Test
    void testContractDuration() {
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);

        assertTrue(contract.getEndDate().isAfter(contract.getBeginDate()));
        assertEquals(364, contract.getBeginDate().until(contract.getEndDate()).getDays());
    }

    @Test
    void testContractFieldTypes() throws NoSuchFieldException {
        assertEquals(Long.class, Contract.class.getDeclaredField("id").getType());
        assertEquals(String.class, Contract.class.getDeclaredField("name").getType());
        assertEquals(String.class, Contract.class.getDeclaredField("content").getType());
        assertEquals(BigDecimal.class, Contract.class.getDeclaredField("value").getType());
        assertEquals(LocalDate.class, Contract.class.getDeclaredField("beginDate").getType());
        assertEquals(LocalDate.class, Contract.class.getDeclaredField("endDate").getType());
        assertEquals(Status.class, Contract.class.getDeclaredField("status").getType());
        assertEquals(Customer.class, Contract.class.getDeclaredField("customer").getType());
        assertEquals(User.class, Contract.class.getDeclaredField("user").getType());
    }

    @Test
    void testBigDecimalPrecision() {
        BigDecimal preciseValue = new BigDecimal("12345.67890");
        contract.setValue(preciseValue);

        assertEquals(preciseValue, contract.getValue());
        assertEquals("12345.67890", contract.getValue().toString());
    }
}