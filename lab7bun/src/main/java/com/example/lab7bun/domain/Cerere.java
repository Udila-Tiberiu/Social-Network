package com.example.lab7bun.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Cerere extends Entity<Long> {
    private Utilizator utilizator1;
    private Utilizator utilizator2;
    private Status status;
    private static long idCounter = 0;

    public Cerere(Utilizator utilizator1, Utilizator utilizator2, Status status) {
        this.setId(selfid());
        this.utilizator1 = utilizator1;
        this.utilizator2 = utilizator2;
        this.status=status;
    }

    public Cerere(Optional<Utilizator> u1, Optional<Utilizator> u2, Status status) {
        if (u1.isPresent() && u2.isPresent()) {
            this.utilizator1 = u1.get();
            this.utilizator2 = u2.get();
            this.status=status;
            this.setId(selfid());
        }
    }

    private long selfid() {
        return idCounter++;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Utilizator getUtilizator1() {
        return utilizator1;
    }

    public void setUtilizator1(Utilizator utilizator1) {
        this.utilizator1 = utilizator1;
    }

    public Utilizator getUtilizator2() {
        return utilizator2;
    }

    public void setUtilizator2(Utilizator utilizator2) {
        this.utilizator2 = utilizator2;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cerere cerere = (Cerere) o;
        return Objects.equals(utilizator1, cerere.utilizator1) && Objects.equals(utilizator2, cerere.utilizator2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), utilizator1, utilizator2);
    }

    public String toString(){
        return "Prietenie: id="+id+", utilizatori: "+utilizator1.getNume()+" "+utilizator1.getPrenume()+", "+utilizator2.getNume()
                +" "+utilizator2.getPrenume()+", status: "+status;
    }
}
