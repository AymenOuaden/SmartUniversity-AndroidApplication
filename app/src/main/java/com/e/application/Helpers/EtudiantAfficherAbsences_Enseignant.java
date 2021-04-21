package com.e.application.Helpers;

import com.e.application.Model.Absence;

import java.io.Serializable;

public class EtudiantAfficherAbsences_Enseignant implements Serializable {

    private int id;
    private String nom;
    private String prenom;
    private String etat;
    private String default_etat;
    private Absence absence;
    boolean historique;


    public EtudiantAfficherAbsences_Enseignant(int id, String nom, String prenom, String etat, String default_etat, Absence absence, boolean historique) {
        this.id=id;
        this.nom=nom;
        this.prenom=prenom;
        this.etat=etat;
        this.default_etat=default_etat;
        this.absence=absence;
        this.historique=historique;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }


    public Absence getAbsence() {
        return absence;
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDefault_etat() {
        return default_etat;
    }

    public void setDefault_etat(String default_etat) {
        this.default_etat = default_etat;
    }

    public boolean isHistorique() {
        return historique;
    }

    public void setHistorique(boolean historique) {
        this.historique = historique;
    }

    @Override
    public String toString() {
        return "EtudiantAfficherAbsences_Enseignant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", etat='" + etat + '\'' +
                ", default_etat='" + default_etat + '\'' +
                ", icon_manage_absences=" + absence +
                ", historique=" + historique +
                '}';
    }
}
