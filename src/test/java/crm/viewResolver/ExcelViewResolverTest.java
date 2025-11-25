package crm.viewResolver;

import crm.view.ExcelView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelViewResolverTest {

    private ExcelViewResolver excelViewResolver;

    @BeforeEach
    public void setUp() {
        excelViewResolver = new ExcelViewResolver();
    }

    @Test
    public void testResolveViewName() throws Exception {
        View view = excelViewResolver.resolveViewName("testView", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }

    @Test
    public void testResolveViewNameWithNullViewName() throws Exception {
        View view = excelViewResolver.resolveViewName(null, Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }

    @Test
    public void testResolveViewNameWithEmptyViewName() throws Exception {
        View view = excelViewResolver.resolveViewName("", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }

    @Test
    public void testResolveViewNameWithDifferentLocales() throws Exception {
        View view1 = excelViewResolver.resolveViewName("view", Locale.US);
        View view2 = excelViewResolver.resolveViewName("view", Locale.FRANCE);
        View view3 = excelViewResolver.resolveViewName("view", Locale.JAPAN);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotNull(view3);
        assertTrue(view1 instanceof ExcelView);
        assertTrue(view2 instanceof ExcelView);
        assertTrue(view3 instanceof ExcelView);
    }

    @Test
    public void testResolveViewNameReturnsNewInstance() throws Exception {
        View view1 = excelViewResolver.resolveViewName("view1", Locale.US);
        View view2 = excelViewResolver.resolveViewName("view2", Locale.US);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotSame(view1, view2);
    }
}
