package com.example.spellingcheck.model.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlFilterRequest {
    private String sortBy;
    private String sortDir;
    private String keyword;
}
