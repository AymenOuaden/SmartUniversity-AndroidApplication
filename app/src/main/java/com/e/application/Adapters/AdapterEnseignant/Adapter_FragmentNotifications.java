package com.e.application.Adapters.AdapterEnseignant;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentNotificationTeacher;
import com.e.application.Helpers.AfficherNotification;
import com.e.application.Model.Seance;
import com.e.application.R;

import java.util.ArrayList;

public class Adapter_FragmentNotifications extends RecyclerView.Adapter<Adapter_FragmentNotifications.ViewHolder> {

    private ArrayList<AfficherNotification> mData;
    private LayoutInflater mInflater;
    private FragmentNotificationTeacher context;
    private ArrayList<Seance> seances;

    // data is passed into the constructor
    public Adapter_FragmentNotifications(FragmentNotificationTeacher context, ArrayList<AfficherNotification> afficherNotifications, ArrayList<Seance> seances) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.mData = afficherNotifications;
        this.context = context;
        this.seances = seances;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_notification, parent, false);
        return new ViewHolder(view);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView notification_texte, subject, etat, date;
        RelativeLayout notification;
        ImageView supprimer;

        ViewHolder(View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            subject = itemView.findViewById(R.id.subject);
            notification_texte = itemView.findViewById(R.id.notification_texte);
            date = itemView.findViewById(R.id.date);
            etat = itemView.findViewById(R.id.etat);
            supprimer = itemView.findViewById(R.id.supprimer);

            supprimer.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    context.supprimer(getAdapterPosition());
                }
            });

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.setVue(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (!mData.get(position).isIs_vue()) {
            holder.notification.setBackground(context.getResources().getDrawable(R.drawable.box_full_greyblue_stroke_black));
        }

        Seance seance = null;
        for (Seance s : seances) {
            System.out.println("le code est " + s.getCode_seance());
            System.out.println("mdata" + mData.get(position).getCode_seance());
            if (s.getCode_seance().equals(mData.get(position).getCode_seance())) {
                seance = s;
                System.out.println("seance pas vide");
            }
        }
        if (seance != null) {
            System.out.println("la seance est" + seance.toString());
            String notification = "";
            String etat;
            if (mData.get(position).getType_notification().equals("SeanceSupp")) {
                holder.subject.setText(context.getResources().getString(R.string.seance_supp));
                etat = context.getResources().getString(R.string.demande_de) + " " + context.getResources().getString(R.string.seance_supp);
            } else {
                holder.subject.setText(context.getResources().getString(R.string.changement_seance));
                etat = context.getResources().getString(R.string.demande_de) + " " + context.getResources().getString(R.string.changement_seance);
            }

            Seance.Etat_Demande b = mData.get(position).getEtat_demande_notifier();
            System.out.println(b);
            if (b == Seance.Etat_Demande.valide) {
                etat = etat + " " + context.getResources().getString(R.string.demande_ok);
                holder.etat.setText(etat);
                holder.etat.setTextColor(context.getResources().getColor(R.color.colorAccent));
            } else {
                etat = etat + " " + context.getResources().getString(R.string.demande_not_ok);
                holder.etat.setText(etat);
                holder.etat.setTextColor(context.getResources().getColor(R.color.red));
            }

            assert seance != null;
            System.out.println("avant holdr" + seance.getCode_module());
            String module = context.getResources().getString(R.string.code_module) + " : " + seance.getCode_module();
            String jour = context.getResources().getString(R.string.jour) + " : " + mData.get(position).getJour();
            String heure = context.getResources().getString(R.string.heure) + " : " + mData.get(position).getHeure();
            String groupe = context.getResources().getString(R.string.groupe) + " : " + seance.getGroupe();
            String specialite = context.getResources().getString(R.string.specialite) + " : " + seance.getSpecialite();
            String annee = context.getResources().getString(R.string.annee) + " : " + seance.getAnnee();
            notification = notification + module + "  ,  " + jour + "  ,  " + heure + "  ,  " + annee + "  ,  " + specialite + "  ,  " + groupe;

            holder.notification_texte.setText(notification);
            holder.date.setText(String.valueOf(mData.get(position).getDate_creation()));
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    // allows clicks events to be caught
    public void setClickListener() {
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void supprimer(int position);

        void setVue(int position);

    }

}
