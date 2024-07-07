package com.skipper.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.skipper.model.Url;
import com.skipper.repository.UrlRepository;

import lombok.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    
    private final UrlRepository repository;

    public String shortenUrl(String originalUrl) {
        if (originalUrl == null) {
            throw new IllegalArgumentException("Original URL cannot be null");
        }

        String shortUrl = generateShortUrl();
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setExpiryDate(LocalDateTime.now().plusMonths(10));
        repository.save(url);
        return shortUrl;
    }

    public Optional<String> getOriginalUrl(String shortUrl) {
        Optional<Url> urlOpt = repository.findByShortUrl(shortUrl);
        return urlOpt.isPresent() ? Optional.ofNullable(urlOpt.get().getOriginalUrl()) : Optional.empty();
    }

    public boolean updateShortUrl(String shortUrl, String newOriginalUrl) {
        if (shortUrl == null || newOriginalUrl == null) {
            throw new IllegalArgumentException("Short URL or new original URL cannot be null");
        }

        Optional<Url> UrlOpt = repository.findByShortUrl(shortUrl);
        if (UrlOpt.isPresent()) {
            Url Url = UrlOpt.get();
            Url.setOriginalUrl(newOriginalUrl);
            repository.save(Url);
            return true;
        }
        return false;
    }

    public boolean updateExpiry(String shortUrl, int daysToAdd) {
        if (shortUrl == null) {
            throw new IllegalArgumentException("Short URL cannot be null");
        }

        Optional<Url> UrlOpt = repository.findByShortUrl(shortUrl);
        if (UrlOpt.isPresent()) {
            Url Url = UrlOpt.get();
            if (daysToAdd < 0) {
                throw new IllegalArgumentException("Number of days to add must be non-negative");
            }
            Url.setExpiryDate(Url.getExpiryDate().plusDays(daysToAdd));
            repository.save(Url);
            return true;
        }
        return false;
    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize SecureRandom", e);
        }
        StringBuilder shortUrl = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            int randomIndex = random.nextInt(characters.length());
            if (randomIndex >= 0 && randomIndex < characters.length()) {
                shortUrl.append(characters.charAt(randomIndex));
            }
        }
        if (shortUrl.length() != 30) {
            throw new RuntimeException("Failed to generate a unique short URL");
        }
        return shortUrl.toString();
    }
    
}
