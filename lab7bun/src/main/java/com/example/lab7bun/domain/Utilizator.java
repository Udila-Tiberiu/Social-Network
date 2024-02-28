package com.example.lab7bun.domain;

import java.util.Objects;

import static com.example.lab7bun.domain.Parola.*;

public class Utilizator extends Entity<Long>{
    private String nume;
    private String prenume;
    private String parola;
    private static long idCounter = 0;

    public Utilizator(String nume,String prenume){
        this.setId(selfid());
        this.nume=nume;
        this.prenume=prenume;
        this.parola = hashPassword(this.getId().toString(), generateSaltPentruId(this.getId()));
        //this.parola = hashPassword(this.getId().toString(), generateSaltPentruId(this.getId()));
    }

    private long selfid(){
        return idCounter++;
    }

    public void setId(long id){
        this.id=id;
    }

    public String getNume() {
        return nume;
    }

    public String getParola(){ return parola;}

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Utilizator that = (Utilizator) o;
        return Objects.equals(nume, that.nume) && Objects.equals(prenume, that.prenume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nume, prenume);
    }

    @Override
    public String toString(){
        return "Utilizator: "+this.getId()+",nume: "+this.getNume()+",prenume: "+this.getPrenume() + "  Parola: " + this.getParola();
    }
}