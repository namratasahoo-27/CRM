package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserTest {

    private CurrentUser currentUser;
    private User mockUser;
    private Set<GrantedAuthority> mockAuthorities;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser();
        mockUser = mock(User.class);
        mockAuthorities = new HashSet<>();
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void testCurrentUserCreation() {
        assertNotNull(currentUser);
    }

    @Test
    void testImplementsUserDetails() {
        assertTrue(currentUser instanceof UserDetails);
    }

    @Test
    void testSetAndGetUser() {
        currentUser.setUser(mockUser);
        assertEquals(mockUser, currentUser.getUser());
    }

    @Test
    void testSetAndGetAuthorities() {
        currentUser.setAuthorities(mockAuthorities);
        assertEquals(mockAuthorities, currentUser.getAuthorities());
    }

    @Test
    void testGetAuthoritiesFromUserDetails() {
        currentUser.setAuthorities(mockAuthorities);

        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        String expectedPassword = "testPassword";
        when(mockUser.getPassword()).thenReturn(expectedPassword);
        currentUser.setUser(mockUser);

        String actualPassword = currentUser.getPassword();
        assertEquals(expectedPassword, actualPassword);
        verify(mockUser).getPassword();
    }

    @Test
    void testGetPasswordWithNullUser() {
        currentUser.setUser(null);

        assertThrows(NullPointerException.class, () -> {
            currentUser.getPassword();
        });
    }

    @Test
    void testGetUsername() {
        String expectedUsername = "testUser";
        when(mockUser.getUsername()).thenReturn(expectedUsername);
        currentUser.setUser(mockUser);

        String actualUsername = currentUser.getUsername();
        assertEquals(expectedUsername, actualUsername);
        verify(mockUser).getUsername();
    }

    @Test
    void testGetUsernameWithNullUser() {
        currentUser.setUser(null);

        assertThrows(NullPointerException.class, () -> {
            currentUser.getUsername();
        });
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(currentUser.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(currentUser.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testAllUserDetailsMethodsReturnTrue() {
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testSetNullUser() {
        currentUser.setUser(null);
        assertNull(currentUser.getUser());
    }

    @Test
    void testSetNullAuthorities() {
        currentUser.setAuthorities(null);
        assertNull(currentUser.getAuthorities());
    }

    @Test
    void testGetAuthoritiesWithNullAuthorities() {
        currentUser.setAuthorities(null);
        assertNull(currentUser.getAuthorities());
    }

    @Test
    void testEmptyAuthorities() {
        Set<GrantedAuthority> emptyAuthorities = new HashSet<>();
        currentUser.setAuthorities(emptyAuthorities);

        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testSingleAuthority() {
        Set<GrantedAuthority> singleAuthority = new HashSet<>();
        singleAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));
        currentUser.setAuthorities(singleAuthority);

        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testMultipleAuthorities() {
        Set<GrantedAuthority> multipleAuthorities = new HashSet<>();
        multipleAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        multipleAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        multipleAuthorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        currentUser.setAuthorities(multipleAuthorities);

        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        assertNotNull(authorities);
        assertEquals(3, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }

    @Test
    void testCurrentUserToString() {
        when(mockUser.getUsername()).thenReturn("testUser");
        currentUser.setUser(mockUser);
        currentUser.setAuthorities(mockAuthorities);

        String currentUserString = currentUser.toString();
        assertNotNull(currentUserString);
    }

    @Test
    void testCurrentUserEquals() {
        CurrentUser currentUser1 = new CurrentUser();
        currentUser1.setUser(mockUser);
        currentUser1.setAuthorities(mockAuthorities);

        CurrentUser currentUser2 = new CurrentUser();
        currentUser2.setUser(mockUser);
        currentUser2.setAuthorities(mockAuthorities);

        assertEquals(currentUser1, currentUser2);
    }

    @Test
    void testCurrentUserNotEquals() {
        User anotherUser = mock(User.class);

        CurrentUser currentUser1 = new CurrentUser();
        currentUser1.setUser(mockUser);

        CurrentUser currentUser2 = new CurrentUser();
        currentUser2.setUser(anotherUser);

        assertNotEquals(currentUser1, currentUser2);
    }

    @Test
    void testCurrentUserHashCode() {
        currentUser.setUser(mockUser);
        currentUser.setAuthorities(mockAuthorities);

        int hashCode1 = currentUser.hashCode();
        int hashCode2 = currentUser.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testUserDetailsInterfaceImplementation() {
        // Verify that all UserDetails methods are implemented
        assertDoesNotThrow(() -> {
            when(mockUser.getUsername()).thenReturn("testUser");
            when(mockUser.getPassword()).thenReturn("testPassword");
            currentUser.setUser(mockUser);
            currentUser.setAuthorities(mockAuthorities);

            currentUser.getUsername();
            currentUser.getPassword();
            currentUser.getAuthorities();
            currentUser.isAccountNonExpired();
            currentUser.isAccountNonLocked();
            currentUser.isCredentialsNonExpired();
            currentUser.isEnabled();
        });
    }

    @Test
    void testGetPasswordWithEmptyPassword() {
        when(mockUser.getPassword()).thenReturn("");
        currentUser.setUser(mockUser);

        String password = currentUser.getPassword();
        assertEquals("", password);
    }

    @Test
    void testGetUsernameWithEmptyUsername() {
        when(mockUser.getUsername()).thenReturn("");
        currentUser.setUser(mockUser);

        String username = currentUser.getUsername();
        assertEquals("", username);
    }

    @Test
    void testGetPasswordWithNullPassword() {
        when(mockUser.getPassword()).thenReturn(null);
        currentUser.setUser(mockUser);

        String password = currentUser.getPassword();
        assertNull(password);
    }

    @Test
    void testGetUsernameWithNullUsername() {
        when(mockUser.getUsername()).thenReturn(null);
        currentUser.setUser(mockUser);

        String username = currentUser.getUsername();
        assertNull(username);
    }

    @Test
    void testUserDetailsFieldsAreAlwaysTrue() {
        // Test that these methods always return true regardless of state
        currentUser.setUser(null);
        currentUser.setAuthorities(null);

        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testCurrentUserWithRealUser() {
        User realUser = new User();
        realUser.setUsername("realUser");
        realUser.setPassword("realPassword");

        currentUser.setUser(realUser);

        assertEquals("realUser", currentUser.getUsername());
        assertEquals("realPassword", currentUser.getPassword());
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testDataAnnotationFunctionality() {
        // Test that @Data annotation provides proper getters and setters
        User testUser = new User();
        Set<GrantedAuthority> testAuthorities = new HashSet<>();

        assertDoesNotThrow(() -> {
            currentUser.setUser(testUser);
            currentUser.setAuthorities(testAuthorities);

            assertEquals(testUser, currentUser.getUser());
            assertEquals(testAuthorities, currentUser.getAuthorities());
        });
    }
}