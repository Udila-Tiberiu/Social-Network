package com.example.lab7bun.domain.validators;

import com.example.lab7bun.domain.Mesaj;

public class MesajValidator implements Validator<Mesaj> {

    public MesajValidator() {
    }

    @Override
    public void validate(Mesaj entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Mesajul nu poate fi null!");
        }
        if (entity.getTo() == null || entity.getFrom()==null) {
            throw new ValidationException("Utilizatorii nu pot fi null!");
        }
        if (entity.getText() == null) {
            throw new ValidationException("Textul mesajului nu poate fi null!");
        }
        if(entity.getTo()==entity.getFrom()){
            throw new ValidationException("Utilizatorii nu pot fi aceeasi!");
        }
    }
}