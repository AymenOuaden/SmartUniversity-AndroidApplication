package com.e.application.Adapters.AdapterEnseignant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentTakePresences;
import com.e.application.Model.Etudiant;
import com.e.application.R;

import java.util.ArrayList;

public class Adapter_FragmentMarquerPresence extends RecyclerView.Adapter<Adapter_FragmentMarquerPresence.ViewHolder> {

    private ArrayList<Etudiant> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<Integer> ids_absent;

    // data is passed into the constructor
    public Adapter_FragmentMarquerPresence(FragmentTakePresences context, ArrayList<Etudiant> data, ArrayList<Integer> liste_absences) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.mData = data;
        this.ids_absent = liste_absences;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_etudiants, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.num.setText(String.valueOf(position + 1));
        holder.id.setText(String.valueOf(mData.get(position).getId_utilisateur()));
        holder.nom.setText(mData.get(position).getNom());
        holder.prenom.setText(mData.get(position).getPrenom());

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.check.isChecked()) {
                    holder.check.setButtonDrawable(R.drawable.icon_chekbox_full_colorprimarydark);
                    mClickListener.Check(v, getItem(position));
                } else {
                    holder.check.setButtonDrawable(R.drawable.icon_chekbox_empty_colorprimarydark);
                    mClickListener.inCheck(v, getItem(position));
                }
            }
        });
    }


    public void add(int id) {
        ids_absent.add(id);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView num, id, nom, prenom;
        CheckBox check;

        ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            id = itemView.findViewById(R.id.id);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            check = itemView.findViewById(R.id.chk);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    // convenience method for getting data at click position
    private int getItem(int id) {
        return mData.get(id).getId_utilisateur();
    }

    // allows clicks events to be caught
    public void setClickListener(FragmentTakePresences itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void Check(View view, int id);

        void inCheck(View view, int id);

    }

}
