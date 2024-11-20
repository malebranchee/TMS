package com.example.tms.controllers;

import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController("/error")
public class HandleController {

    @GetMapping
    public String error()
    {
        return "You've got an error";
    }
}
