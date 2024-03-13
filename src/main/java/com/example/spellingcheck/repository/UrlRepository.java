package com.example.spellingcheck.repository;

import com.example.spellingcheck.model.entity.Url;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


public interface UrlRepository extends JpaRepository<Url, Long> {
    List<Url>  getAllByUserId(Long userId);
    Optional<Url> findByUrlAndUserId(String url, Long userId);
    @Query("SELECT u FROM Url u WHERE u.user.id=?1 AND LOWER(u.url) LIKE LOWER(?2)")
    Page<Url> getAllByUserId(Long userId, String keyword, Pageable pageable);
}
