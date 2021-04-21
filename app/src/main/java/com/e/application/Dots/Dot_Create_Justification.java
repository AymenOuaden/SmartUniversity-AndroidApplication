package com.e.application.Dots;


public class Dot_Create_Justification
{
    private int numero_absence = 0;
    private String date_justification = null;

    public Dot_Create_Justification()
    {

    }

    public Dot_Create_Justification(int numero_absence, String date_justification)
    {
        this.numero_absence = numero_absence;
        this.date_justification = date_justification;
    }

    public int getNumero_absence()
    {
        return numero_absence;
    }

    public String getDate_justification()
    {
        return date_justification;
    }

    public void setNumero_absence(int numero_absence)
    {
        this.numero_absence = numero_absence;
    }

   /* public void setDate_justification(String date_justification)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            this.date_justification = formatter.parse(date_justification);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }*/

   public void setDate_justification(String date_justification){
       this.date_justification = date_justification;
   }

    @Override
    public String toString()
    {
        return "Dot_Create_Justification [numero_absence=" + numero_absence + ", date_justification="
                + date_justification + "]";
    }



}
