package com.example.tms.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RegistrationUserDto {

    @Email(message = "Wrong email")
    @NotBlank(message = "Login field could not be empty or blanked!")
    private String login;

    @NotBlank(message = "Nickname field could not be empty or blanked!")
    @Size(min = 4, max = 30, message = "Minimal nickname length - 4 characters, maximum - 15")
    private String nickname;

    @NotBlank(message = "Password field could not be empty or blanked!")
    @Size(min = 3, max = 15,  message = "Minimal password length - 3 characters, maximum - 15")
    private String password;

    @NotBlank(message = "Password confirm field could not be empty or blanked!")
    private String confirmPassword;

}
