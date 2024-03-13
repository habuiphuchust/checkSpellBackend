package com.example.spellingcheck.controller;

import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.model.responses.BaseResponse;
import com.example.spellingcheck.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@EnableMethodSecurity(prePostEnabled = true)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody BaseResponse getAllUsers() {
        // This returns a JSON or XML with the users
        return BaseResponse.of(userRepository.findAll());
    }

}
