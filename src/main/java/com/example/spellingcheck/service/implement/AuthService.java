package com.example.spellingcheck.service.implement;

import com.example.spellingcheck.authentication.custom.CustomUserDetails;
import com.example.spellingcheck.authentication.jwt.JwtTokenProvider;
import com.example.spellingcheck.exception.CustomException;
import com.example.spellingcheck.exception.ExceptionCode;
import com.example.spellingcheck.model.entity.Role;
import com.example.spellingcheck.model.entity.User;
import com.example.spellingcheck.model.requests.LoginRequest;
import com.example.spellingcheck.model.requests.RegisterRequest;
import com.example.spellingcheck.model.responses.AuthenticationResponse;
import com.example.spellingcheck.model.responses.BaseResponse;
import com.example.spellingcheck.repository.RoleRepository;
import com.example.spellingcheck.repository.UserRepository;
import com.example.spellingcheck.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ExceptionCode.USERNAME_ALREADY_EXIST);
        }
        Role role = roleRepository.findByName("ROLE_USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleSet)
                .fullName(request.getFullName())
                .build();
        userRepository.save(newUser);

        String msg = "Register sucessfully";
        return ResponseEntity.ok(BaseResponse.of(msg));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            roles.add(authority);
        }
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .roles(roles)
                .token(jwtTokenProvider.generateToken(authentication))
                .username(authentication.getName())
                .fullName(((CustomUserDetails)authentication.getPrincipal()).getUser().getFullName())
                .build();

        return ResponseEntity.ok(BaseResponse.of(authenticationResponse));
    }

    @Override
    public User getUser() throws RuntimeException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUser();
        }
        throw new CustomException(ExceptionCode.UNAUTHORIZED);
    }
}
