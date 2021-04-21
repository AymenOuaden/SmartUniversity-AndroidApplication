package com.e.application.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChefDepartement extends Enseignant
{
	private Date date_nomination = null;
	
	public ChefDepartement()
	{
		
	}
	
	public ChefDepartement(Utilisateur utilisateur, Enseignant enseignant, Date date_nomination)
	{
		super(utilisateur, enseignant.getGrade(), enseignant.getCode_departement());
		this.date_nomination = date_nomination;
	}

	public Date getDate_nomination()
	{
		return date_nomination;
	}

	public void setDate_nomination(String date_nomination)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			this.date_nomination = formatter.parse(date_nomination);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
}
