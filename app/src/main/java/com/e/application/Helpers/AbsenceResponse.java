package com.e.application.Helpers;

import com.e.application.Model.Absence;
import com.e.application.Model.Justification;

import java.io.Serializable;
import java.util.ArrayList;

public class AbsenceResponse implements Serializable {

    private Absence absence = null;
    private boolean isJustifier = false;
    private ArrayList<Justification> justifications = null;

    public AbsenceResponse(Absence absence, boolean isJustifier, ArrayList<Justification> justifications)
    {
        this.absence = absence;
        this.isJustifier = isJustifier;
        this.justifications = justifications;
    }

    public AbsenceResponse()
    {

    }

    public Absence getAbsence()
    {
        return absence;
    }

    public boolean isJustifier()
    {
        return isJustifier;
    }

    public ArrayList<Justification> getJustification()
    {
        return justifications;
    }

    public void setAbsence(Absence absence)
    {
        this.absence = absence;
    }

    public void setJustifier(boolean isJustifier)
    {
        this.isJustifier = isJustifier;
    }

    public void setJustification(ArrayList<Justification> justifications)
    {
        this.justifications = justifications;
    }
}
