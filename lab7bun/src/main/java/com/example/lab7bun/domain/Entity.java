package com.example.lab7bun.domain;

import java.util.Objects;

public class Entity<ID>{

    // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}