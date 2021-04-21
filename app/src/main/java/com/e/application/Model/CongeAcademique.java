package com.e.application.Model;


import java.util.Arrays;

public class CongeAcademique
{
	private int numero_conge_academique;
	private int id_etudiant;
	private byte[] fichier = null;
	private String extension = null;
	private Seance.Etat_Demande etat_demande;

	public CongeAcademique()
	{
		
	}

	public CongeAcademique(int numero_conge_academique, int id_etudiant, byte[] fichier, String extension,
			Seance.Etat_Demande etat_demande)
	{
		this.numero_conge_academique = numero_conge_academique;
		this.id_etudiant = id_etudiant;
		this.fichier = fichier;
		this.extension = extension;
		this.etat_demande = etat_demande;
	}

	public int getNumero_conge_academique()
	{
		return numero_conge_academique;
	}

	public int getId_etudiant()
	{
		return id_etudiant;
	}

	public byte[] getFichier()
	{
		return fichier;
	}

	public String getExtension()
	{
		return extension;
	}

	public Seance.Etat_Demande getEtat_demande()
	{
		return etat_demande;
	}

	public void setNumero_conge_academique(int numero_conge_academique)
	{
		this.numero_conge_academique = numero_conge_academique;
	}

	public void setId_etudiant(int id_etudiant)
	{
		this.id_etudiant = id_etudiant;
	}

	public void setFichier(byte[] fichier)
	{
		this.fichier = fichier;
	}

	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	public void setEtat_demande(Seance.Etat_Demande etat_demande)
	{
		this.etat_demande = etat_demande;
	}

	@Override
	public String toString() {
		return "CongeAcademique{" +
				"numero_conge_academique=" + numero_conge_academique +
				", id_etudiant=" + id_etudiant +
				", fichier=" + Arrays.toString(fichier) +
				", extension='" + extension + '\'' +
				", etat_demande=" + etat_demande +
				'}';
	}
}
