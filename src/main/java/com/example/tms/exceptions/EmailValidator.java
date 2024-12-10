package com.example.tms.exceptions;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Additional validator for email
 */
@Component
public class EmailValidator {
    @Bean
    public org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator EmailValidatorBean()
    {
        return new org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator();
    }
}
