package com.paocorp.models;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

public class Mot {

    String mot;
    ArrayList<Lettre> lettres;
    HashMap verif;
    boolean trouve = false;
    boolean existe = true;
    boolean fini = false;
    boolean recursif = false;
    int ligne = 1;
    String malplace = "yellow";
    String exact = "red";
    String nontrouve = "blue";

    public Mot(String mot) {
        this.lettres = new ArrayList<Lettre>();
        this.verif = new HashMap<Integer, Mot>();
        this.mot = stripAccents(mot.toLowerCase());

        String[] caracs = mot.split("");
        for (int i = 0; i < caracs.length; i++) {
            this.lettres.add(new Lettre(caracs[i], i));
        }
    }

    public void compare(Mot mot) throws Exception {

        if (this.existe) {
            if (this.verif.size() > 0) {
                this.verif.clear();
            }

            this.firstPassage(mot);

            for (int i = 0; i < mot.getLettres().size(); i++) {
                if (!this.getLettres().get(i).getCaractere().equals(mot.getLettres().get(i).getCaractere())) {

                    if (this.countOccurrences(this.mot, mot.getLettres().get(i).getCaractere()) == 0) {
                        this.getLettres().get(i).setPlacement(this.nontrouve);
                    } else {

                        int nbTry = this.countOccurrences(mot.getMot(), mot.getLettres().get(i).getCaractere());
                        int nbMot = this.countOccurrences(this.mot, mot.getLettres().get(i).getCaractere());
                        if (nbTry > nbMot) {
                            for (int j = 0; j < mot.getLettres().size(); j++) {
                                if (mot.getLettres().get(i).getCaractere().equals(mot.getLettres().get(j).getCaractere())
                                        && this.getLettres().get(j).getPlacement() == this.nontrouve
                                        && i != j) {
                                    this.getLettres().get(i).setPlacement(this.malplace);
                                }
                            }
                            if (this.getLettres().get(i).getPlacement().isEmpty()) {
                                this.getLettres().get(i).setPlacement(this.nontrouve);
                            }

                        } else {
                            this.getLettres().get(i).setPlacement(this.malplace);
                        }
                    }
                    String[] averif = {
                            mot.getLettres().get(i).getCaractere(),
                            this.lettres.get(i).getPlacement()
                    };
                    this.verif.put(i, averif);
                }
            }

            this.estTrouve();
        } else {
            this.fini = true;
            this.trouve = false;
        }

    }

    private void firstPassage(Mot mot) {
        for (int i = 0; i < mot.getLettres().size(); i++) {
            if (this.getLettres().get(i).getCaractere().equals(mot.getLettres().get(i).getCaractere())) {
                this.getLettres().get(i).setPlacement(this.exact);
                String[] averif = {
                        mot.getLettres().get(i).getCaractere(),
                        this.lettres.get(i).getPlacement()
                };
                this.verif.put(i, averif);
            }
        }
    }

    public void soluce() {
        this.verif.clear();
        for (int i = 0; i < this.lettres.size(); i++) {
            String[] anArray = {
                    this.lettres.get(i).getCaractere(),
                    this.exact
            };
            this.verif.put(i, anArray);
        }
    }

    public void estTrouve() {
        boolean motTrouve = true;
        for (int i = 0; i < this.lettres.size(); i++) {
            if (!this.getLettres().get(i).getPlacement().equals(this.exact)) {
                motTrouve = false;
            }
        }
        this.trouve = motTrouve;
    }

    private int countOccurrences(String haystack, String needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (String.valueOf(haystack.charAt(i)).equals(needle)) {
                count++;
            }
        }
        return count;
    }

    public String getMot() {
        return this.mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public ArrayList<Lettre> getLettres() {
        return this.lettres;
    }

    public void setLettres(ArrayList<Lettre> lettres) {
        this.lettres = lettres;
    }

    public HashMap getVerif() {
        return this.verif;
    }

    public void setVerif(HashMap verif) {
        this.verif = verif;
    }

    public boolean isFini() {
        return this.fini;
    }

    public void setFini(boolean fini) {
        this.fini = fini;
    }

    public boolean isTrouve() {
        return this.trouve;
    }

    public void setTrouve(boolean trouve) {
        this.trouve = trouve;
    }

    public boolean isExiste() {
        return this.existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }

    public int getLigne() {
        return this.ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public String getMalplace() {
        return this.malplace;
    }

    public void setMalplace(String malplace) {
        this.malplace = malplace;
    }

    public String getExact() {
        return this.exact;
    }

    public void setExact(String exact) {
        this.exact = exact;
    }

    public String getNontrouve() {
        return this.nontrouve;
    }

    public void setNontrouve(String nontrouve) {
        this.nontrouve = nontrouve;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
