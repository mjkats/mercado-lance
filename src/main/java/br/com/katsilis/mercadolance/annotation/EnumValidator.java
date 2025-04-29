package br.com.katsilis.mercadolance.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        // Used only for initialization without logic
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Enum.valueOf(value.getDeclaringClass(), value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}