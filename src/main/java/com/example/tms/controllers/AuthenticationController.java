package com.example.tms.controllers;

import com.example.tms.dtos.JwtRequest;
import com.example.tms.dtos.RefreshTokenDto;
import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Authentication&Registration controller", description = "Provides access to registration and authentication methods")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final AuthService authService;
    private final EmailValidator emailValidator;

    @Operation(summary = "Authentication method", description = "Returns access token in response body")
    @PostMapping("/auth")
    public ResponseEntity<?> authorizeUser(
            @Parameter(name = "Authentication request", description = "Enter your credentials")
            @RequestBody
            @Validated
            JwtRequest authRequest)
    {

        return authService.createAuthToken(authRequest);
    }

    @Operation(summary = "Refreshing token method", description = "Refreshes access token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Parameter(name = "URI for changing tokens. NOT FUNCTIONAL! Added for future.")
            @RequestBody
            @Validated
            RefreshTokenDto refreshTokenRequest)
    {
        return authService.refreshToken(refreshTokenRequest);
    }

    @Operation(summary = "User registration method", description = "Creates new user with role USER")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(
            @RequestBody
            @Validated
            RegistrationUserDto registrationUserDto)
    {
        return authService.createNewUser(registrationUserDto);
    }

}
