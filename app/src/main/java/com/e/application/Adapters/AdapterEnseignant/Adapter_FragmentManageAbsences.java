package com.e.application.Adapters.AdapterEnseignant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentManageAbsences;
import com.e.application.Model.Seance;
import com.e.application.R;

import java.util.ArrayList;

import static com.e.application.R.id;
import static com.e.application.R.layout;
import static java.lang.String.valueOf;

public class Adapter_FragmentManageAbsences extends RecyclerView.Adapter<Adapter_FragmentManageAbsences.ViewHolder> {

    private ArrayList<Seance> mData;
    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public Adapter_FragmentManageAbsences(Context context,ArrayList<Seance> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.recyclerview_seances_liste, null);
        return new Adapter_FragmentManageAbsences.ViewHolder(view);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numero, code_module, annee, specialite, groupe, heure, jour, type_seance;

        ViewHolder(final View itemView) {
            super(itemView);
            numero = itemView.findViewById(id.numero);
            code_module = itemView.findViewById(id.code_module);
            annee = itemView.findViewById(id.annee);
            specialite = itemView.findViewById(id.specialite);
            groupe = itemView.findViewById(id.groupe);
            heure = itemView.findViewById(id.heure);
            jour = itemView.findViewById(id.jour);
            type_seance = itemView.findViewById(id.type_seance);
            Button select = itemView.findViewById(id.b);
            itemView.setOnClickListener(this);
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mClickListener.gererReleveAbsences(v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.numero.setText(valueOf(position + 1));
        holder.code_module.setText(mData.get(position).getCode_module());
        holder.annee.setText(mData.get(position).getAnnee().toString());
        holder.specialite.setText(mData.get(position).getSpecialite().toString());
        String jour = mData.get(position).getJour().toString();
        switch (jour) {
            case "dimanche":
                holder.jour.setText(mInflater.getContext().getResources().getString(R.string.dimanche));
                break;
            case "lundi":
                holder.jour.setText(mInflater.getContext().getResources().getString(R.string.lundi));
                break;
            case "mardi":
                holder.jour.setText(mInflater.getContext().getResources().getString(R.string.mardi));
                break;
            case "mercredi":
                holder.jour.setText(mInflater.getContext().getResources().getString(R.string.mercredi));
                break;
            case "jeudi":
                holder.jour.setText(mInflater.getContext().getResources().getString(R.string.jeudi));
                break;
        }
        holder.heure.setText(mData.get(position).getHeure());
        holder.type_seance.setText(mData.get(position).getType().toString());
        holder.groupe.setText(valueOf(mData.get(position).getGroupe()));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(FragmentManageAbsences fragmentConsulterSeances) {
        this.mClickListener = fragmentConsulterSeances;
    }

    public interface ItemClickListener {
        void gererReleveAbsences(View view, int position);
    }

}
