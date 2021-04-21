package com.e.application.Model;

import java.io.Serializable;

public class SeanceSupp implements Serializable {

    private String code_seance = null;
    private int code_seance_supp;
    private Seance.Jour jour;
    private String heure = null;
    private Seance.Etat_Demande etat_seance;

    public SeanceSupp()
    {
    }

    public SeanceSupp(String code_seance, int code_seance_supp, Seance.Jour jour, String heure, Seance.Etat_Demande etat_seance)
    {
        this.code_seance = code_seance;
        this.code_seance_supp = code_seance_supp;
        this.jour = jour;
        this.heure = heure;
        this.etat_seance = etat_seance;
    }

    public String getCode_seance()
    {
        return code_seance;
    }

    public int getCode_seance_supp()
    {
        return code_seance_supp;
    }

    public Seance.Jour getJour()
    {
        return jour;
    }

    public String getHeure()
    {
        return heure;
    }

    public Seance.Etat_Demande getEtat_seance()
    {
        return etat_seance;
    }

    public void setCode_seance(String code_seance)
    {
        this.code_seance = code_seance;
    }

    public void setCode_seance_supp(int code_seance_supp)
    {
        this.code_seance_supp = code_seance_supp;
    }

    public void setJour(Seance.Jour jour)
    {
        this.jour = jour;
    }

    public void setHeure(String heure)
    {
        this.heure = heure;
    }

    public void setEtat_seance(Seance.Etat_Demande etat_seance)
    {
        this.etat_seance = etat_seance;
    }


    @Override
    public String toString() {
        return "SeanceSupp{" +
                "code_seance='" + code_seance + '\'' +
                ", code_seance_supp=" + code_seance_supp +
                ", jour=" + jour +
                ", heure='" + heure + '\'' +
                ", etat_seance=" + etat_seance +
                '}';
    }
}
