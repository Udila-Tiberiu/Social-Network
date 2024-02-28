package com.example.lab7bun.service;

import com.example.lab7bun.domain.*;
import com.example.lab7bun.domain.validators.CerereValidator;
import com.example.lab7bun.domain.validators.MesajValidator;
import com.example.lab7bun.domain.validators.PrietenieValidator;
import com.example.lab7bun.domain.validators.UtilizatorValidator;
import com.example.lab7bun.repository.CerereDBRepository;
import com.example.lab7bun.repository.MesajDBRepository;
import com.example.lab7bun.repository.PrietenieDBRepository;
import com.example.lab7bun.repository.UtilizatorDBRepository;
import com.example.lab7bun.algoritm.EntiateGraf;
import com.example.lab7bun.algoritm.Graf;
import com.example.lab7bun.algoritm.ElementGraf;
import com.example.lab7bun.repository.paging.Page;
import com.example.lab7bun.repository.paging.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Service {
    private PrietenieDBRepository repoPrietenie;
    private UtilizatorDBRepository repoUser;

    private MesajDBRepository repoMsj;

    private CerereDBRepository repoCerere;

    private static Service instanta=null;

    public static synchronized Service getInstance() {
        if (instanta == null) {
            String url = "jdbc:postgresql://localhost:5432/socialnetwork";
            String username = "postgres";
            String password = "123456";

            UtilizatorDBRepository utilizatorRepo = new UtilizatorDBRepository(url, username, password, new UtilizatorValidator());
            PrietenieDBRepository prietenieRepo = new PrietenieDBRepository(url, username, password, new PrietenieValidator(), utilizatorRepo);
            MesajDBRepository msgRepo = new MesajDBRepository(url,username,password,new MesajValidator(),utilizatorRepo);
            CerereDBRepository repoCerere=new CerereDBRepository(url,username,password,new CerereValidator(),utilizatorRepo);
            instanta = new Service(utilizatorRepo, prietenieRepo, msgRepo,repoCerere);
        }
        return instanta;
    }

    public Service(UtilizatorDBRepository utilizatorRepo, PrietenieDBRepository prietenieRepo, MesajDBRepository msgRepo, CerereDBRepository repoCerere) {
        this.repoPrietenie = prietenieRepo;
        this.repoUser = utilizatorRepo;
        this.repoMsj = msgRepo;
        this.repoCerere = repoCerere;
    }

    public Iterable<Utilizator> getAllUtilizatori() {
        return repoUser.findAll();
    }

    public Iterable<Prietenie> getAllPrietenie(){
        return repoPrietenie.findAll();
    }

    public Iterable<Mesaj> getAllMesaje(){
        return repoMsj.findAll();
    }

    public Iterable<Cerere> getAllCereri(){
        return repoCerere.findAll();
    }

    public Utilizator cautaNume(String nume, String prenume) {
        for (Utilizator u : this.getAllUtilizatori()) {
            if (Objects.equals(u.getNume(), nume) && Objects.equals(u.getPrenume(), prenume)) {
                return u;
            }
        }
        return null;
    }


    public Page<Utilizator> getMoviesOnPage(Pageable pageable){
        return repoUser.findAll(pageable);
    }


    public Cerere cautaCerereNume(Utilizator u1, Utilizator u2) {
        for (Cerere c : this.getAllCereri()) {
            if (Objects.equals(c.getUtilizator1(),u1) && Objects.equals(c.getUtilizator2(),u2)) {
                return c;
            }
            if (Objects.equals(c.getUtilizator1(),u2) && Objects.equals(c.getUtilizator2(),u1)) {
                return c;
            }
        }
        return null;
    }

    public void modificaPrietenii(Utilizator utilizatorVechi, Utilizator utilizatorNou){
        List<Prietenie> updatedPrietenii = new ArrayList<>();
        for(Prietenie p : this.getAllPrietenie()){
            if(Objects.equals(utilizatorVechi, p.getUtilizator1())){
                p.setUtilizator1(utilizatorNou);
                updatedPrietenii.add(p);
            }
            if(Objects.equals(utilizatorVechi, p.getUtilizator2())){
                p.setUtilizator2(utilizatorNou);
                updatedPrietenii.add(p);
            }
        }
        repoPrietenie.updateAll(updatedPrietenii);
    }

    public Optional<Utilizator> modificaUtilizator(String nume, String prenume, String numeNou, String prenumeNou) throws Exception{
        if (this.cautaNume(nume, prenume)==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Utilizator utilizatorVechi=cautaNume(nume,prenume);
        Utilizator utilizatorNou = new Utilizator(numeNou, prenumeNou);
        utilizatorNou.setId(utilizatorVechi.getId());
        //this.modificaPrietenii(utilizatorVechi,utilizatorNou);
        Optional<Utilizator> updatedUtilizator = repoUser.update(utilizatorNou);
        return updatedUtilizator;
    }

    public void adaugaUtilizator(String nume, String prenume) throws Exception {
        Utilizator utilizatorNou = new Utilizator(nume, prenume);
        if (this.cautaNume(nume, prenume) != null) {
            throw new Exception("Utilizatorul exista deja!");
        }
        repoUser.save(utilizatorNou);
    }

    public void stergeUtilizator(String nume, String prenume) throws Exception {
        if (this.cautaNume(nume, prenume)==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Utilizator utilizatorDeSters = this.cautaNume(nume, prenume);
        //*
        for(Prietenie p:this.getAllPrietenie()){
            if(Objects.equals(utilizatorDeSters, p.getUtilizator1())){
                this.stergePrieten(utilizatorDeSters.getNume(), utilizatorDeSters.getPrenume(), p.getUtilizator2().getNume(), p.getUtilizator2().getPrenume());
            }
            if(Objects.equals(utilizatorDeSters, p.getUtilizator2())){
                this.stergePrieten(utilizatorDeSters.getNume(), utilizatorDeSters.getPrenume(), p.getUtilizator1().getNume(), p.getUtilizator1().getPrenume());
            }
        }
        //*/
        repoUser.delete(utilizatorDeSters.getId());
    }

    public void adaugaPrieten(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator utilizator1 = this.cautaNume(nume1, prenume1);
        Utilizator utilizator2 = this.cautaNume(nume2, prenume2);
        if (utilizator1==null && utilizator2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (utilizator1==null || utilizator2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Prietenie p=this.cautaPrietenieNume(utilizator1,utilizator2);
        if (p!=null) {
            throw new Exception("Prietenia exista deja!");
        }
        p = new Prietenie(utilizator1, utilizator2);
        repoPrietenie.save(p);
    }

    public void stergePrieten(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator utilizator1 = this.cautaNume(nume1, prenume1);
        Utilizator utilizator2 = this.cautaNume(nume2, prenume2);
        if (utilizator1==null && utilizator2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (utilizator1==null || utilizator2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Prietenie p=this.cautaPrietenieNume(utilizator1,utilizator2);
        if (p==null) {
            throw new Exception("Prietenia nu exista!");
        }
        repoPrietenie.delete(p.getId());
    }

    private Prietenie cautaPrietenieNume(Utilizator utilizator1,Utilizator utilizator2) {
        for(Prietenie p:this.getAllPrietenie()){
            if(p.getUtilizator1().equals(utilizator1) && p.getUtilizator2().equals(utilizator2)){
                return p;
            }
            if(p.getUtilizator1().equals(utilizator2) && p.getUtilizator2().equals(utilizator1)){
                return p;
            }
        }
        return null;
    }

    public List<Integer> keys = new ArrayList<Integer>();

    public Integer nrComunitati() {
        Graf g = new Graf(((Collection<?>) repoUser.findAll()).size());
        Map<Long, ElementGraf> entities = new HashMap<>();
        int i = 0;
        for (Utilizator u : this.getAllUtilizatori()) {
            ElementGraf elem = new ElementGraf(u);
            elem.setIdGraf(i);
            entities.put(u.getId(), elem);
            i++;
        }
        for(Prietenie p:this.getAllPrietenie()){
            ElementGraf elem1=entities.get(p.getUtilizator1().getId());
            ElementGraf elem2=entities.get(p.getUtilizator2().getId());
            g.adaugaMuchie((int) elem1.getIdGraf(), (int) elem2.getIdGraf());
        }
        return g.Conexe();
    }

    public ArrayList<ArrayList<Utilizator>> comunitateSociabila() {
        Graf g = new Graf(((Collection<?>) repoUser.findAll()).size());
        Map<Long, ElementGraf> entities = new HashMap<>();
        int i = 0;
        for (Utilizator u : this.getAllUtilizatori()) {
            ElementGraf elem = new ElementGraf(u);
            elem.setIdGraf(i);
            entities.put(u.getId(), elem);
            i++;
        }
        for(Prietenie p:this.getAllPrietenie()){
            ElementGraf elem1=entities.get(p.getUtilizator1().getId());
            ElementGraf elem2=entities.get(p.getUtilizator2().getId());
            g.adaugaMuchie((int) elem1.getIdGraf(), (int) elem2.getIdGraf());
        }
        Integer nr_comp = g.Conexe();
        ArrayList<ArrayList<Utilizator>> comp = new ArrayList<ArrayList<Utilizator>>();
        ArrayList<Utilizator> var;
        for (ArrayList<Integer> listaListe : g.getAllCompConx()) {
            var = new ArrayList<Utilizator>();
            for (Integer val : listaListe) {
                for (ElementGraf u : entities.values()) {
                    if ((int) u.getIdGraf() == val) {
                        var.add(u.getElement());
                    }
                }
            }
            comp.add(var);
        }
        return comp;
    }

    public ArrayList<Utilizator> celMaiMareArray(ArrayList<ArrayList<Utilizator>> param) {
        ArrayList<Utilizator> celMaiMareArray = new ArrayList<Utilizator>();
        for (ArrayList<Utilizator> listaActuala : param) {
            if (listaActuala.size() > celMaiMareArray.size()) {
                celMaiMareArray = listaActuala;
            }
        }
        return celMaiMareArray;
    }

    public ArrayList<Utilizator> prieteniUser(Utilizator utilizator){
        ArrayList<Utilizator> sol=new ArrayList<>();
        for(Prietenie p:this.getAllPrietenie()){
            if(p.getUtilizator1().equals(utilizator)){
                sol.add(p.getUtilizator2());
            }
            if(p.getUtilizator2().equals(utilizator)){
                sol.add(p.getUtilizator1());
            }
        }
        return sol;
    }

    public List<Utilizator> PrieteniMinimPredicat(Integer n) {
        Predicate<Utilizator> predicate = utilizator -> prieteniUser(utilizator).size() >= n;
        List<Utilizator> rez=new ArrayList<Utilizator>();
        this.getAllUtilizatori().forEach(rez::add);
        return rez.stream().filter(predicate).collect(Collectors.toList());
    }

    public ArrayList<Utilizator> utilizatoriStr(String string){
        ArrayList<Utilizator> rez=new ArrayList<>();
        for(Utilizator u: this.getAllUtilizatori()){
            if(u.getNume().contains(string) || u.getPrenume().contains(string)){
                rez.add(u);
            }
        }
        return rez;
    }

    public List<Utilizator> prieteniLuna(String nume, String prenume, String luna) {
        Utilizator utilizator = this.cautaNume(nume, prenume);

        Iterable<Prietenie> allPrietenie = getAllPrietenie();

        return StreamSupport.stream(allPrietenie.spliterator(), false)
                .filter(p -> {
                    String lunaAux = p.getData().format(DateTimeFormatter.ofPattern("MM"));
                    return (p.getUtilizator1().equals(utilizator) || p.getUtilizator2().equals(utilizator))
                            && luna.equals(lunaAux);
                })
                .flatMap(p -> {
                    if (p.getUtilizator1().equals(utilizator)) {
                        return Stream.of(p.getUtilizator2());
                    } else {
                        return Stream.of(p.getUtilizator1());
                    }
                })
                .collect(Collectors.toList());
    }

    public void trimiteMesaj(String nume1, String prenume1, String nume2, String prenume2, String text, Long reply) throws Exception{
        Utilizator u1=cautaNume(nume1,prenume1);
        Utilizator u2=cautaNume(nume2,prenume2);
        if (u1==null && u2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (u1==null || u2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        if(Objects.equals(u1.getId(), u2.getId())){
            throw new Exception("Utilizatorul nu poate trimite mesaj lui insusi!");
        }
        Mesaj m=new Mesaj(text,u1,u2,reply);
        repoMsj.save(m);
    }

    public ArrayList<Mesaj> conversatie(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator u1 = cautaNume(nume1, prenume1);
        Utilizator u2 = cautaNume(nume2, prenume2);
        if (u1 == null && u2 == null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (u1 == null || u2 == null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        ArrayList<Mesaj> conv=new ArrayList<>();
        for(Mesaj m: this.getAllMesaje()){
            if(Objects.equals(m.getFrom(),u1) && Objects.equals(m.getTo(),u2)){
                conv.add(m);
            }
            if(Objects.equals(m.getFrom(),u2) && Objects.equals(m.getTo(),u1)){
                conv.add(m);
            }
        }
        Collections.sort(conv, new MesajComparator());
        return conv;
    }

    public void forwardMesaj(String numeUser1, String prenumeUser1, String iduri, String text, Long reply) throws Exception{
        String[] id_useri=iduri.split(",");
        Utilizator u1=cautaNume(numeUser1,prenumeUser1);
        if (u1 == null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        for(String id_user:id_useri){
            Optional<Utilizator> u2=repoUser.findOne(Long.parseLong(id_user));
            if(u2.isEmpty()){
                throw new Exception("Utilizatorul cu id "+id_user+" nu exista!");
            }
            trimiteMesaj(numeUser1,prenumeUser1,u2.get().getNume(),u2.get().getPrenume(),text,reply);
        }
    }

    public void creeazaCerere(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator utilizator1 = this.cautaNume(nume1, prenume1);
        Utilizator utilizator2 = this.cautaNume(nume2, prenume2);
        if (utilizator1==null && utilizator2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (utilizator1==null || utilizator2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Cerere c=this.cautaCerereNume(utilizator1,utilizator2);
        if (c!=null) {
            throw new Exception("Cererea deja exista!");
        }
        Prietenie p=this.cautaPrietenieNume(utilizator1,utilizator2);
        if (p!=null) {
            throw new Exception("Prietenia exista deja!");
        }
        c=new Cerere(utilizator1,utilizator2,Status.PENDING);
        repoCerere.save(c);
    }

    public void acceptaCerere(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator utilizator1 = this.cautaNume(nume1, prenume1);
        Utilizator utilizator2 = this.cautaNume(nume2, prenume2);
        if (utilizator1==null && utilizator2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (utilizator1==null || utilizator2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Cerere c=this.cautaCerereNume(utilizator1,utilizator2);
        if (c==null) {
            throw new Exception("Cererea nu exista!");
        }
        if (c.getStatus()!=Status.PENDING) {
            throw new Exception("Cererea deja acceptata!");
        }
        c.setStatus(Status.ACCEPTED);
        this.adaugaPrieten(nume1,prenume1,nume2,prenume2);
        repoCerere.update(c);
    }

    public void stergeCerere(String nume1, String prenume1, String nume2, String prenume2) throws Exception {
        Utilizator utilizator1 = this.cautaNume(nume1, prenume1);
        Utilizator utilizator2 = this.cautaNume(nume2, prenume2);
        if (utilizator1==null && utilizator2==null) {
            throw new Exception("Utilizatorii nu exista!");
        }
        if (utilizator1==null || utilizator2==null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        Cerere c=this.cautaCerereNume(utilizator1,utilizator2);
        if (c==null) {
            throw new Exception("Cererea nu exista!");
        }
        if (c.getStatus()!=Status.PENDING) {
            throw new Exception("Cererea deja acceptata!");
        }
        repoCerere.delete(c.getId());
    }
}
