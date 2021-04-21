package com.e.application.Model;

import com.e.application.Model.Seance.Etat_Demande;

import java.util.Date;


public class NotificationSeanceSupp extends Notification {
    private SeanceSupp seanceSupp;

    public NotificationSeanceSupp() {

    }

    public NotificationSeanceSupp(int id_notification, int id_utilisateur, boolean isVue, Date dateCreation,
                                  Etat_Demande etat_demande_notifier, SeanceSupp seanceSupp) {
        super(id_notification, id_utilisateur, isVue, dateCreation, etat_demande_notifier);
        this.seanceSupp = seanceSupp;
    }

    public NotificationSeanceSupp(Notification notification, SeanceSupp seanceSupp) {
        super(notification.id_notification, notification.id_utilisateur, notification.is_vue,
                notification.date_creation, notification.etat_demande_notifier);
        this.seanceSupp = seanceSupp;
    }

    public SeanceSupp getSeanceSupp() {
        return seanceSupp;
    }

    public void setSeanceSupp(SeanceSupp seanceSupp) {
        this.seanceSupp = seanceSupp;
    }
}
