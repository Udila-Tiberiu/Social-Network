package com.example.lab7bun.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}