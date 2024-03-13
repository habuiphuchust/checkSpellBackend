package com.example.spellingcheck.service;

import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.model.requests.LoginRequest;
import com.example.spellingcheck.model.requests.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<?> login(LoginRequest request);

    User getUser() throws RuntimeException;
}
