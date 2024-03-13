package com.example.spellingcheck.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlDto {
    private Long id;
    private String url;
    private LocalDateTime time;
    private String text;
    private String repair;
}
