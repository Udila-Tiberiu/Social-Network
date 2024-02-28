package com.example.lab7bun.algoritm;

import java.util.*;

public class Graf {
    private int V;
    private int nrCompConx=0;
    LinkedList<Integer>[] adiacenteLista;
    Map<Integer,ArrayList<Integer>> compConx;

    public Graf(int V){
        this.V=V;
        adiacenteLista=new LinkedList[V];
        for(int i=0; i<V; i++){
            adiacenteLista[i]=new LinkedList<Integer>();
        }
        compConx=new HashMap<>();
    }

    public Iterable<ArrayList<Integer>> getAllCompConx(){
        return compConx.values();
    }

    public void adaugaMuchie(int sursa, int dest){
        adiacenteLista[sursa].add(dest);
        adiacenteLista[dest].add(sursa);
    }

    public void DFS(int v, boolean[] vizitat){
        vizitat[v]=true;
        compConx.get(this.nrCompConx).add(v);
        for(int x: adiacenteLista[v]){
            if(!vizitat[x]) DFS(x, vizitat);
        }
    }

    public int Conexe(){
        boolean[] vizitat = new boolean[V];
        for(int v=0; v<V; v++){
            if(!vizitat[v]){
                nrCompConx++;
                compConx.put(nrCompConx, new ArrayList<>());
                DFS(v, vizitat);
            }
        }
        return nrCompConx;
    }
}