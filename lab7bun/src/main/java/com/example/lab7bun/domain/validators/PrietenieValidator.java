package com.example.lab7bun.domain.validators;

import com.example.lab7bun.domain.Prietenie;

import java.util.Objects;

public class PrietenieValidator implements Validator<Prietenie>{
    public PrietenieValidator() {
    }

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity == null){
            throw new ValidationException("Prietenia nu poate fi null!");
        }
        if(Objects.equals(entity.getUtilizator1(), null)){
            throw new ValidationException("Utilizatorul 1 nu poate fi null!");
        }
        if(Objects.equals(entity.getUtilizator2(), null)){
            throw new ValidationException("Utilizatorul 2 nu poate fi null!");
        }
        if(entity.getId()==null){
            throw new ValidationException("Id-ul nu poate fi negativ sau null!");
        }
    }
}