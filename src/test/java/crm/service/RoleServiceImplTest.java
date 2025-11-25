package crm.service;

import crm.entity.Role;
import crm.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest {

    private RoleServiceImpl roleService;
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleService = new RoleServiceImpl(roleRepository);
    }

    @Test
    public void testListAllRoles() {
        List<Role> roles = new ArrayList<>();
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_USER");
        roles.add(role1);

        when(roleRepository.findAll()).thenReturn(roles);

        Iterable<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        verify(roleRepository).findAll();
    }

    @Test
    public void testListAllRolesEmpty() {
        when(roleRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        verify(roleRepository).findAll();
    }

    @Test
    public void testConstructor() {
        assertNotNull(roleService);
    }

    @Test
    public void testListAllRolesMultiple() {
        List<Role> roles = new ArrayList<>();
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("ROLE_ADMIN");

        roles.add(role1);
        roles.add(role2);

        when(roleRepository.findAll()).thenReturn(roles);

        Iterable<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        verify(roleRepository, times(1)).findAll();
    }
}
