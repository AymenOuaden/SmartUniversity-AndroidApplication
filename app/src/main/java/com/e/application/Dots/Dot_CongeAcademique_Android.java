package com.e.application.Dots;

import java.util.Arrays;

public class Dot_CongeAcademique_Android {
    private int id_etudiant = 0;
    private byte[] image = null;
    private String extension = null;

    public Dot_CongeAcademique_Android() {

    }

    public Dot_CongeAcademique_Android(int id_etudiant, byte[] image, String extension) {
        this.id_etudiant = id_etudiant;
        this.image = image;
        this.extension = extension;
    }

    public int getId_etudiant() {
        return id_etudiant;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
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
        return "Dot_Justification_Android [id_etudiant=" + id_etudiant + ", image=" + Arrays.toString(image)
                + ", extension=" + extension + "]";
    }
}
