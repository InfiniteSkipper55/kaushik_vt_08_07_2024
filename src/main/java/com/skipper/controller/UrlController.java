package com.skipper.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.skipper.service.UrlService;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UrlController {
    
    private final UrlService service;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestParam String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Original URL is required");
        }
        try {
            String shortUrl = service.shortenUrl(originalUrl);
            return new ResponseEntity<>(shortUrl, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to shorten URL", e);
        }
    }

    @GetMapping("/{shortenString}")
    public void redirectToFullUrl(HttpServletResponse response, @PathVariable String shortenString) {
        try {
            String fullUrl = service.getOriginalUrl(shortenString)
                    .orElseThrow(NoSuchElementException::new);
            response.sendRedirect(fullUrl);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not redirect to the full url", e);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> updateShortUrl(@RequestParam String shortUrl,
                                                   @RequestParam(required = false) String newOriginalUrl) {
        if (newOriginalUrl == null || newOriginalUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New original URL is required");
        }
        boolean updated;
        try {
            updated = service.updateShortUrl(shortUrl, newOriginalUrl);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL", e);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/update-expiry")
    public ResponseEntity<Boolean> updateExpiry(@RequestParam String shortUrl,
                                                 @RequestParam int daysToAdd) {
        if (shortUrl == null || daysToAdd < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters");
        }
        try {
            boolean updated = service.updateExpiry(shortUrl, daysToAdd);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL", e);
        }
    }
}
