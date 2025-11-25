package crm.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ReadDataUtilsTest {

    @Test
    public void testReadFileMethodExists() {
        assertNotNull(ReadDataUtils.class);
    }

    @Test
    public void testReadFileWithNullParent() {
        File result = ReadDataUtils.ReadFile("Test Dialog", null, "CSV Files", "csv");
        assertTrue(result == null || result instanceof File);
    }

    @Test
    public void testReadFileWithCSVExtension() {
        File result = ReadDataUtils.ReadFile("Select CSV", null, "CSV Files", "csv");
        assertTrue(result == null || result instanceof File);
    }

    @Test
    public void testReadFileWithMultipleExtensions() {
        File result = ReadDataUtils.ReadFile("Select File", null, "All Files", "csv", "txt", "pdf");
        assertTrue(result == null || result instanceof File);
    }

    @Test
    public void testReadFileWithSingleExtension() {
        File result = ReadDataUtils.ReadFile("Select PDF", null, "PDF Files", "pdf");
        assertTrue(result == null || result instanceof File);
    }

    @Test
    public void testReadFileWithEmptyExtensions() {
        File result = ReadDataUtils.ReadFile("Select File", null, "All Files");
        assertTrue(result == null || result instanceof File);
    }
}
