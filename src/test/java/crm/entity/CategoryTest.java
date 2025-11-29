package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import lombok.Data;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void testCategoryCreation() {
        assertNotNull(category);
    }

    @Test
    void testSetAndGetId() {
        Long expectedId = 1L;
        category.setId(expectedId);
        assertEquals(expectedId, category.getId());
    }

    @Test
    void testSetAndGetName() {
        String expectedName = "VIP Customer";
        category.setName(expectedName);
        assertEquals(expectedName, category.getName());
    }

    @Test
    void testSetIdWithNull() {
        category.setId(null);
        assertNull(category.getId());
    }

    @Test
    void testSetIdWithZero() {
        category.setId(0L);
        assertEquals(0L, category.getId());
    }

    @Test
    void testSetIdWithNegativeValue() {
        category.setId(-1L);
        assertEquals(-1L, category.getId());
    }

    @Test
    void testSetIdWithLargeValue() {
        Long largeId = Long.MAX_VALUE;
        category.setId(largeId);
        assertEquals(largeId, category.getId());
    }

    @Test
    void testSetNameWithNull() {
        category.setName(null);
        assertNull(category.getName());
    }

    @Test
    void testSetNameWithEmptyString() {
        category.setName("");
        assertEquals("", category.getName());
    }

    @Test
    void testSetNameWithWhitespace() {
        String nameWithWhitespace = "  Premium Customer  ";
        category.setName(nameWithWhitespace);
        assertEquals(nameWithWhitespace, category.getName());
    }

    @Test
    void testSetNameWithSpecialCharacters() {
        String specialName = "VIP@2023";
        category.setName(specialName);
        assertEquals(specialName, category.getName());
    }

    @Test
    void testSetNameWithLongString() {
        String longName = "This is a very long category name that might exceed normal length expectations for testing purposes";
        category.setName(longName);
        assertEquals(longName, category.getName());
    }

    @Test
    void testCategoryToString() {
        category.setId(1L);
        category.setName("Premium");

        String categoryString = category.toString();
        assertNotNull(categoryString);
        assertTrue(categoryString.contains("1"));
        assertTrue(categoryString.contains("Premium"));
    }

    @Test
    void testCategoryEquals() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("VIP");

        assertEquals(category1, category2);
    }

    @Test
    void testCategoryNotEquals() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Regular");

        assertNotEquals(category1, category2);
    }

    @Test
    void testCategoryHashCode() {
        category.setId(1L);
        category.setName("Premium");

        int hashCode1 = category.hashCode();
        int hashCode2 = category.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testCategoryHashCodeWithDifferentValues() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Regular");

        int hashCode1 = category1.hashCode();
        int hashCode2 = category2.hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testEntityAnnotation() {
        assertTrue(Category.class.isAnnotationPresent(Entity.class));
    }

    @Test
    void testTableAnnotation() {
        assertTrue(Category.class.isAnnotationPresent(Table.class));
        Table table = Category.class.getAnnotation(Table.class);
        assertEquals("category", table.name());
    }

    @Test
    void testDataAnnotation() {
        assertTrue(Category.class.isAnnotationPresent(Data.class));
    }

    @Test
    void testIdFieldAnnotations() throws NoSuchFieldException {
        var idField = Category.class.getDeclaredField("id");

        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));
        assertTrue(idField.isAnnotationPresent(Column.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.AUTO, generatedValue.strategy());

        Column column = idField.getAnnotation(Column.class);
        assertEquals("category_id", column.name());
    }

    @Test
    void testNameFieldAnnotations() throws NoSuchFieldException {
        var nameField = Category.class.getDeclaredField("name");

        assertTrue(nameField.isAnnotationPresent(Column.class));

        Column column = nameField.getAnnotation(Column.class);
        assertEquals("category", column.name());
    }

    @Test
    void testCategoryWithMultipleOperations() {
        category.setId(1L);
        category.setName("VIP");

        assertEquals(1L, category.getId());
        assertEquals("VIP", category.getName());

        category.setId(2L);
        category.setName("Regular");

        assertEquals(2L, category.getId());
        assertEquals("Regular", category.getName());
    }

    @Test
    void testDefaultValues() {
        Category newCategory = new Category();

        assertNull(newCategory.getId());
        assertNull(newCategory.getName());
    }

    @Test
    void testCategoryEqualsWithNull() {
        category.setId(1L);
        category.setName("VIP");

        assertNotEquals(category, null);
    }

    @Test
    void testCategoryEqualsWithDifferentClass() {
        category.setId(1L);
        category.setName("VIP");

        assertNotEquals(category, "Not a Category");
    }

    @Test
    void testCategoryEqualsWithSameReference() {
        category.setId(1L);
        category.setName("VIP");

        assertEquals(category, category);
    }

    @Test
    void testCategoryFieldTypes() throws NoSuchFieldException {
        var idField = Category.class.getDeclaredField("id");
        var nameField = Category.class.getDeclaredField("name");

        assertEquals(Long.class, idField.getType());
        assertEquals(String.class, nameField.getType());
    }

    @Test
    void testCategoryWithCommonCategoryNames() {
        String[] commonNames = {"VIP", "Premium", "Regular", "Gold", "Silver", "Bronze", "Standard"};

        for (String name : commonNames) {
            category.setName(name);
            assertEquals(name, category.getName());
        }
    }

    @Test
    void testCategoryWithNumericName() {
        category.setName("123");
        assertEquals("123", category.getName());
    }

    @Test
    void testCategoryWithMixedCaseNames() {
        category.setName("vIp CuStOmEr");
        assertEquals("vIp CuStOmEr", category.getName());
    }
}