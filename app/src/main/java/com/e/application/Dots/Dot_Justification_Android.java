package com.e.application.Dots;

import java.util.Arrays;

public class Dot_Justification_Android {

	private int numero_absence = 0;
	private byte[] image= null;
	private String extension ="extension";

	public Dot_Justification_Android()
	{
		
	}
	
	public Dot_Justification_Android(int numero_absence, byte[] image, String extension)
	{
		this.numero_absence = numero_absence;
		this.image = image;
		this.extension=extension;
	}
	

	public int getNumero_absence() {
		return numero_absence;
	}

	public void setNumero_absence(int numero_absence) {
		this.numero_absence = numero_absence;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "Dot_Justification_Android{" +
				"numero_absence=" + numero_absence +
				", image=" + Arrays.toString(image) +
				", extension='" + extension + '\'' +
				'}';
	}
}
