package com.paocorp.models;

import java.util.ArrayList;

public class Motus {

    int nb;
    ArrayList<Mot> mots;
    ArrayList<Integer> essais;
    int ligne;
    int current;
    int nbmots = 5;
    int nblignes = 6;

    public Motus(int nb) {
        this.setNb(nb);
        this.setLigne(1);
        this.setCurrent(1);
        this.mots = new ArrayList<Mot>();
        this.essais = new ArrayList<Integer>();
    }

    private boolean finEssais(Mot mot){
        if(mot.getLigne() == this.nblignes || mot.isTrouve() || !mot.isExiste()){
            this.ligne = 1;
            mot.setFini(true);
            return true;
        } else {
            this.ligne++;
            mot.setLigne(this.ligne);
            return false;
        }
    }

    public Mot verifMot(Integer rang, String caracs, boolean existe) throws Exception {
        Mot mot = this.getMot(rang);
        mot.setExiste(existe);

        mot.compare(new Mot(caracs));

        boolean fin = this.finEssais(mot);

        if(fin && !this.isComplete()){
            rang++;
            this.setCurrent(rang);
            //mot.soluce();
        } else if(this.isComplete()){
            //mot.soluce();
        }

        return mot;
    }

    public boolean isComplete(){
        return this.current == this.nbmots +1;
    }

    private void setNb(int nb) {
        this.nb = nb;
    }

    public void setMots(ArrayList mots) {
        this.mots = mots;
    }

    public void setEssais(ArrayList essais) {
        this.essais = essais;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getNb() {
        return this.nb;
    }

    public int getNbmots() {
        return this.nbmots;
    }

    public int getNblignes() {
        return nblignes;
    }

    public ArrayList<Mot> getMots() {
        return this.mots;
    }

    public Mot getMot(int rang){
        return this.getMots().get(rang-1);
    }

    public ArrayList getEssais() {
        return this.essais;
    }

    public int getCurrent() {
        return this.current;
    }

    public int getLigne() {
        return this.ligne;
    }
}
