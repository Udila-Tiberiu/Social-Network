package com.example.lab7bun.domain;

import java.util.Objects;
import java.time.LocalDateTime;
import java.util.Optional;

public class Prietenie extends Entity<Long> {
    private Utilizator utilizator1;
    private Utilizator utilizator2;
    private LocalDateTime data;
    private static long idCounter = 0;

    public Prietenie(Utilizator utilizator1, Utilizator utilizator2) {
        this.setId(selfid());
        this.utilizator1 = utilizator1;
        this.utilizator2 = utilizator2;
        this.data = LocalDateTime.now().withNano(0);
    }

    public Prietenie(Utilizator utilizator1, Utilizator utilizator2, LocalDateTime data) {
        this.setId(selfid());
        this.utilizator1 = utilizator1;
        this.utilizator2 = utilizator2;
        this.data = data;
    }

    public Prietenie(Optional<Utilizator> u1, Optional<Utilizator> u2, LocalDateTime din) {
        if (u1.isPresent() && u2.isPresent()) {
            this.utilizator1 = u1.get();
            this.utilizator2 = u2.get();
            this.data = din;
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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Prietenie prietenie = (Prietenie) o;
        return Objects.equals(utilizator1, prietenie.utilizator1) && Objects.equals(utilizator2, prietenie.utilizator2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), utilizator1, utilizator2);
    }

    @Override
    public String toString(){
        return "Prietenie: id="+id+", utilizatori:"+utilizator1.getNume()+" "+utilizator1.getPrenume()+", "+utilizator2.getNume()+" "+utilizator2.getPrenume();
    }
}