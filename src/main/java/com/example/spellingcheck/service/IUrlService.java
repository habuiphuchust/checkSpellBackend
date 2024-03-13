package com.example.spellingcheck.service;

import com.example.spellingcheck.model.dto.PageUrlDto;
import com.example.spellingcheck.model.dto.UrlDto;
import com.example.spellingcheck.model.entity.Url;
import com.example.spellingcheck.model.requests.UrlFilterRequest;
import com.example.spellingcheck.model.requests.UrlRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUrlService {
    UrlDto create(UrlRequest urlRequest) throws RuntimeException;
    List<UrlDto> getAll(Long userId);

    PageUrlDto getAll(int pageNumber, int pageSize, UrlFilterRequest filterRequest);
}
