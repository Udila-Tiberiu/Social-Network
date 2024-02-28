package com.example.lab7bun.service;

import com.example.lab7bun.domain.Mesaj;

import java.util.Comparator;

public class MesajComparator implements Comparator<Mesaj> {
    @Override
    public int compare(Mesaj message1, Mesaj message2) {
        return message1.getData().compareTo(message2.getData());
    }
}