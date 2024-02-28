package com.example.lab7bun.algoritm;

import java.io.Serializable;

public class EntiateGraf <ID> implements Serializable {
    private static final long serialVersionUID = 0L;
    private ID id;
    public ID getIdGraf(){
        return id;
    }
    public void setIdGraf(ID id){
        this.id=id;
    }
}
