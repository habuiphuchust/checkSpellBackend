package com.example.spellingcheck.controller;

import com.example.spellingcheck.exception.ValidationException;
import com.example.spellingcheck.model.requests.LoginRequest;
import com.example.spellingcheck.model.requests.RegisterRequest;
import com.example.spellingcheck.service.IAuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
@AllArgsConstructor
public class AuthController {
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new ValidationException(bindingResult);
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new ValidationException(bindingResult);
        return authService.register(request);
    }
}
