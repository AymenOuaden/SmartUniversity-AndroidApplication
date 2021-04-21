package com.e.application.Dots;

public class Dot_Create_ChangementSeance {

    private String code_seance = null;
    private String nouveau_jour = null;
    private String nouvelle_heure = null;

    public Dot_Create_ChangementSeance(String code_seance, String nouveau_jour, String nouvelle_heure)
    {
        this.nouveau_jour = nouveau_jour;
        this.nouvelle_heure = nouvelle_heure;
        this.code_seance = code_seance;
    }

    public Dot_Create_ChangementSeance()
    {

    }

    public String getCode_seance()
    {
        return code_seance;
    }

    public String getNouveau_jour()
    {
        return nouveau_jour;
    }

    public String getNouvelle_heure()
    {
        return nouvelle_heure;
    }

    public void setCode_seance(String code_seance)
    {
        this.code_seance = code_seance;
    }

    public void setNouveau_jour(String nouveau_jour)
    {
        this.nouveau_jour = nouveau_jour;
    }

    public void setNouvelle_heure(String nouvelle_heure)
    {
        this.nouvelle_heure = nouvelle_heure;
    }


}
