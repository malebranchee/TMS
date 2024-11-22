package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegistrationUserDto {
    /*public interface registration{}
    public interface login{}

    @NotBlank(groups = {registration.class, login.class}, message = "Поле имени не может быть пустым")
    @Size(min = 4, groups = {registration.class}, message = "Логин должен содержать минимум 4 символа")
    private String login;

    @NotBlank(groups = {registration.class, login.class}, message = "Поле пароля не может быть пустым")
    @Size(min = 2, groups = {registration.class, login.class},  message = "Минимум 2 символа в пароле!")
    private String password;

    @NotBlank(groups = {registration.class}, message = "")
    private String confirmPassword;*/

    private String login;
    private String password;
    private String confirmPassword;
}
