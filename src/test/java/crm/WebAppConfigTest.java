package crm;

import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebAppConfigTest {

    private WebAppConfig webAppConfig;
    private ViewControllerRegistry mockViewControllerRegistry;
    private ContentNegotiationConfigurer mockContentNegotiationConfigurer;
    private ContentNegotiationManager mockContentNegotiationManager;

    @BeforeEach
    void setUp() {
        webAppConfig = new WebAppConfig();
        mockViewControllerRegistry = mock(ViewControllerRegistry.class);
        mockContentNegotiationConfigurer = mock(ContentNegotiationConfigurer.class);
        mockContentNegotiationManager = mock(ContentNegotiationManager.class);
    }

    @Test
    void testAddViewControllers() {
        // Act
        webAppConfig.addViewControllers(mockViewControllerRegistry);

        // Assert
        verify(mockViewControllerRegistry).addViewController("/login");
        verify(mockViewControllerRegistry).addViewController("/");
        verify(mockViewControllerRegistry).addViewController("/user/menu");
        verify(mockViewControllerRegistry).addViewController("/customer/menu");
        verify(mockViewControllerRegistry).addViewController("/contract/menu");
        verify(mockViewControllerRegistry).addViewController("/contract/search");
        verify(mockViewControllerRegistry).addViewController("/admin");
        verify(mockViewControllerRegistry).addViewController("/search");
        verify(mockViewControllerRegistry).addViewController("/403");
        verify(mockViewControllerRegistry).addViewController("/logout");
        verify(mockViewControllerRegistry).setOrder(anyInt());
    }

    @Test
    void testConfigureContentNegotiation() {
        // Arrange
        when(mockContentNegotiationConfigurer.ignoreAcceptHeader(anyBoolean())).thenReturn(mockContentNegotiationConfigurer);
        when(mockContentNegotiationConfigurer.defaultContentType(any(MediaType.class))).thenReturn(mockContentNegotiationConfigurer);
        when(mockContentNegotiationConfigurer.mediaTypes(anyMap())).thenReturn(mockContentNegotiationConfigurer);

        // Act
        webAppConfig.configureContentNegotiation(mockContentNegotiationConfigurer);

        // Assert
        verify(mockContentNegotiationConfigurer).ignoreAcceptHeader(false);
        verify(mockContentNegotiationConfigurer).defaultContentType(MediaType.APPLICATION_JSON);
        verify(mockContentNegotiationConfigurer).mediaTypes(anyMap());
    }

    @Test
    void testContentNegotiatingViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(mockContentNegotiationManager);

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ContentNegotiatingViewResolver);
    }

    @Test
    void testTemplateResolver() {
        // Act
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        // Assert
        assertNotNull(resolver);
        assertEquals("templates/", resolver.getPrefix());
        assertEquals(".html", resolver.getSuffix());
        assertEquals("HTML", resolver.getTemplateMode());
        assertEquals("UTF-8", resolver.getCharacterEncoding());
        assertFalse(resolver.isCacheable());
    }

    @Test
    void testTemplateEngine() {
        // Act
        SpringTemplateEngine engine = webAppConfig.templateEngine();

        // Assert
        assertNotNull(engine);
        assertNotNull(engine.getTemplateResolvers());
    }

    @Test
    void testTemplateEngineWithResolver() {
        // Arrange
        ClassLoaderTemplateResolver mockResolver = mock(ClassLoaderTemplateResolver.class);

        // Act
        TemplateEngine engine = webAppConfig.templateEngine(mockResolver);

        // Assert
        assertNotNull(engine);
        assertTrue(engine instanceof SpringTemplateEngine);
    }

    @Test
    void testViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.viewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ThymeleafViewResolver);
        ThymeleafViewResolver thymeleafResolver = (ThymeleafViewResolver) resolver;
        assertEquals("UTF-8", thymeleafResolver.getCharacterEncoding());
    }

    @Test
    void testExcelViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.excelViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ExcelViewResolver);
    }

    @Test
    void testCsvViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.csvViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof CsvViewResolver);
    }

    @Test
    void testPdfViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.pdfViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof PdfViewResolver);
    }

    @Test
    void testConfigureContentNegotiationWithNullConfigurer() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            webAppConfig.configureContentNegotiation(null);
        });
    }

    @Test
    void testAddViewControllersWithNullRegistry() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            webAppConfig.addViewControllers(null);
        });
    }

    @Test
    void testContentNegotiatingViewResolverWithNullManager() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(null);
            assertNotNull(resolver);
        });
    }

    @Test
    void testTemplateResolverConfiguration() {
        // Act
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        // Assert
        assertNotNull(resolver.getPrefix());
        assertNotNull(resolver.getSuffix());
        assertNotNull(resolver.getTemplateMode());
        assertNotNull(resolver.getCharacterEncoding());
        assertFalse(resolver.isCacheable());
    }

    @Test
    void testAllViewResolversAreCreated() {
        // Act
        ViewResolver excelResolver = webAppConfig.excelViewResolver();
        ViewResolver csvResolver = webAppConfig.csvViewResolver();
        ViewResolver pdfResolver = webAppConfig.pdfViewResolver();
        ViewResolver thymeleafResolver = webAppConfig.viewResolver();

        // Assert
        assertNotNull(excelResolver);
        assertNotNull(csvResolver);
        assertNotNull(pdfResolver);
        assertNotNull(thymeleafResolver);

        // Verify types
        assertTrue(excelResolver instanceof ExcelViewResolver);
        assertTrue(csvResolver instanceof CsvViewResolver);
        assertTrue(pdfResolver instanceof PdfViewResolver);
        assertTrue(thymeleafResolver instanceof ThymeleafViewResolver);
    }

    @Test
    void testWebAppConfigImplementsWebMvcConfigurer() {
        // Assert
        assertTrue(webAppConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer);
    }
}