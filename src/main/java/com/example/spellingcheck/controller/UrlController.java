package com.example.spellingcheck.controller;

import com.example.spellingcheck.exception.CustomException;
import com.example.spellingcheck.exception.ExceptionCode;
import com.example.spellingcheck.model.dto.UrlDto;
import com.example.spellingcheck.model.dto.UserDto;
import com.example.spellingcheck.model.entity.Url;
import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.model.requests.UrlFilterRequest;
import com.example.spellingcheck.model.requests.UrlRequest;
import com.example.spellingcheck.model.responses.BaseResponse;
import com.example.spellingcheck.model.responses.UrlsResponse;
import com.example.spellingcheck.repository.UrlRepository;
import com.example.spellingcheck.service.IAuthService;
import com.example.spellingcheck.service.IUrlService;
import com.example.spellingcheck.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/url")
@AllArgsConstructor
@Slf4j
public class UrlController {
    private IUrlService urlService;
    private IAuthService authService;
    private UrlRepository urlRepository;
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<?> createUrl(@RequestBody UrlRequest urlRequest) {
        UrlDto urlDto = urlService.create(urlRequest);
        try {
            Document doc = Jsoup.connect(urlRequest.getUrl()).get();
            String fethUrl = "https://hoctap.coccoc.com/composer/spellchecker";

            // Lấy nội dung văn bản từ HTML
            Element body = doc.body();
            StringBuilder textBuilder = new StringBuilder();
            for (Element element : body.getAllElements()) {
                String elementText = element.ownText();
                if (!elementText.isEmpty()) {
                    textBuilder.append(elementText).append(" ");
                }
            }
            String text = textBuilder.toString().trim();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestString = String.format("{\"params\":{\"text\":\"%s\"}}",text.replace("\"", "'"));
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestString,headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(fethUrl, requestEntity, String.class);
            urlDto.setText(text);
            urlDto.setRepair(responseEntity.getBody());
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            urlDto.setText(exception.getMessage());
        }

        return ResponseEntity.ok(BaseResponse.of(urlDto));
    }

    @GetMapping
    public ResponseEntity<?> getAllUrlByUser(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                     @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
                                             @RequestParam(value = "sortBy", defaultValue = Constants.SORT_BY_TIME) String sortBy,
                                             @RequestParam(value = "sortDir", defaultValue = Constants.SORT_DIRECTION_ASC) String sortDir,
                                             @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        UrlFilterRequest filterRequest = UrlFilterRequest.builder()
                .sortBy(sortBy)
                .sortDir(sortDir)
                .keyword(keyword)
                .build();
        return ResponseEntity.ok(BaseResponse.of(urlService.getAll(page, size, filterRequest)));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUrl(@RequestBody UrlRequest urlRequest) {
        User user = authService.getUser();
        Optional<Url> url = urlRepository.findByUrlAndUserId(urlRequest.getUrl(), user.getId());
        if (url.isPresent()) {
            urlRepository.deleteById(url.get().getId());
            return ResponseEntity.ok(BaseResponse.of(null));
        } else {
            throw new CustomException(ExceptionCode.URL_NOT_FOUND);
        }
    }
}
