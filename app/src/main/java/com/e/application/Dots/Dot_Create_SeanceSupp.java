package com.e.application.Dots;

public class Dot_Create_SeanceSupp {
    private String code_seance = null;
    private String jour;
    private String heure = null;



    public Dot_Create_SeanceSupp(String code_seance, String jour, String heure)
    {
        this.code_seance = code_seance;
        this.jour = jour;
        this.heure = heure;
    }

    public String getCode_seance()
    {
        return code_seance;
    }

    public String getJour()
    {
        return jour;
    }

    public String getHeure()
    {
        return heure;
    }

    public void setCode_seance(String code_seance)
    {
        this.code_seance = code_seance;
    }

    public void setJour(String jour)
    {
        this.jour = jour;
    }

    public void setHeure(String heure)
    {
        this.heure = heure;
    }


}
