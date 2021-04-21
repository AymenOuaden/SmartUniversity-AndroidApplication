package com.e.application.Dots;

public class Dot_Create_Absence {

    private String code_seance;
    private int id_etudiant;
    private String date_absence;

    public Dot_Create_Absence(String code_seance, int id_etudiant, String date) {
        this.code_seance = code_seance;
        this.id_etudiant = id_etudiant;
        this.date_absence = date;
    }


    public String getCode_seance() {
        return code_seance;
    }

    public int getId_etudiant() {
        return id_etudiant;
    }

    public String getDate_absence() {
        return date_absence;
    }

    public void setCode_seance(String code_seance) {
        this.code_seance = code_seance;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }

    public void setDate_absence(String date_absence) {
        this.date_absence = date_absence;
    }

}
