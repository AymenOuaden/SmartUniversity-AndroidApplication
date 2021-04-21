package com.e.application.Adapters.AdapterEnseignant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentAddExtraClass;
import com.e.application.Model.SeanceSupp;
import com.e.application.R;

import java.util.ArrayList;

import static com.e.application.R.id;
import static com.e.application.R.layout;

public class Adapter_Options_classes extends RecyclerView.Adapter<Adapter_Options_classes.ViewHolder> {

    private ArrayList<SeanceSupp> mData;
    private FragmentAddExtraClass mClickListener;
    private LayoutInflater mInflater;


    public Adapter_Options_classes(Context context, ArrayList<SeanceSupp> data) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(layout.recyclerview_options_classes, null);
        return new Adapter_Options_classes.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String jour = mData.get(position).getJour().toString();
        String jour_affiche = jour;
        switch (jour) {
            case "dimanche":
                jour_affiche = mInflater.getContext().getResources().getString(R.string.dimanche);
                break;
            case "lundi":
                jour_affiche = mInflater.getContext().getResources().getString(R.string.lundi);
                break;
            case "mardi":
                jour_affiche = mInflater.getContext().getResources().getString(R.string.mardi);
                break;
            case "mercredi":
                jour_affiche = mInflater.getContext().getResources().getString(R.string.mercredi);
                break;
            case "jeudi":
                jour_affiche = mInflater.getContext().getResources().getString(R.string.jeudi);
                break;
        }

        holder.jour.setText(jour_affiche);

        holder.heure.setText(mData.get(position).getHeure());

        String etat = mData.get(position).getEtat_seance().toString();
        String etat_afficher = etat;
        switch (etat) {
            case "valide":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.valide);
                break;
            case "refuse":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.refuse);
                break;
            case "nonTraite":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.non_traite);
                break;
        }
        holder.etat.setText(etat_afficher);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setClickListener(FragmentAddExtraClass fragmentAddExtraClass) {
        this.mClickListener = fragmentAddExtraClass;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView heure, jour, etat;
        ImageView supprimer;

        ViewHolder(final View itemView) {
            super(itemView);
            heure = itemView.findViewById(id.heure);
            jour = itemView.findViewById(id.jour);
            etat = itemView.findViewById(id.etat);
            supprimer = itemView.findViewById(id.supprimer);

            itemView.setOnClickListener(this);
            supprimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.supprimer(v, getCodeSeance(getAdapterPosition()), getCodeSeanceSupp(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

    // convenience method for getting data at click position
    private String getCodeSeance(int position) {
        return mData.get(position).getCode_seance();
    }

    private int getCodeSeanceSupp(int position) {
        return mData.get(position).getCode_seance_supp();
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void supprimer(View view, String code_seance, int code_seance_supp);

    }

}
