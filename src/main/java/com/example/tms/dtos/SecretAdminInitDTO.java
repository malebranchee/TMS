package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Secret class
 * @deprecated Marked for removal in production
 */
@AllArgsConstructor
@Data
public class SecretAdminInitDTO {
    private Long id;
    private String login;
    private String nickname;
    private String role;
    private String access_token;
    public SecretAdminInitDTO(){}
}
