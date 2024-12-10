package com.example.tms.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Refresh JWT request data class
 */
@Data
@AllArgsConstructor
public class RefreshTokenDto {
    @NotBlank
    private String token;
}
