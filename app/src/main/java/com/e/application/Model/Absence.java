package com.e.application.Model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Absence implements Serializable {
    private int numero_absence = 0;
    private String code_seance = null;
    private int id_etudiant = 0;
    private Date date_absence = null;

    public Absence(int numero_absence, String code_seance, int id_etudiant)
    {
        this.numero_absence = numero_absence;
        this.code_seance = code_seance;
        this.id_etudiant = id_etudiant;
        this.date_absence = new Date();
    }

    public Absence()
    {

    }

    public int getNumero_absence()
    {
        return numero_absence;
    }

    public String getCode_seance()
    {
        return code_seance;
    }

    public int getId_etudiant()
    {
        return id_etudiant;
    }

    public String getDate_absence()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.applyPattern("yyyy-MM-dd");
        return formatter.format(date_absence);
    }

    public void setNumero_absence(int numero_absence)
    {
        this.numero_absence = numero_absence;
    }

    public void setCode_seance(String code_seance)
    {
        this.code_seance = code_seance;
    }

    public void setId_etudiant(int id_etudiant)
    {
        this.id_etudiant = id_etudiant;
    }

    public void setDate_absence(String date_absence)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            this.date_absence = formatter.parse(date_absence);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "AbsenceSQL{" +
                "numero_absence=" + numero_absence +
                ", code_seance='" + code_seance + '\'' +
                ", id_etudiant=" + id_etudiant +
                ", date_absence=" + date_absence +
                '}';
    }
}
