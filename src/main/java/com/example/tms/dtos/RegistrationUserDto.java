package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RegistrationUserDto {

    @NotBlank(message = "Login field could not be empty or blanked!")
    @Size(min = 4,  message = "Minimal login length - 4 characters")
    private String login;

    @NotBlank(message = "Nickname field could not be empty or blanked!")
    @Size(min = 4, message = "Minimal nickname length - 4 characters")
    private String nickname;

    @NotBlank(message = "Password field could not be empty or blanked!")
    @Size(min = 3,  message = "Minimal password length - 3 characters")
    private String password;

    @NotBlank(message = "Password confirm field could not be empty or blanked!")
    private String confirmPassword;

}
