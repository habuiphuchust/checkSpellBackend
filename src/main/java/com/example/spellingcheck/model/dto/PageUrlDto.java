package com.example.spellingcheck.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageUrlDto {
    private List<UrlDto> urls;
    private int totalPage;
    private int totalElement;
    private int currentPage;
    private int pageSize;
}
