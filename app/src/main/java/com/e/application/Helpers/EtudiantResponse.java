package com.e.application.Helpers;

import com.e.application.Model.Etudiant;

import java.io.Serializable;
import java.util.ArrayList;

public class EtudiantResponse implements Serializable {

    private Etudiant etudiant = null;
    private ArrayList<AbsenceResponse> absences = null;
    private boolean isExclus = false;
    private int absencesNonJustifier = 0;
    private int absencesJusifiter = 0;
    private int nombreAbsences = 0;

    public EtudiantResponse(Etudiant etudiant, ArrayList<AbsenceResponse> absences, boolean isExclus,
                            int absencesNonJustifier, int absencesJusifiter, int nombreAbsences)
    {
        this.etudiant = etudiant;
        this.absences = absences;
        this.isExclus = isExclus;
        this.absencesNonJustifier = absencesNonJustifier;
        this.absencesJusifiter = absencesJusifiter;
        this.nombreAbsences = nombreAbsences;
    }

    public EtudiantResponse()
    {

    }

    public Etudiant getEtudiant()
    {
        return etudiant;
    }

    public ArrayList<AbsenceResponse> getAbsences()
    {
        return absences;
    }

    public boolean isExclus()
    {
        return isExclus;
    }

    public int getAbsencesNonJustifier()
    {
        return absencesNonJustifier;
    }

    public int getAbsencesJusifiter()
    {
        return absencesJusifiter;
    }

    public int getNombreAbsences()
    {
        return nombreAbsences;
    }

    public void setEtudiant(Etudiant etudiant)
    {
        this.etudiant = etudiant;
    }

    public void setAbsences(ArrayList<AbsenceResponse> absences)
    {
        this.absences = absences;
    }

    public void setExclus(boolean isExclus)
    {
        this.isExclus = isExclus;
    }

    public void setAbsencesNonJustifier(int absencesNonJustifier)
    {
        this.absencesNonJustifier = absencesNonJustifier;
    }

    public void setAbsencesJusifiter(int absencesJusifiter)
    {
        this.absencesJusifiter = absencesJusifiter;
    }

    public void setNombreAbsences(int nombreAbsences)
    {
        this.nombreAbsences = nombreAbsences;
    }
}
