package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String login;
    private String nickname;
}
