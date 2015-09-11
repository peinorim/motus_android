package com.paocorp.models;

public class Lettre {

    String caractere;
    String placement;
    Integer position;

    public Lettre(String c, Integer p){
        this.caractere = c;
        this.position = p;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getCaractere() {
        return caractere;
    }

    public void setCaractere(String caractere) {
        this.caractere = caractere;
    }
}
