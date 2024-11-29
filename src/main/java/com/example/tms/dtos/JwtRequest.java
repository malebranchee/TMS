package com.example.tms.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "JWT token request body. Payload that will be sent to server.")
@Data
@AllArgsConstructor
public class JwtRequest {

    @Schema(description = "Login that will be sent to server", nullable = false, example = "paho@mail.ru", defaultValue = "paho@mail.ru")
    @NotBlank(message = "Field login can't be empty or blanked!")
    private String login;

    @Schema(description = "Password that will be sent to server", nullable = false, example = "123", defaultValue = "123")
    @NotBlank(message = "Field password can't be empty or blanked!")
    private String password;
}
