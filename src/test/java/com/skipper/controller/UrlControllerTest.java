package com.skipper.controller;

import com.skipper.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UrlControllerTest {

    private UrlController urlController;
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = mock(UrlService.class);
        urlController = new UrlController(urlService);
    }

    @Test
    void shortenUrl_ValidOriginalUrl_ReturnsShortUrl() {
        String originalUrl = "https://www.example.com";
        String shortUrl = "abc123";
        when(urlService.shortenUrl(originalUrl)).thenReturn(shortUrl);

        ResponseEntity<String> response = urlController.shortenUrl(originalUrl);

        assertEquals(shortUrl, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shortenUrl_NullOriginalUrl_ThrowsBadRequestException() {
        String originalUrl = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.shortenUrl(originalUrl));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Original URL is required", exception.getReason());
    }

    @Test
    void shortenUrl_EmptyOriginalUrl_ThrowsBadRequestException() {
        String originalUrl = "";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.shortenUrl(originalUrl));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Original URL is required", exception.getReason());
    }

    @Test
    void shortenUrl_ServiceException_ThrowsInternalServerErrorException() {
        String originalUrl = "https://www.example.com";
        when(urlService.shortenUrl(originalUrl)).thenThrow(new RuntimeException());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.shortenUrl(originalUrl));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("Failed to shorten URL", exception.getReason());
    }

    @Test
    public void testUpdateShortUrl_NewOriginalUrlIsNull() {
        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);

        assertThrows(ResponseStatusException.class, () -> {
            urlController.updateShortUrl("shortUrl", null);
        });
    }

    @Test
    public void testUpdateShortUrl_NewOriginalUrlIsEmpty() {
        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);

        assertThrows(ResponseStatusException.class, () -> {
            urlController.updateShortUrl("shortUrl", "");
        });
    }

    @Test
    public void testUpdateShortUrl_ServiceThrowsIllegalArgumentException() {
        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);
        when(mockService.updateShortUrl(anyString(), anyString())).thenThrow(new IllegalArgumentException());

        assertThrows(ResponseStatusException.class, () -> {
            urlController.updateShortUrl("shortUrl", "newOriginalUrl");
        });
    }

    @Test
    public void testUpdateShortUrl_SuccessfulUpdate() {
        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);
        when(mockService.updateShortUrl(anyString(), anyString())).thenReturn(true);

        ResponseEntity<Boolean> response = urlController.updateShortUrl("shortUrl", "newOriginalUrl");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void updateShortUrl_NullNewOriginalUrl_ThrowsBadRequestException() {
        String shortUrl = "shortUrl";
        String newOriginalUrl = null;

        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.updateShortUrl(shortUrl, newOriginalUrl));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("New original URL is required", exception.getReason());
    }

    @Test
    void updateShortUrl_EmptyNewOriginalUrl_ThrowsBadRequestException() {
        String shortUrl = "shortUrl";
        String newOriginalUrl = "";

        UrlService mockService = mock(UrlService.class);
        UrlController urlController = new UrlController(mockService);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.updateShortUrl(shortUrl, newOriginalUrl));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("New original URL is required", exception.getReason());
    }

    @Test
    void updateShortUrl_ValidInput_ReturnsTrue() {
        String shortUrl = "shortUrl";
        String newOriginalUrl = "newOriginalUrl";

        UrlService mockService = mock(UrlService.class);
        when(mockService.updateShortUrl(anyString(), anyString())).thenReturn(true);

        UrlController urlController = new UrlController(mockService);
        ResponseEntity<Boolean> response = urlController.updateShortUrl(shortUrl, newOriginalUrl);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void updateShortUrl_InvalidUrl_ThrowsBadRequestException() {
        String shortUrl = "shortUrl";
        String newOriginalUrl = "newOriginalUrl";

        UrlService mockService = mock(UrlService.class);
        when(mockService.updateShortUrl(anyString(), anyString())).thenThrow(IllegalArgumentException.class);

        UrlController urlController = new UrlController(mockService);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> urlController.updateShortUrl(shortUrl, newOriginalUrl));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid URL", exception.getReason());
    }

    @Test
    public void testUpdateExpiry_ValidInput_ReturnsOk() {
        // Mock service for updateExpiry method
        UrlService service = mock(UrlService.class);
        when(service.updateExpiry(anyString(), anyInt())).thenReturn(true);

        // Create UrlController instance
        UrlController urlController = new UrlController(service);

        // Test valid input parameters
        ResponseEntity<Boolean> response = urlController.updateExpiry("testShortUrl", 5);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    public void testUpdateExpiry_ShortUrlIsNull_ThrowsBadRequestException() {
        UrlService service = mock(UrlService.class);
        UrlController urlController = new UrlController(service);

        assertThrows(ResponseStatusException.class, () -> urlController.updateExpiry(null, 5));
    }

    @Test
    public void testUpdateExpiry_DaysToAddNegative_ThrowsBadRequestException() {
        UrlService service = mock(UrlService.class);
        UrlController urlController = new UrlController(service);

        assertThrows(ResponseStatusException.class, () -> urlController.updateExpiry("testShortUrl", -1));
    }

    @Test
    public void testUpdateExpiry_InvalidUrlException_ThrowsBadRequestException() {
        UrlService service = mock(UrlService.class);
        when(service.updateExpiry(anyString(), anyInt())).thenThrow(new IllegalArgumentException("Invalid URL"));
        UrlController urlController = new UrlController(service);

        assertThrows(ResponseStatusException.class, () -> urlController.updateExpiry("testShortUrl", 5));
    }


    
}