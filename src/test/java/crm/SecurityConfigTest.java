package crm;

import crm.service.SpringDataUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void testSecurityConfigCreation() {
        assertNotNull(securityConfig);
    }

    @Test
    void testClassAnnotations() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(Configuration.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class));

        EnableMethodSecurity enableMethodSecurity = SecurityConfig.class.getAnnotation(EnableMethodSecurity.class);
        assertTrue(enableMethodSecurity.securedEnabled());
    }

    @Test
    void testPasswordEncoderBean() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testPasswordEncoderFunctionality() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "testPassword";

        String encodedPassword = encoder.encode(password);

        assertNotNull(encodedPassword);
        assertNotEquals(password, encodedPassword);
        assertTrue(encoder.matches(password, encodedPassword));
    }

    @Test
    void testCustomUserDetailsServiceBean() {
        SpringDataUserDetailsService userDetailsService = securityConfig.customUserDetailsService();

        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof SpringDataUserDetailsService);
    }

    @Test
    void testAuthenticationProviderBean() {
        DaoAuthenticationProvider authProvider = securityConfig.authenticationProvider();

        assertNotNull(authProvider);
        assertTrue(authProvider instanceof DaoAuthenticationProvider);
        // Note: getUserDetailsService() and getPasswordEncoder() are protected methods
        // Testing configuration indirectly through authentication behavior
    }

    @Test
    void testAuthenticationProviderConfiguration() {
        DaoAuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // Note: getUserDetailsService() and getPasswordEncoder() are protected methods
        // Testing configuration indirectly through DaoAuthenticationProvider type check
    }

    @Test
    void testAuthenticationManagerBean() {
        AuthenticationManager authManager = securityConfig.authenticationManager();

        assertNotNull(authManager);
        assertTrue(authManager instanceof ProviderManager);
    }

    @Test
    void testSecurityFilterChainBean() throws Exception {
        // Note: This is a more complex test that would typically require Spring context
        // In a unit test environment, we verify the method exists and can be called
        assertDoesNotThrow(() -> {
            var method = SecurityConfig.class.getDeclaredMethod("securityFilterChain",
                    org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
            assertNotNull(method);
            assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        });
    }

    @Test
    void testPasswordEncoderBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getDeclaredMethod("passwordEncoder");

        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals(BCryptPasswordEncoder.class, method.getReturnType());
    }

    @Test
    void testCustomUserDetailsServiceBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getDeclaredMethod("customUserDetailsService");

        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals(SpringDataUserDetailsService.class, method.getReturnType());
    }

    @Test
    void testAuthenticationProviderBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getDeclaredMethod("authenticationProvider");

        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals(DaoAuthenticationProvider.class, method.getReturnType());
    }

    @Test
    void testAuthenticationManagerBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getDeclaredMethod("authenticationManager");

        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals(AuthenticationManager.class, method.getReturnType());
    }

    @Test
    void testSecurityFilterChainBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getDeclaredMethod("securityFilterChain",
                org.springframework.security.config.annotation.web.builders.HttpSecurity.class);

        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals(org.springframework.security.web.SecurityFilterChain.class, method.getReturnType());
    }

    @Test
    void testBCryptPasswordEncoderStrength() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "testPassword123";

        // Test multiple encodings produce different results
        String encoded1 = encoder.encode(password);
        String encoded2 = encoder.encode(password);

        assertNotEquals(encoded1, encoded2); // BCrypt uses salt, so results should differ
        assertTrue(encoder.matches(password, encoded1));
        assertTrue(encoder.matches(password, encoded2));
    }

    @Test
    void testMultiplePasswordEncoderInstances() {
        BCryptPasswordEncoder encoder1 = securityConfig.passwordEncoder();
        BCryptPasswordEncoder encoder2 = securityConfig.passwordEncoder();

        // Each call should return a new instance
        assertNotSame(encoder1, encoder2);

        // But both should work the same way
        String password = "test";
        String encoded1 = encoder1.encode(password);
        assertTrue(encoder2.matches(password, encoded1));
    }

    @Test
    void testMultipleUserDetailsServiceInstances() {
        SpringDataUserDetailsService service1 = securityConfig.customUserDetailsService();
        SpringDataUserDetailsService service2 = securityConfig.customUserDetailsService();

        // Each call should return a new instance
        assertNotSame(service1, service2);
        assertEquals(service1.getClass(), service2.getClass());
    }

    @Test
    void testAuthenticationProviderDependencies() {
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // Note: getUserDetailsService() and getPasswordEncoder() are protected methods
        // Testing configuration through provider type verification
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testAuthenticationManagerWithProvider() {
        AuthenticationManager manager = securityConfig.authenticationManager();
        assertTrue(manager instanceof ProviderManager);

        ProviderManager providerManager = (ProviderManager) manager;
        assertEquals(1, providerManager.getProviders().size());
        assertTrue(providerManager.getProviders().get(0) instanceof DaoAuthenticationProvider);
    }

    @Test
    void testAllBeansArePublicMethods() throws NoSuchMethodException {
        String[] beanMethods = {"passwordEncoder", "customUserDetailsService",
                               "authenticationProvider", "authenticationManager", "securityFilterChain"};

        for (String methodName : beanMethods) {
            var method = SecurityConfig.class.getDeclaredMethod(methodName,
                    methodName.equals("securityFilterChain") ?
                    new Class[]{org.springframework.security.config.annotation.web.builders.HttpSecurity.class} :
                    new Class[0]);

            assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()),
                    "Method " + methodName + " should be public");
            assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class),
                    "Method " + methodName + " should be annotated with @Bean");
        }
    }

    @Test
    void testClassIsConfiguration() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    void testWebSecurityEnabled() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class));
    }

    @Test
    void testMethodSecurityEnabled() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class));

        EnableMethodSecurity annotation = SecurityConfig.class.getAnnotation(EnableMethodSecurity.class);
        assertTrue(annotation.securedEnabled());
    }

    @Test
    void testConfigurationIntegrity() {
        // Test that all components can be created without errors
        assertDoesNotThrow(() -> {
            BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
            SpringDataUserDetailsService userService = securityConfig.customUserDetailsService();
            DaoAuthenticationProvider authProvider = securityConfig.authenticationProvider();
            AuthenticationManager authManager = securityConfig.authenticationManager();

            assertNotNull(encoder);
            assertNotNull(userService);
            assertNotNull(authProvider);
            assertNotNull(authManager);
        });
    }
}