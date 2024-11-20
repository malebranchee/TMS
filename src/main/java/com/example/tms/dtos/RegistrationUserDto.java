package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegistrationUserDto implements Serializable {
    private String login;
    private String password;
    private String confirmPassword;
}
