package com.e.application.Helpers;

import com.e.application.Model.ChangementSeance;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Etudiant.Specialite;
import com.e.application.Model.Module;
import com.e.application.Model.Seance;
import com.e.application.Model.SeanceSupp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class SeanceResponse implements Serializable {

    private ArrayList<EtudiantResponse> etudiants = null;
    private Module module = null;
    private Seance seance = null;
    private ChangementSeance changementSeance = null;
    private ArrayList<SeanceSupp> seancesSupp = null;

    public SeanceResponse(ArrayList<EtudiantResponse> etudiants, Module module, Seance seance,
                          ChangementSeance changementSeance, ArrayList<SeanceSupp> seancesSupp)
    {
        this.etudiants = etudiants;
        this.module = module;
        this.seance = seance;
        this.changementSeance = changementSeance;
        this.seancesSupp = seancesSupp;
    }

    public SeanceResponse()
    {

    }

    public ArrayList<EtudiantResponse> getEtudiants()
    {
        return etudiants;
    }

    public Module getModule()
    {
        return module;
    }

    public Seance getSeance()
    {
        return seance;
    }

    public ChangementSeance getChangementSeance()
    {
        return changementSeance;
    }

    public ArrayList<SeanceSupp> getSeancesSupp()
    {
        return seancesSupp;
    }

    public void setSeancesSupp(ArrayList<SeanceSupp> seancesSupp)
    {
        this.seancesSupp = seancesSupp;
    }

    public void setEtudiants(ArrayList<EtudiantResponse> etudiants)
    {
        this.etudiants = etudiants;
    }

    public void setModule(Module module)
    {
        this.module = module;
    }

    public void setSeance(Seance seance)
    {
        this.seance = seance;
    }

    public void setChangementSeance(ChangementSeance changementSeance)
    {
        this.changementSeance = changementSeance;
    }

    public static SeanceResponse GetByCodeSeance(ArrayList<SeanceResponse> seancesResponse, String code_seance)
    {
        SeanceResponse result = null;
        for (SeanceResponse seanceResponse : seancesResponse)
        {
            if(seanceResponse.getSeance().getCode_seance().equals(code_seance))
            {
                result = seanceResponse;
                return result;
            }
        }

        return null;
    }

    public boolean hasEtudiantsExclus()
    {
        int result = 0;
        if(etudiants != null)
        {
            for (EtudiantResponse etudiantResponse : etudiants)
            {
                if(etudiantResponse.isExclus())
                {
                    result++;
                }
            }
        }

        return result > 0;
    }

    public boolean hasAbsences()
    {
        if(etudiants != null)
        {
            for (EtudiantResponse etudiantResponse : etudiants)
            {
                if(etudiantResponse.getNombreAbsences() > 0)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasEtudiants()
    {
        if(etudiants != null && etudiants.size() > 0)
            return true;

        return false;
    }

    public static ArrayList<String> getUniqueModules(ArrayList<SeanceResponse> seances)
    {
        ArrayList<String> result = new ArrayList<String>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getModule().getNom()))
            {
                result.add(seance.getModule().getNom());
            }
        }

        return result;
    }

    public static ArrayList<Integer> getUniqueGroupes(ArrayList<SeanceResponse> seances)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getGroupe()))
            {
                result.add(seance.getSeance().getGroupe());
            }
        }

        Collections.sort(result);

        return result;
    }

    public static ArrayList<String> getUniqueJours(ArrayList<SeanceResponse> seances)
    {
        ArrayList<Seance.Jour> result = new ArrayList<Seance.Jour>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getJour()))
            {
                result.add(seance.getSeance().getJour());
            }
        }

        Collections.sort(result);

        ArrayList<String> strings = new ArrayList<String>();

        for (Seance.Jour jour : result)
        {
            String str = String.valueOf(jour);
            strings.add(str.substring(0, 1).toUpperCase() + str.substring(1));
        }

        return strings;
    }

    public static ArrayList<Etudiant.Annee> getUniqueAnnees(ArrayList<SeanceResponse> seances)
    {
        ArrayList<Etudiant.Annee> result = new ArrayList<Etudiant.Annee>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getAnnee()))
            {
                result.add(seance.getSeance().getAnnee());
            }
        }

        Collections.sort(result);

        return result;
    }

    public static ArrayList<Etudiant.Specialite> getUniqueSpecialites(ArrayList<SeanceResponse> seances)
    {
        ArrayList<Etudiant.Specialite> result = new ArrayList<Specialite>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getSpecialite()))
            {
                result.add(seance.getSeance().getSpecialite());
            }
        }

        Collections.sort(result);

        return result;
    }

    public static ArrayList<String> getUniqueHeures(ArrayList<SeanceResponse> seances)
    {
        ArrayList<String> result = new ArrayList<String>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getHeure()))
            {
                result.add(seance.getSeance().getHeure());
            }
        }

        Collections.sort(result);

        return result;
    }

    public static ArrayList<Seance.Type_Seance> getUniqueTypes(ArrayList<SeanceResponse> seances)
    {
        ArrayList<Seance.Type_Seance> result = new ArrayList<Seance.Type_Seance>();

        for (SeanceResponse seance : seances)
        {
            if(!result.contains(seance.getSeance().getType()))
            {
                result.add(seance.getSeance().getType());
            }
        }

        Collections.sort(result);

        return result;
    }
}
