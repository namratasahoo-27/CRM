package crm.service;

import crm.entity.Contract;
import crm.entity.Customer;
import crm.entity.Status;
import crm.entity.User;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ContractServiceTest {

    @Test
    public void testContractServiceInterface() {
        assertNotNull(ContractService.class);
    }

    @Test
    public void testFindByNameMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findByName", String.class));
    }

    @Test
    public void testListAllContractsMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("listAllContracts"));
    }

    @Test
    public void testShowContractMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("showContract", Long.class));
    }

    @Test
    public void testFindAllByValueLessThanEqualMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByValueLessThanEqual", BigDecimal.class));
    }

    @Test
    public void testFindAllByValueGreaterThanEqualMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByValueGreaterThanEqual", BigDecimal.class));
    }

    @Test
    public void testFindAllByBeginDateMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByBeginDate", LocalDate.class));
    }

    @Test
    public void testFindAllByEndDateMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByEndDate", LocalDate.class));
    }

    @Test
    public void testFindAllByStatusMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByStatus", Status.class));
    }

    @Test
    public void testFindAllByCustomerMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByCustomer", Customer.class));
    }

    @Test
    public void testFindAllByUserMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("findAllByUser", User.class));
    }

    @Test
    public void testSaveContractMethodExists() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("saveContract", Contract.class));
    }

    @Test
    public void testContractServiceIsInterface() {
        assertTrue(ContractService.class.isInterface());
    }
}
