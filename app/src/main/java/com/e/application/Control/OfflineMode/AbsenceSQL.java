package com.e.application.Control.OfflineMode;

public class AbsenceSQL {
    private int id_etudiant;
    private int id_enseignant;
    private String jour_seance;
    private String heure_seance;
    private String date_absence;


    public AbsenceSQL(int id_etudiant, int id_enseignant, String jour_seance, String heure_seance, String date_absence) {
        this.id_etudiant = id_etudiant;
        this.id_enseignant = id_enseignant;
        this.jour_seance = jour_seance;
        this.heure_seance = heure_seance;
        this.date_absence = date_absence;
    }


    public int getId_etudiant() {
        return id_etudiant;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }

    public int getId_enseignant() {
        return id_enseignant;
    }

    public void setId_enseignant(int id_enseignant) {
        this.id_enseignant = id_enseignant;
    }

    public String getJour_seance() {
        return jour_seance;
    }

    public void setJour_seance(String jour_seance) {
        this.jour_seance = jour_seance;
    }

    public String getHeure_seance() {
        return heure_seance;
    }

    public void setHeure_seance(String heure_seance) {
        this.heure_seance = heure_seance;
    }

    public String getDate_absence() {
        return date_absence;
    }

    public void setDate_absence(String date_absence) {
        this.date_absence = date_absence;
    }
}
