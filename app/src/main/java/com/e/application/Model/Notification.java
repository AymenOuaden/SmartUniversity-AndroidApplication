package com.e.application.Model;

import com.e.application.Model.Seance.Etat_Demande;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Notification {
    protected int id_notification;
    protected int id_utilisateur;
    protected boolean is_vue;
    protected Date date_creation;
    protected Etat_Demande etat_demande_notifier;

    public Notification() {

    }

    public Notification(int id_notification, int id_utilisateur, boolean is_vue, Date date_creation, Seance.Etat_Demande etat_demande_notifier) {
        this.id_notification = id_notification;
        this.id_utilisateur = id_utilisateur;
        this.is_vue = is_vue;
        this.date_creation = date_creation;
        this.etat_demande_notifier = etat_demande_notifier;
    }

    public int getId_notification() {
        return id_notification;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_notification(int id_notification) {
        this.id_notification = id_notification;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public boolean isIs_vue() {
        return is_vue;
    }

    public void setIs_vue(boolean is_vue) {
        this.is_vue = is_vue;
    }

    public Date getDate_creation() {
        return date_creation;
    }

    public Etat_Demande getEtat_demande_notifier() {
        return etat_demande_notifier;
    }

    public void setEtat_demande_notifier(Etat_Demande etat_demande_notifier) {
        this.etat_demande_notifier = etat_demande_notifier;
    }

    public void setDate_creation(String date_creation) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            this.date_creation = formatter.parse(date_creation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
