package com.example.lab7bun.algoritm;

import com.example.lab7bun.domain.Utilizator;

public class ElementGraf extends EntiateGraf<Integer>{
    public Utilizator element;
    public ElementGraf(Utilizator element){
        this.element=element;
    }
    public Utilizator getElement(){
        return this.element;
    }
}
