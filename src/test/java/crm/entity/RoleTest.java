package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import lombok.Data;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void testRoleCreation() {
        // Test that role can be instantiated
        assertNotNull(role);
    }

    @Test
    void testSetAndGetId() {
        // Arrange
        int expectedId = 1;

        // Act
        role.setId(expectedId);

        // Assert
        assertEquals(expectedId, role.getId());
    }

    @Test
    void testSetAndGetName() {
        // Arrange
        String expectedName = "ADMIN";

        // Act
        role.setName(expectedName);

        // Assert
        assertEquals(expectedName, role.getName());
    }

    @Test
    void testSetIdWithZero() {
        // Act
        role.setId(0);

        // Assert
        assertEquals(0, role.getId());
    }

    @Test
    void testSetIdWithNegativeValue() {
        // Act
        role.setId(-1);

        // Assert
        assertEquals(-1, role.getId());
    }

    @Test
    void testSetIdWithLargeValue() {
        // Act
        int largeId = Integer.MAX_VALUE;
        role.setId(largeId);

        // Assert
        assertEquals(largeId, role.getId());
    }

    @Test
    void testSetNameWithNull() {
        // Act
        role.setName(null);

        // Assert
        assertNull(role.getName());
    }

    @Test
    void testSetNameWithEmptyString() {
        // Act
        role.setName("");

        // Assert
        assertEquals("", role.getName());
    }

    @Test
    void testSetNameWithWhitespace() {
        // Act
        String nameWithWhitespace = "  ADMIN  ";
        role.setName(nameWithWhitespace);

        // Assert
        assertEquals(nameWithWhitespace, role.getName());
    }

    @Test
    void testSetNameWithSpecialCharacters() {
        // Act
        String specialName = "ADMIN@123";
        role.setName(specialName);

        // Assert
        assertEquals(specialName, role.getName());
    }

    @Test
    void testRoleToString() {
        // Arrange
        role.setId(1);
        role.setName("USER");

        // Act
        String roleString = role.toString();

        // Assert
        assertNotNull(roleString);
        assertTrue(roleString.contains("1"));
        assertTrue(roleString.contains("USER"));
    }

    @Test
    void testRoleEquals() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(1);
        role2.setName("ADMIN");

        // Assert
        assertEquals(role1, role2);
    }

    @Test
    void testRoleNotEquals() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("USER");

        // Assert
        assertNotEquals(role1, role2);
    }

    @Test
    void testRoleHashCode() {
        // Arrange
        role.setId(1);
        role.setName("ADMIN");

        // Act
        int hashCode1 = role.hashCode();
        int hashCode2 = role.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testRoleHashCodeWithDifferentValues() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("USER");

        // Act
        int hashCode1 = role1.hashCode();
        int hashCode2 = role2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testEntityAnnotation() {
        // Verify that the Role class has @Entity annotation
        assertTrue(Role.class.isAnnotationPresent(Entity.class));
    }

    @Test
    void testTableAnnotation() {
        // Verify that the Role class has @Table annotation with correct name
        assertTrue(Role.class.isAnnotationPresent(Table.class));
        Table table = Role.class.getAnnotation(Table.class);
        assertEquals("role", table.name());
    }

    @Test
    void testDataAnnotation() {
        // Verify that the Role class has @Data annotation from Lombok
        assertTrue(Role.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        // Verify that the id field has correct JPA annotations
        var idField = Role.class.getDeclaredField("id");

        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));
        assertTrue(idField.isAnnotationPresent(Column.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());

        Column column = idField.getAnnotation(Column.class);
        assertEquals("role_id", column.name());
    }

    @Test
    void testNameFieldAnnotations() throws NoSuchFieldException {
        // Verify that the name field has correct JPA annotations
        var nameField = Role.class.getDeclaredField("name");

        assertTrue(nameField.isAnnotationPresent(Column.class));

        Column column = nameField.getAnnotation(Column.class);
        assertEquals("role", column.name());
        assertTrue(column.unique());
    }

    @Test
    void testRoleWithMultipleOperations() {
        // Test multiple operations on the same role instance
        role.setId(1);
        role.setName("ADMIN");

        assertEquals(1, role.getId());
        assertEquals("ADMIN", role.getName());

        role.setId(2);
        role.setName("USER");

        assertEquals(2, role.getId());
        assertEquals("USER", role.getName());
    }

    @Test
    void testDefaultValues() {
        // Test default values when role is created
        Role newRole = new Role();

        assertEquals(0, newRole.getId()); // primitive int defaults to 0
        assertNull(newRole.getName()); // String defaults to null
    }
}