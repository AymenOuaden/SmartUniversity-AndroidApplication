package com.e.application.SharedPrefrences;

import android.content.Context;
import android.content.SharedPreferences;

import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.CongeAcademique;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Justification;
import com.e.application.Model.Module;
import com.e.application.Model.Seance;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LoginPreferencesConfig {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LoginPreferencesConfig(Context applicationContext) {
        String loginPreferences = "loginPreferences";
        sharedPreferences = applicationContext.getSharedPreferences(loginPreferences, Context.MODE_PRIVATE);
    }


    public void setLoginStatusEnseignant(int status, Enseignant enseignant, String token) {
        editor = this.sharedPreferences.edit();
        editor.putInt("loginStatuKey", status);
        editor.putString("token", token);
        // pour ensignant ----------
        Gson gson = new Gson();
        String json = gson.toJson(enseignant);
        editor.putString("enseignant", json);
        editor.commit();
    }




    public void setLoginStatusEtudiant(int status, Etudiant etudiant, String token) {
        editor = this.sharedPreferences.edit();
        editor.putInt("loginStatuKey", status);
        editor.putString("token", token);
        // pour ensignant ----------
        Gson gson = new Gson();
        String json = gson.toJson(etudiant);
        editor.putString("etudiant", json);
        editor.commit();
    }

    public void setSeances(ArrayList<Seance> seances) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // array seances
        String seances_j = gson.toJson(seances);
        editor.putString("seances", seances_j);
        editor.commit();
    }

    public void setEnseignant(Enseignant enseignant) {
        editor = this.sharedPreferences.edit();
        // pour ensignant ----------
        Gson gson = new Gson();
        String json = gson.toJson(enseignant);
        editor.putString("enseignant", json);
        editor.commit();
    }

    public void setCongeAcademique(CongeAcademique congeAcademique) {
        editor = this.sharedPreferences.edit();
        // pour ensignant ----------
        Gson gson = new Gson();
        String json = gson.toJson(congeAcademique);
        editor.putString("congeAcademique", json);
        editor.commit();
    }

    public String getCongeAcademique() {
        return sharedPreferences.getString("congeAcademique", "");
    }

    public void setJustification(Justification justification) {
        editor = this.sharedPreferences.edit();
        // pour ensignant ----------
        Gson gson = new Gson();
        String json = gson.toJson(justification);
        editor.putString("justification", json);
        editor.commit();
    }

    public String getJustification() {
        return sharedPreferences.getString("justification", "");
    }

    public void setListeEtudiantsEtLeursAbsences(ArrayList<EtudiantResponse> liste_etudiants_absences) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // array etudiants
        String etudiants_absences_j = gson.toJson(liste_etudiants_absences);
        editor.putString("liste_etudiants_absences", etudiants_absences_j);
        editor.commit();
    }

    public String getListeEtudiantsEtLeursAbsences() {
        return sharedPreferences.getString("liste_etudiants_absences", "vide");
    }


    public void setSeance(Seance seance) {
        editor = this.sharedPreferences.edit();

        Gson gson = new Gson();
        // seance
        String seance_j = gson.toJson(seance);
        editor.putString("seance", seance_j);
        editor.commit();

    }

    public void setEtudiant(Etudiant etudiant) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // seance
        String etudiant_j = gson.toJson(etudiant);
        editor.putString("etudiant", etudiant_j);
        editor.commit();
    }

    public void setAbsence(Absence absence) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // seance
        String absence_j = gson.toJson(absence);
        editor.putString("icon_manage_absences", absence_j);
        editor.commit();
    }

    public void clearAll() {
        editor = this.sharedPreferences.edit();
        editor.clear().commit();
    }



    public String getEtudiant() {
        return sharedPreferences.getString("etudiant", "null");
    }

    public String getAbsence() {
        return sharedPreferences.getString("icon_manage_absences", "pas de seances");
    }


    public String getSeances() {
        return sharedPreferences.getString("seances", "pas de seances");
    }


    public String getSeance() {
        return sharedPreferences.getString("seance", "");
    }


    public String getEnseignant() {
        return sharedPreferences.getString("enseignant", "null");
    }


    public String getTest() {
        return sharedPreferences.getString("test", "abc");
    }




    public int getLoginStatuKey() {
        return sharedPreferences.getInt("loginStatuKey", 0);
    }

    public String getToken() {
        return sharedPreferences.getString("token", "test");
    }


    //----------------------------------------etudiants--------------------------------------

    public void setModule(Module module) {
        editor = this.sharedPreferences.edit();

        Gson gson = new Gson();
        // seance
        String module_j = gson.toJson(module);
        editor.putString("module", module_j);
        editor.commit();

    }


    public void setAbsenceDepartementResponses(ArrayList<AbsenceDepartementResponse> absenceDepartementResponses) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // array etudiant_absence_seance_module_justification
        String absence_departement_responses_j = gson.toJson(absenceDepartementResponses);
        editor.putString("liste_absences_justification_seance_module", absence_departement_responses_j);
        editor.commit();
    }


    public void setSeanceResponsesEtudiant(ArrayList<SeanceResponse> seanceResponses) {
        editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        // array etudiant_absence_seance_module_justification
        String seance_response_j = gson.toJson(seanceResponses);
        editor.putString("seance_response", seance_response_j);
        editor.commit();
    }

    public String getSeanceResponseEtudiant() {
        return sharedPreferences.getString("seance_response", "");
    }


    public String getAbsenceDepartementResponses() {
        return sharedPreferences.getString("liste_absences_justification_seance_module", "");
    }

}











