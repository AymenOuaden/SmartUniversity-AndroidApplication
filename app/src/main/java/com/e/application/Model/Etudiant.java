package com.e.application.Model;

import java.io.Serializable;

public class Etudiant extends Utilisateur implements Serializable {



    public enum Annee
    {
        L1,
        L2,
        L3,
        M1,
        M2
    }

    public enum Specialite
    {
        MI,
        GL,
        SI,
        SCI,
        TI,
        STIC,
        STIW,
        RSD
    }

    public enum Etat_Etudiant
    {
        actif,
        bloque
    }

    private Annee annee;
    private Specialite specialite;
    private int section = 0;
    private int groupe = 0;
    private Etat_Etudiant etat_etudiant;
    private Code_Departement code_departement;

    public Etudiant(Utilisateur utilisateur, Annee annee, Specialite specialite,
                    int section, int groupe, Etat_Etudiant etat_etudiant, Code_Departement code_departement)
    {
        super(utilisateur.id_utilisateur, utilisateur.user, utilisateur.pass, utilisateur.nom, utilisateur.prenom, utilisateur.adresse,
                utilisateur.date, utilisateur.email, utilisateur.telephone, utilisateur.user_type);
        this.annee = annee;
        this.specialite = specialite;
        this.section = section;
        this.groupe = groupe;
        this.etat_etudiant = etat_etudiant;
        this.code_departement = code_departement;
    }

    public Etudiant(String nom, String prenom) {
    super(0,nom,prenom);
    }

    public Etudiant()
    {

    }



    public Annee getAnnee()
    {
        return annee;
    }

    public Specialite getSpecialite()
    {
        return specialite;
    }

    public int getSection()
    {
        return section;
    }

    public int getGroupe()
    {
        return groupe;
    }

    public Etat_Etudiant getEtat_etudiant()
    {
        return etat_etudiant;
    }

    public Code_Departement getCode_departement()
    {
        return code_departement;
    }

    public void setAnnee(Annee annee)
    {
        this.annee = annee;
    }

    public void setSpecialite(Specialite specialite)
    {
        this.specialite = specialite;
    }

    public void setSection(int section)
    {
        this.section = section;
    }

    public void setGroupe(int groupe)
    {
        this.groupe = groupe;
    }

    public void setEtat_etudiant(Etat_Etudiant etat_etudiant)
    {
        this.etat_etudiant = etat_etudiant;
    }

    public void setCode_departement(Code_Departement code_departement)
    {
        this.code_departement = code_departement;
    }

    @Override
    public String toString()
    {
        return "Etudiant [annee=" + annee + ", specialite=" + specialite + ", section=" + section + ", groupe=" + groupe
                + ", etat_etudiant=" + etat_etudiant + ", code_departement=" + code_departement + ", id=" + id_utilisateur
                + ", user=" + user + ", pass=" + pass + ", nom=" + nom + ", prenom=" + prenom + ", adresse=" + adresse
                + ", date_n=" + date + ", email=" + email + ", telephone=" + telephone + ", type_utilisateur="
                + user_type + "]";
    }}
