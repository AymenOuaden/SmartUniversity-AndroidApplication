package com.e.application.Model;

import java.io.Serializable;

public class Justification implements Serializable {

    private int numero_justification = 0;
    private int numero_absence = 0;
    private byte[] fichier = null;
    private String extension = null;
    private String date_justification = null;
    private Seance.Etat_Demande etat_justification;

    public Justification()
    {

    }

    public Justification(int numero_justification, int numero_absence, byte[] fichier,
                         String extension ,String date_justification,Seance.Etat_Demande etat_justification)
    {
        this.numero_justification = numero_justification;
        this.numero_absence = numero_absence;
        this.fichier = fichier;
        this.extension = extension;
        this.date_justification = date_justification;
        this.etat_justification = etat_justification;
    }

    public int getNumero_justification()
    {
        return numero_justification;
    }

    public int getNumero_absence()
    {
        return numero_absence;
    }

    public String getDate_justification()
    {
        return date_justification;
    }

    public Seance.Etat_Demande getEtat_justification()
    {
        return etat_justification;
    }

    public void setNumero_justification(int numero_justification)
    {
        this.numero_justification = numero_justification;
    }

    public void setNumero_absence(int numero_absence)
    {
        this.numero_absence = numero_absence;
    }

    public byte[] getFichier()
    {
        return fichier;
    }

    public void setFichier(byte[] fichier)
    {
        this.fichier = fichier;
    }

    public void setDate_justification(String date_justification)
    {
        /*SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            this.date_justification = formatter.parse(date_justification);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }*/
        this.date_justification = date_justification;
    }

    public void setEtat_justification(Seance.Etat_Demande etat_justification)
    {
        this.etat_justification = etat_justification;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "Justification{" +
                "numero_justification=" + numero_justification +
                ", numero_absence=" + numero_absence +
                ", extension='" + extension + '\'' +
                ", date_justification='" + date_justification + '\'' +
                ", etat_justification=" + etat_justification +
                '}';
    }
}
