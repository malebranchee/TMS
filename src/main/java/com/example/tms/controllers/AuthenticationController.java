package com.example.tms.controllers;

import com.example.tms.dtos.JwtRequest;
import com.example.tms.dtos.RefreshTokenRequest;
import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.services.AuthService;
import com.example.tms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<?> authorizeUser(@RequestBody JwtRequest authRequest, HttpServletRequest request)
    {
        return authService.createAuthToken(authRequest, request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody  RegistrationUserDto registrationUserDto, HttpServletRequest request)
    {
        return authService.createNewUser(registrationUserDto, request);
    }

}
