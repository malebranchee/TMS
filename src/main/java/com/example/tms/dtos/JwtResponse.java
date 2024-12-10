package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT response data class
 */
@Data
@AllArgsConstructor
public class JwtResponse {

    private String access_token;

    private String refresh_token;



}
