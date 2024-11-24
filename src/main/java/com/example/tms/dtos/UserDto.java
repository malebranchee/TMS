package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDto implements Serializable {
    interface user{}

    private Long id;
    private String login;
    private String nickname;
}
