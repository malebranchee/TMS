package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {

    interface login{}

    @NotBlank(message = "Field login can't be empty or blanked!")
    @Size(min = 4, groups = login.class)
    private String login;


    @NotBlank(message = "Field password can't be empty or blanked!")
    private String password;
}
