package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ContractTest {

    private Contract contract;
    private Customer customer;
    private User user;

    @BeforeEach
    public void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("customer@test.com")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@test.com")
                .build();

        contract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .content("Contract content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();
    }

    @Test
    public void testContractCreation() {
        assertNotNull(contract);
        assertEquals(1L, contract.getId());
        assertEquals("Test Contract", contract.getName());
        assertEquals("Contract content", contract.getContent());
        assertEquals(new BigDecimal("10000.00"), contract.getValue());
        assertEquals(LocalDate.of(2025, 1, 1), contract.getBeginDate());
        assertEquals(LocalDate.of(2025, 12, 31), contract.getEndDate());
        assertEquals(Status.PROPOSED, contract.getStatus());
        assertNotNull(contract.getCustomer());
        assertNotNull(contract.getUser());
    }

    @Test
    public void testContractBuilder() {
        Contract builtContract = Contract.builder()
                .name("New Contract")
                .value(new BigDecimal("5000.00"))
                .status(Status.NEGOTIATED)
                .build();

        assertNotNull(builtContract);
        assertEquals("New Contract", builtContract.getName());
        assertEquals(new BigDecimal("5000.00"), builtContract.getValue());
        assertEquals(Status.NEGOTIATED, builtContract.getStatus());
    }

    @Test
    public void testSetters() {
        contract.setName("Updated Contract");
        contract.setContent("Updated content");
        contract.setValue(new BigDecimal("15000.00"));
        contract.setStatus(Status.DONE);

        assertEquals("Updated Contract", contract.getName());
        assertEquals("Updated content", contract.getContent());
        assertEquals(new BigDecimal("15000.00"), contract.getValue());
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    public void testNoArgsConstructor() {
        Contract emptyContract = new Contract();
        assertNotNull(emptyContract);
        assertNull(emptyContract.getId());
        assertNull(emptyContract.getName());
    }

    @Test
    public void testAllArgsConstructor() {
        LocalDate begin = LocalDate.of(2025, 6, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Contract constructedContract = new Contract(
                2L,
                "Constructor Contract",
                "Content",
                new BigDecimal("20000.00"),
                begin,
                end,
                Status.IMPLEMENTED,
                customer,
                user
        );

        assertNotNull(constructedContract);
        assertEquals(2L, constructedContract.getId());
        assertEquals("Constructor Contract", constructedContract.getName());
        assertEquals(begin, constructedContract.getBeginDate());
        assertEquals(end, constructedContract.getEndDate());
    }

    @Test
    public void testContractWithNullValues() {
        Contract nullContract = Contract.builder().build();
        assertNull(nullContract.getName());
        assertNull(nullContract.getValue());
        assertNull(nullContract.getStatus());
    }

    @Test
    public void testDateModification() {
        LocalDate newBeginDate = LocalDate.of(2025, 3, 1);
        LocalDate newEndDate = LocalDate.of(2026, 3, 1);

        contract.setBeginDate(newBeginDate);
        contract.setEndDate(newEndDate);

        assertEquals(newBeginDate, contract.getBeginDate());
        assertEquals(newEndDate, contract.getEndDate());
    }

    @Test
    public void testCustomerAssociation() {
        Customer newCustomer = Customer.builder()
                .id(2L)
                .name("New Customer")
                .build();

        contract.setCustomer(newCustomer);
        assertEquals(2L, contract.getCustomer().getId());
        assertEquals("New Customer", contract.getCustomer().getName());
    }

    @Test
    public void testUserAssociation() {
        User newUser = User.builder()
                .id(2L)
                .username("newuser")
                .build();

        contract.setUser(newUser);
        assertEquals(2L, contract.getUser().getId());
        assertEquals("newuser", contract.getUser().getUsername());
    }
}
