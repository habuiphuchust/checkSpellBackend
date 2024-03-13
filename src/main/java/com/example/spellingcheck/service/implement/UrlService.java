package com.example.spellingcheck.service.implement;

import com.example.spellingcheck.model.dto.PageUrlDto;
import com.example.spellingcheck.model.dto.UrlDto;
import com.example.spellingcheck.model.entity.Url;
import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.model.requests.UrlFilterRequest;
import com.example.spellingcheck.model.requests.UrlRequest;
import com.example.spellingcheck.repository.UrlRepository;
import com.example.spellingcheck.service.IUrlService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UrlService implements IUrlService {
    private UrlRepository urlRepository;
    private AuthService authService;
    private ModelMapper modelMapper;
    @Override
    public UrlDto create(UrlRequest urlRequest) throws RuntimeException {
        User user = authService.getUser();
        log.debug(user.toString());
        Url url = Url.builder()
                .url(urlRequest.getUrl())
                .user(user)
                .time(LocalDateTime.now())
                .build();
        Optional<Url> oldUrl = urlRepository.findByUrlAndUserId(urlRequest.getUrl(), user.getId());
        if (oldUrl.isEmpty()) {
            urlRepository.save(url);
            return modelMapper.map(url, UrlDto.class);
        } else {
            Url newUrl = oldUrl.get();
            newUrl.setTime(LocalDateTime.now());
            urlRepository.save(newUrl);
            return modelMapper.map(newUrl, UrlDto.class);
        }
    }

    @Override
    public List<UrlDto> getAll(Long userId) {
        return urlRepository.getAllByUserId(userId)
                .stream()
                .map(url -> modelMapper.map(url, UrlDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageUrlDto getAll(int pageNumber, int pageSize, UrlFilterRequest filterRequest) {
        String sortBy = filterRequest.getSortBy();
        String sortDir = filterRequest.getSortDir();
        String keyword = "%"+ filterRequest.getKeyword() + "%";
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<Url> urlPage = urlRepository.getAllByUserId(authService.getUser().getId(),keyword, pageRequest);
        List<Url> urlList = urlPage.getContent();

        PageUrlDto pageUrlDto = new PageUrlDto();
        pageUrlDto.setUrls(urlList.stream().map(this::mapToDTO).toList());
        pageUrlDto.setCurrentPage(pageNumber);
        pageUrlDto.setPageSize(pageSize);
        pageUrlDto.setTotalPage(urlPage.getTotalPages());
        pageUrlDto.setTotalElement((int) urlPage.getTotalElements());

        return pageUrlDto;
    }

    private UrlDto mapToDTO(Url url) {
        return modelMapper.map(url, UrlDto.class);
    }
}
