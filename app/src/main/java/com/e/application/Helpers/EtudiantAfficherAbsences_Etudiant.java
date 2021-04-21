package com.e.application.Helpers;

public class EtudiantAfficherAbsences_Etudiant {
    // affichage default
    private String code_module = "";
    private String type = "";
    private String jour = "";
    private String heure = "";
    private int nbr_absences = 0;
    //affichage apres display
    private int numero_absence = 0;
    private String date_absence = "";
    private String etat = "";
    private String etat_default = "";
    private boolean historique = false;


    public EtudiantAfficherAbsences_Etudiant() {
    }

    public EtudiantAfficherAbsences_Etudiant(String code_module, String type, String jour, String heure, int nbr_absences) {
        this.code_module = code_module;
        this.type = type;
        this.jour = jour;
        this.heure = heure;
        this.nbr_absences = nbr_absences;
    }

    public EtudiantAfficherAbsences_Etudiant(int numero_absence, String code_module, String type, String date_absence, String etat, String etat_default, boolean historique) {
        this.numero_absence = numero_absence;
        this.code_module = code_module;
        this.type = type;
        this.date_absence = date_absence;
        this.etat = etat;
        this.etat_default = etat_default;
        this.historique = historique;
    }

    public String getCode_module() {
        return code_module;
    }

    public void setCode_module(String code_module) {
        this.code_module = code_module;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public int getNbr_absences() {
        return nbr_absences;
    }

    public void setNbr_absences(int nbr_absences) {
        this.nbr_absences = nbr_absences;
    }


    public String getDate_absence() {
        return date_absence;
    }

    public void setDate_absence(String date_absence) {
        this.date_absence = date_absence;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }


    public String getEtat_default() {
        return etat_default;
    }

    public void setEtat_default(String etat_default) {
        this.etat_default = etat_default;
    }

    public boolean isHistorique() {
        return historique;
    }

    public void setHistorique(boolean historique) {
        this.historique = historique;
    }

    public int getNumero_absence() {
        return numero_absence;
    }

    public void setNumero_absence(int numero_absence) {
        this.numero_absence = numero_absence;
    }

    @Override
    public String toString() {
        return "EtudiantAfficherAbsences_Etudiant{" +
                "code_module='" + code_module + '\'' +
                ", type='" + type + '\'' +
                ", jour='" + jour + '\'' +
                ", heure='" + heure + '\'' +
                ", nbr_absences=" + nbr_absences +
                ", numero_absence=" + numero_absence +
                ", date_absence='" + date_absence + '\'' +
                ", etat='" + etat + '\'' +
                ", etat_default='" + etat_default + '\'' +
                ", historique=" + historique +
                '}';
    }
}
