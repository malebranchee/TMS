package com.example.tms.controllers;

import com.example.tms.dtos.RegistrationDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @PostMapping("/registration")
    public String getMessage(@RequestBody RegistrationDto dto)
    {
        //System.out.printf("%s, %s, %s", dto.getUsername(), dto.getPassword(), dto.getConfirmPassword());
        return "Hello, from Docker!";
    }

}
