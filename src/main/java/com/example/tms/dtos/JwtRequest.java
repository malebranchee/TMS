package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {

    interface login{}

    @NotBlank
    @Size(min = 4, groups = login.class, message = "Min length = 4")
    private String username;

    @Size(min = 2, groups = login.class)
    @NotBlank
    private String password;
}
