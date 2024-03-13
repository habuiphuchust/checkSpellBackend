package com.example.spellingcheck.model.responses;

import com.example.spellingcheck.model.dto.UrlDto;
import com.example.spellingcheck.model.dto.UserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UrlsResponse {
    private UserDto user;
    private List<UrlDto> urls;
}
