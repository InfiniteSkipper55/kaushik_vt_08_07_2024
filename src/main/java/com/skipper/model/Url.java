package com.skipper.model;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;

    private String shortUrl;

    private LocalDateTime expiryDate;

    
}