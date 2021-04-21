package com.e.application.Model;

import java.io.Serializable;

public class ChangementSeance implements Serializable {

    private String code_seance = null;
    private Seance.Jour nouveau_jour;
    private String heure ;
    private Seance.Etat_Demande etat_seance;

    public ChangementSeance(String code_seance, Seance.Jour nouveau_jour, String heure, Seance.Etat_Demande etat_seance)
    {
        this.code_seance = code_seance;
        this.nouveau_jour = nouveau_jour;
        this.heure = heure;
        this.etat_seance = etat_seance;
    }

    public ChangementSeance()
    {

    }

    public String getCode_seance()
    {
        return code_seance;
    }

    public Seance.Jour getNouveau_jour()
    {
        return nouveau_jour;
    }

    public String getheure()
    {
        return heure;
    }

    public Seance.Etat_Demande getEtat_demande()
    {
        return etat_seance;
    }

    public void setCode_seance(String code_seance)
    {
        this.code_seance = code_seance;
    }

    public void setNouveau_jour(Seance.Jour nouveau_jour)
    {
        this.nouveau_jour = nouveau_jour;
    }

    public void setheure(String heure)
    {
        this.heure = heure;
    }

    public void setEtat_demande(Seance.Etat_Demande etat_seance)
    {
        this.etat_seance = etat_seance;
    }

    public String CapitalizedJour()
    {
        String str = String.valueOf(nouveau_jour);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String toString()
    {
        return "ChangementSeance [code_seance=" + code_seance + ", nouveau_jour=" + nouveau_jour + ", heure=" + heure
                + ", etat=" + etat_seance + "]";
    }
}
