package com.example.lab7bun.domain.validators;

import com.example.lab7bun.domain.Utilizator;

import java.util.Objects;

public class UtilizatorValidator implements Validator<Utilizator> {
    public UtilizatorValidator() {
    }

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if (entity == null){
            throw new ValidationException("Utilizatorul nu poate fi null!");
        }
        if(Objects.equals(entity.getPrenume(), "")){
            throw new ValidationException("Prenumele nu poate fi null!");
        }
        if(Objects.equals(entity.getNume(), "")){
            throw new ValidationException("Numele nu poate fi null!");
        }
        if(entity.getId()<0 || entity.getId()==null){
            throw new ValidationException("Id-ul nu poate fi negativ sau null!");
        }
    }
}
