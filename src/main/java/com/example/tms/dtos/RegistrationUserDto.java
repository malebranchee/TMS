package com.example.tms.dtos;

import com.example.tms.exceptions.validators.ConstraintNicknameValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Registration of user data class
 */
@Schema(description = "DTO that will be sent to the server to authorize user authorities")
@Data
@AllArgsConstructor
public class RegistrationUserDto {

    @Schema(description = "Mail that is login", example = "paho@mail.ru", defaultValue = "paho@mail.ru", nullable = false)
    @Email(message = "Wrong email")
    @NotBlank(message = "Login field could not be empty or blanked!")
    private String login;

    @Schema(description = "Nickname of new user (do not confuse with login!). Used for interaction with tasks. Used custom validator", minLength = 4, maxLength = 30, example = "pablo", defaultValue = "pablo", nullable = false)
    @NotBlank(message = "Nickname field could not be empty or blanked!")
    @Size(min = 4, max = 30, message = "Minimal nickname length - 4 characters, maximum - 15")
    @ConstraintNicknameValidator
    private String nickname;

    @Schema(description = "Password of new user", minLength = 3, maxLength = 15, example = "123", defaultValue = "123", nullable = false)
    @NotBlank(message = "Password field could not be empty or blanked!")
    @Size(min = 3, max = 15,  message = "Minimal password length - 3 characters, maximum - 15")
    private String password;

    @Schema(description = "Confirmation of entered password", defaultValue = "123", nullable = false)
    @NotBlank(message = "Password confirm field could not be empty or blanked!")
    private String confirmPassword;
}
