package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    private Category category;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Technology");
    }

    @Test
    public void testCategoryCreation() {
        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("Technology", category.getName());
    }

    @Test
    public void testSetters() {
        category.setId(2L);
        category.setName("Finance");

        assertEquals(2L, category.getId());
        assertEquals("Finance", category.getName());
    }

    @Test
    public void testGetters() {
        assertEquals(1L, category.getId());
        assertEquals("Technology", category.getName());
    }

    @Test
    public void testCategoryWithNullValues() {
        Category nullCategory = new Category();
        assertNull(nullCategory.getId());
        assertNull(nullCategory.getName());
    }

    @Test
    public void testCategoryEquality() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Tech");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Tech");

        assertEquals(category1.getId(), category2.getId());
        assertEquals(category1.getName(), category2.getName());
    }

    @Test
    public void testCategoryInequality() {
        Category category1 = new Category();
        category1.setId(1L);

        Category category2 = new Category();
        category2.setId(2L);

        assertNotEquals(category1.getId(), category2.getId());
    }

    @Test
    public void testNameModification() {
        String oldName = category.getName();
        category.setName("Updated Category");

        assertNotEquals(oldName, category.getName());
        assertEquals("Updated Category", category.getName());
    }
}
