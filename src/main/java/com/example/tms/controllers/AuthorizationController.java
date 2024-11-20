package com.example.tms.controllers;

import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.services.AuthService;
import com.example.tms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class AuthorizationController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> registration(@RequestBody @Validated(RegistrationUserDto.registration.class) RegistrationUserDto registrationUserDto, HttpServletRequest request)
    {
        ResponseEntity<?> l = authService.createNewUser(registrationUserDto, request);
        return l;
    }


}
