package com.example.tms.exceptions.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<ConstraintNicknameValidator, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains("@$%^&*#!+");
    }

    @Override
    public void initialize(ConstraintNicknameValidator validator)
    {

    }
}
