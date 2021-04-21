package com.e.application.Helpers;

import com.e.application.Model.NotificationChangementSeance;
import com.e.application.Model.NotificationSeanceSupp;
import com.e.application.Model.Seance;

import java.util.Date;

public class AfficherNotification {

    protected int id_notification;
    protected int id_utilisateur;
    protected boolean is_vue;
    protected Date date_creation;
    protected Seance.Etat_Demande etat_demande_notifier;
    protected String type_notification;
    protected String code_seance;
    protected String jour;
    protected String heure;


    public AfficherNotification(NotificationSeanceSupp notificationSeanceSupp, String type, String code_seance, String jour, String heure) {
        this.id_notification = notificationSeanceSupp.getId_notification();
        this.id_utilisateur = notificationSeanceSupp.getId_utilisateur();
        this.is_vue = notificationSeanceSupp.isIs_vue();
        this.date_creation = notificationSeanceSupp.getDate_creation();
        this.etat_demande_notifier = notificationSeanceSupp.getEtat_demande_notifier();
        this.type_notification = type;
        this.code_seance = code_seance;
        this.jour = jour;
        this.heure = heure;
    }

    public AfficherNotification(NotificationChangementSeance notificationChangementSeance, String type, String code_seance, String jour, String heure) {
        this.id_notification = notificationChangementSeance.getId_notification();
        this.id_utilisateur = notificationChangementSeance.getId_utilisateur();
        this.is_vue = notificationChangementSeance.isIs_vue();
        this.date_creation = notificationChangementSeance.getDate_creation();
        this.etat_demande_notifier = notificationChangementSeance.getEtat_demande_notifier();
        this.type_notification = type;
        this.code_seance = code_seance;
        this.jour = jour;
        this.heure = heure;
    }

    public int getId_notification() {
        return id_notification;
    }

    public void setId_notification(int id_notification) {
        this.id_notification = id_notification;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
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

    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }

    public Seance.Etat_Demande getEtat_demande_notifier() {
        return etat_demande_notifier;
    }

    public void setEtat_demande_notifier(Seance.Etat_Demande etat_demande_notifier) {
        this.etat_demande_notifier = etat_demande_notifier;
    }

    public String getType_notification() {
        return type_notification;
    }

    public void setType_notification(String type_notification) {
        this.type_notification = type_notification;
    }

    public String getCode_seance() {
        return code_seance;
    }

    public void setCode_seance(String code_seance) {
        this.code_seance = code_seance;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    @Override
    public String toString() {
        return "AfficherNotification{" +
                "id_notification=" + id_notification +
                ", id_utilisateur=" + id_utilisateur +
                ", is_vue=" + is_vue +
                ", date_creation=" + date_creation +
                ", etat_demande_notifier=" + etat_demande_notifier +
                ", type_notification='" + type_notification + '\'' +
                ", code_seance='" + code_seance + '\'' +
                ", jour='" + jour + '\'' +
                ", heure='" + heure + '\'' +
                '}';
    }
}
