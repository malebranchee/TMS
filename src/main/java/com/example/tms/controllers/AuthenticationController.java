package com.example.tms.controllers;

import com.example.tms.dtos.JwtRequest;
import com.example.tms.dtos.RefreshTokenRequest;
import com.example.tms.services.AuthService;
import com.example.tms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> authorizeUser(@RequestBody JwtRequest authRequest, HttpServletRequest request)
    {
        return authService.createAuthToken(authRequest, request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        return authService.refreshToken(refreshTokenRequest);
    }



    @PostMapping("/confirmed{login}")
    public String mailConfirm(@PathVariable String login)
    {
        if (userService.ifUserNotExists(login))
            return "redirect:/error";

        userService.addRole("ROLE_CONFIRMED", login);
        userService.deleteRole("ROLE_UNCONFIRMED", login);
        return "redirect:/api/auth/panel";
    }
}
