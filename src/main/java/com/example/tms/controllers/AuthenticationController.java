package com.example.tms.controllers;

import com.example.tms.dtos.JwtRequest;
import com.example.tms.dtos.RefreshTokenRequest;
import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final AuthService authService;
    private final EmailValidator emailValidator;

    @PostMapping("/auth")
    public ResponseEntity<?> authorizeUser(@RequestBody @Validated JwtRequest authRequest)
    {

        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Validated RefreshTokenRequest refreshTokenRequest)
    {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Validated RegistrationUserDto registrationUserDto)
    {
        return authService.createNewUser(registrationUserDto);
    }

}
