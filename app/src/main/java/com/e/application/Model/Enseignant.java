package com.e.application.Model;

import java.io.Serializable;

public class Enseignant extends Utilisateur implements Serializable {

    private String grade = null;
    private Code_Departement code_departement;

    public Enseignant()
    {

    }

    public Enseignant(String grade)
    {
        this.grade = grade;
    }

    public Enseignant(Utilisateur utilisateur, String grade, Code_Departement code_departement)
    {
        super(utilisateur.id_utilisateur, utilisateur.user, utilisateur.pass, utilisateur.nom, utilisateur.prenom,
                utilisateur.adresse, utilisateur.date, utilisateur.email, utilisateur.telephone,
                utilisateur.user_type);
        this.grade = grade;
        this.code_departement = code_departement;
    }



    public Code_Departement getCode_departement()
    {
        return code_departement;
    }

    public void setCode_departement(Code_Departement code_departement)
    {
        this.code_departement = code_departement;
    }

    public String getGrade()
    {
        return grade;
    }

    public void setGrade(String grade)
    {
        this.grade = grade;
    }


    @Override
    public String toString() {
        return "Enseignant{" +
                "grade='" + grade + '\'' +
                ", code_departement=" + code_departement +
                ", id_utilisateur=" + id_utilisateur +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", ic_date='" + date + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", user_type=" + user_type +
                '}';
    }
}
