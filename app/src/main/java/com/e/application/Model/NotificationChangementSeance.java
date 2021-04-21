package com.e.application.Model;

import com.e.application.Model.Seance.Etat_Demande;

import java.util.Date;

public class NotificationChangementSeance extends Notification
{
	private ChangementSeance changementSeance;

	public NotificationChangementSeance()
	{

	}

	public NotificationChangementSeance(int id_notification, int id_utilisateur, boolean isVue, Date dateCreation,
			Etat_Demande etat_demande_notifier, ChangementSeance changementSeance)
	{
		super(id_notification, id_utilisateur, isVue, dateCreation, etat_demande_notifier);
		this.changementSeance = changementSeance;
	}

	public NotificationChangementSeance(Notification notification, ChangementSeance changementSeance)
	{
		super(notification.id_notification, notification.id_utilisateur, notification.is_vue,
				notification.date_creation, notification.etat_demande_notifier);
		this.changementSeance = changementSeance;
	}

	public ChangementSeance getChangementSeance()
	{
		return changementSeance;
	}

	public void setChangementSeance(ChangementSeance changementSeance)
	{
		this.changementSeance = changementSeance;
	}
}
