package com.e.application.Adapters.AdapterEnseignant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentExcludedStudents;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.R;

import java.util.ArrayList;

public class Adapter_FragmentEtudiantsExclus extends RecyclerView.Adapter<Adapter_FragmentEtudiantsExclus.ViewHolder> {

    private ArrayList<EtudiantResponse> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public Adapter_FragmentEtudiantsExclus(FragmentExcludedStudents context, ArrayList<EtudiantResponse> etudiantResponses) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.mData = etudiantResponses;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_etudiants_exclus, parent, false);
        return new ViewHolder(view);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView num, nom, prenom, nombre_absence_non_justife, getNombre_absence_justife;

        ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            nombre_absence_non_justife = itemView.findViewById(R.id.nombre_absences_non_justifié);
            getNombre_absence_justife = itemView.findViewById(R.id.nombre_absences_justifié);
        }

        @Override
        public void onClick(View v) {
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.num.setText(String.valueOf(position + 1));
        holder.nom.setText(mData.get(position).getEtudiant().getNom());
        holder.prenom.setText(mData.get(position).getEtudiant().getPrenom());
        holder.nombre_absence_non_justife.setText(String.valueOf(mData.get(position).getAbsencesNonJustifier()));
        holder.getNombre_absence_justife.setText(String.valueOf(mData.get(position).getAbsencesJusifiter()));
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
    }

}
