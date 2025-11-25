package crm.viewResolver;

import crm.view.PdfView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class PdfViewResolverTest {

    private PdfViewResolver pdfViewResolver;

    @BeforeEach
    public void setUp() {
        pdfViewResolver = new PdfViewResolver();
    }

    @Test
    public void testResolveViewName() throws Exception {
        View view = pdfViewResolver.resolveViewName("testView", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof PdfView);
    }

    @Test
    public void testResolveViewNameWithNullViewName() throws Exception {
        View view = pdfViewResolver.resolveViewName(null, Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof PdfView);
    }

    @Test
    public void testResolveViewNameWithEmptyViewName() throws Exception {
        View view = pdfViewResolver.resolveViewName("", Locale.US);
        assertNotNull(view);
        assertTrue(view instanceof PdfView);
    }

    @Test
    public void testResolveViewNameWithDifferentLocales() throws Exception {
        View view1 = pdfViewResolver.resolveViewName("view", Locale.GERMAN);
        View view2 = pdfViewResolver.resolveViewName("view", Locale.ITALIAN);
        View view3 = pdfViewResolver.resolveViewName("view", Locale.CHINESE);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotNull(view3);
        assertTrue(view1 instanceof PdfView);
        assertTrue(view2 instanceof PdfView);
        assertTrue(view3 instanceof PdfView);
    }

    @Test
    public void testResolveViewNameReturnsNewInstance() throws Exception {
        View view1 = pdfViewResolver.resolveViewName("view1", Locale.US);
        View view2 = pdfViewResolver.resolveViewName("view2", Locale.US);

        assertNotNull(view1);
        assertNotNull(view2);
        assertNotSame(view1, view2);
    }
}
