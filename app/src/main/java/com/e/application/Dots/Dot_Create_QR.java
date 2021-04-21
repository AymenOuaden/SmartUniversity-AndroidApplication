package com.e.application.Dots;

import java.util.Arrays;

public class Dot_Create_QR {

    private int id_enseignant=0;
    private String code_seance="";
    private byte[] fichier=null;
    private String texte_qr ="";

    public Dot_Create_QR() {
    }

    public Dot_Create_QR(int id_enseignant, String code_seance, byte[] fichier,String texte_qr) {
        this.id_enseignant = id_enseignant;
        this.code_seance = code_seance;
        this.fichier = fichier;
        this.texte_qr = texte_qr;
    }

    public int getId_enseignant() {
        return id_enseignant;
    }

    public void setId_enseignant(int id_enseignant) {
        this.id_enseignant = id_enseignant;
    }

    public String getCode_seance() {
        return code_seance;
    }

    public void setCode_seance(String code_seance) {
        this.code_seance = code_seance;
    }

    public byte[] getFichier() {
        return fichier;
    }

    public void setFichier(byte[] fichier) {
        this.fichier = fichier;
    }

    public String getTexte_qr() {
        return texte_qr;
    }

    public void setTexte_qr(String texte_qr) {
        this.texte_qr = texte_qr;
    }

    @Override
    public String toString() {
        return "Dot_Create_QR{" +
                "id_enseignant=" + id_enseignant +
                ", code_seance='" + code_seance + '\'' +
                ", fichier=" + Arrays.toString(fichier) +
                ", texte_qr='" + texte_qr + '\'' +
                '}';
    }
}
