package crm.viewResolver;

import crm.view.CsvView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class CsvViewResolverTest {

    private CsvViewResolver csvViewResolver;

    @BeforeEach
    public void setUp() {
        csvViewResolver = new CsvViewResolver();
    }

    @Test
    public void testResolveViewName() throws Exception {
        View view = csvViewResolver.resolveViewName("testView", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }

    @Test
    public void testResolveViewNameWithNullViewName() throws Exception {
        View view = csvViewResolver.resolveViewName(null, Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }

    @Test
    public void testResolveViewNameWithEmptyViewName() throws Exception {
        View view = csvViewResolver.resolveViewName("", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }

    @Test
    public void testResolveViewNameWithDifferentLocales() throws Exception {
        View view1 = csvViewResolver.resolveViewName("view", Locale.US);
        View view2 = csvViewResolver.resolveViewName("view", Locale.CANADA);
        View view3 = csvViewResolver.resolveViewName("view", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotNull(view3);
        assertTrue(view1 instanceof CsvView);
        assertTrue(view2 instanceof CsvView);
        assertTrue(view3 instanceof CsvView);
    }

    @Test
    public void testResolveViewNameReturnsNewInstance() throws Exception {
        View view1 = csvViewResolver.resolveViewName("view1", Locale.US);
        View view2 = csvViewResolver.resolveViewName("view2", Locale.US);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotSame(view1, view2);
    }
}
