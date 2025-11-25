package crm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebAppConfigTest {

    private WebAppConfig webAppConfig;
    private ViewControllerRegistry viewControllerRegistry;
    private ContentNegotiationConfigurer contentNegotiationConfigurer;

    @BeforeEach
    public void setUp() {
        webAppConfig = new WebAppConfig();
        viewControllerRegistry = mock(ViewControllerRegistry.class);
        contentNegotiationConfigurer = mock(ContentNegotiationConfigurer.class);
    }

    @Test
    public void testAddViewControllers() {
        webAppConfig.addViewControllers(viewControllerRegistry);
        verify(viewControllerRegistry, atLeastOnce()).addViewController(anyString());
    }

    @Test
    public void testConfigureContentNegotiation() {
        webAppConfig.configureContentNegotiation(contentNegotiationConfigurer);
        verify(contentNegotiationConfigurer).ignoreAcceptHeader(false);
        verify(contentNegotiationConfigurer).defaultContentType(MediaType.APPLICATION_JSON);
        verify(contentNegotiationConfigurer).mediaTypes(anyMap());
    }

    @Test
    public void testTemplateResolver() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertNotNull(resolver);
    }

    @Test
    public void testTemplateEngine() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        assertNotNull(engine);
    }

    @Test
    public void testViewResolver() {
        ViewResolver viewResolver = webAppConfig.viewResolver();
        assertNotNull(viewResolver);
    }

    @Test
    public void testExcelViewResolver() {
        ViewResolver excelResolver = webAppConfig.excelViewResolver();
        assertNotNull(excelResolver);
    }

    @Test
    public void testCsvViewResolver() {
        ViewResolver csvResolver = webAppConfig.csvViewResolver();
        assertNotNull(csvResolver);
    }

    @Test
    public void testPdfViewResolver() {
        ViewResolver pdfResolver = webAppConfig.pdfViewResolver();
        assertNotNull(pdfResolver);
    }

    @Test
    public void testContentNegotiatingViewResolver() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);
        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(manager);
        assertNotNull(resolver);
    }
}
