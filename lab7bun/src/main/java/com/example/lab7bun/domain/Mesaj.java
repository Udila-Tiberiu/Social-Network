package com.example.lab7bun.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Mesaj extends Entity<Long>{
    private String text;
    private Utilizator from;
    private Utilizator to;
    private LocalDateTime data;
    private Long reply;
    private static long idCounter = 0;

    public Mesaj(String text, Utilizator from, Utilizator to,Long reply){
        this.setId(selfid());
        this.text=text;
        this.from=from;
        this.to=to;
        this.data=LocalDateTime.now().withNano(0);
        this.reply=reply;
    }

    public Mesaj(String text, Utilizator from, Utilizator to,LocalDateTime data,Long reply){
        this.setId(selfid());
        this.text=text;
        this.from=from;
        this.to=to;
        this.data=data;
        this.reply=reply;
    }

    public Mesaj(String textmesaj, Optional<Utilizator> u1, Optional<Utilizator> u2, LocalDateTime din, Long reply) {
        if (u1.isPresent() && u2.isPresent()) {
            this.setId(selfid());
            this.text=textmesaj;
            this.from=u1.get();
            this.to=u2.get();
            this.data=din;
            this.reply=reply;
        }
    }

    private long selfid(){
        return idCounter++;
    }

    public void setId(long id){
        this.id=id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public Utilizator getTo() {
        return to;
    }

    public void setTo(Utilizator to) {
        this.to = to;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Mesaj mesaj = (Mesaj) o;
        return Objects.equals(text, mesaj.text) && Objects.equals(from, mesaj.from) && Objects.equals(to, mesaj.to) && Objects.equals(data, mesaj.data) && Objects.equals(reply, mesaj.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, from, to, data, reply);
    }

    @Override
    public String toString() {
        return "Mesaj: " + id +
                ", text= '" + text + '\'' +
                " , from=" + from.getNume()+" "+from.getPrenume() +
                ", to=" + to.getNume()+" "+to.getPrenume() +
                ", data=" + data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                ", reply mesajului:=" + reply;
    }
}