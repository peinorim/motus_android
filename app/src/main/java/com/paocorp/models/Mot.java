package com.paocorp.models;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

public class Mot {

    String mot;
    ArrayList<Lettre> lettres;
    boolean trouve = false;
    boolean existe = true;
    boolean fini = false;
    boolean recursif = false;
    int ligne = 1;
    String[] caracs;
    public static HashMap<Integer, String> verif = new HashMap<Integer, String>();
    public static HashMap<Integer, String> saveVerif = new HashMap<Integer, String>();
    String malplace = "yellow";
    String exact = "red";
    String nontrouve = "blue";

    public Mot(String mot) {
        this.lettres = new ArrayList<Lettre>();
        this.verif = new HashMap<Integer, String>();
        this.mot = stripAccents(mot.toLowerCase());

        this.caracs = mot.split("");
        for (int i = 0; i < caracs.length; i++) {
            this.lettres.add(new Lettre(caracs[i], i));
        }
    }

    public void compare(Mot mot) throws Exception {

        String lettresEssai[] = mot.getMot().split("");
        String lettresBase[] = this.getMot().split("");
        this.trouve = true;

        for (int i = 0; i < lettresEssai.length; i++) {

            String currLettreEssai = lettresEssai[i];

            if (!currLettreEssai.isEmpty()) {

                boolean checked = false;

                int m = 0;
                while (m < lettresBase.length) {
                    String currLettreBase = lettresBase[m];
                    if ((i == m && currLettreEssai.equals(currLettreBase))) {
                        verif.put(i, exact);
                        saveVerif.put(m, exact);
                    }
                    m++;
                }

                for (int j = 0; j < lettresBase.length; j++) {

                    String currLettreBase = lettresBase[j];

                    if (!checked && !currLettreBase.isEmpty()) {

                        if ((i == j && currLettreEssai.equals(currLettreBase)) || currLettreEssai.equals(lettresBase[i])) {
                            verif.put(i, exact);
                            saveVerif.put(i, exact);
                        } else {
                            this.trouve = false;
                            int countOccur = countOccurrences(this.getMot(),
                                    lettresEssai[i]);
                            if (countOccur >= 1) {
                                for (int k = 0; k < lettresBase.length; k++) {

                                    if (lettresEssai[i].equals(lettresBase[k])) {
                                        if (saveVerif.get(k) != null) {
                                            verif.put(i, nontrouve);
                                            //saveVerif.put(k, nontrouve);
                                        } else {
                                            verif.put(i, malplace);
                                            saveVerif.put(k, malplace);
                                        }
                                    }
                                }

                            } else if (countOccur == 0) {
                                verif.put(i, nontrouve);
                                //saveVerif.put(i, nontrouve);
                            }
                        }
                        checked = true;
                    }
                }
            }

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

    public void fillExiste() {
        this.trouve = false;
        for (int i = 0; i < this.getMot().split("").length; i++) {
            verif.put(i, nontrouve);
        }
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

    public HashMap<Integer, String> getVerif() {
        return this.verif;
    }

    public String[] getCaracs() {
        return caracs;
    }

    public void setCaracs(String[] caracs) {
        this.caracs = caracs;
    }

    public static void setVerif(HashMap<Integer, String> verif) {
        Mot.verif = verif;
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
