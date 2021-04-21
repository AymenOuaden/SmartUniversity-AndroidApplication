package com.e.application.Adapters.AdapterEtudiant;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Etudiant.EtudiantFragmentReleveAbsences;
import com.e.application.Helpers.EtudiantAfficherAbsences_Etudiant;
import com.e.application.R;

import java.util.ArrayList;

public class Etudiant_Adapter_FragmentReleveAbsence extends RecyclerView.Adapter<Etudiant_Adapter_FragmentReleveAbsence.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String recy;
    private ArrayList<EtudiantAfficherAbsences_Etudiant> etudiantAfficherAbsenceEtudiants;

    // data is passed into the constructor
    public Etudiant_Adapter_FragmentReleveAbsence(EtudiantFragmentReleveAbsences context, ArrayList<EtudiantAfficherAbsences_Etudiant> etudiantAfficherAbsenceEtudiants, String recy) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.etudiantAfficherAbsenceEtudiants = etudiantAfficherAbsenceEtudiants;
        this.recy = recy;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (recy.equals("afficher_default")) {
            view = mInflater.inflate(R.layout.etudiant_recyclerview_releve_absence, parent, false);
            return new ViewHolder(view);
        } else {
            view = mInflater.inflate(R.layout.etudiant_recyclerview_show_absences, parent, false);
            return new ViewHolder(view);
        }

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView num, etat, date, type, jour, heure, code_module, nbr_absence;
        ImageView display;
        ImageView option_justifier, show_justification;

        ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            code_module = itemView.findViewById(R.id.code_module);
            type = itemView.findViewById(R.id.type);
            jour = itemView.findViewById(R.id.jour);
            heure = itemView.findViewById(R.id.heure);
            display = itemView.findViewById(R.id.display);
            date = itemView.findViewById(R.id.date);
            etat = itemView.findViewById(R.id.etat);
            nbr_absence = itemView.findViewById(R.id.nbr_absence);

            if (recy.equals("afficher_default")) {

                display.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.Display(v, getAdapterPosition());
                    }
                });

            } else {

                option_justifier = itemView.findViewById(R.id.option_justifier);
                show_justification = itemView.findViewById(R.id.show_justification);

                option_justifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.gererJustification(
                                getNumeroAbsence(getAdapterPosition()),
                                "FragmentUploadJustification"
                        );
                    }
                });

                show_justification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.gererJustification(
                                getNumeroAbsence(getAdapterPosition()),
                                "FragmentDisplayJustification"
                        );
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
        }
    }

    // binds the data to the TextView in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (recy.equals("afficher_default")) {
            holder.code_module.setText(etudiantAfficherAbsenceEtudiants.get(position).getCode_module());
            holder.type.setText(String.valueOf(etudiantAfficherAbsenceEtudiants.get(position).getType()));
            holder.jour.setText(String.valueOf(etudiantAfficherAbsenceEtudiants.get(position).getJour()));
            holder.heure.setText(String.valueOf(etudiantAfficherAbsenceEtudiants.get(position).getHeure()));
            holder.nbr_absence.setText(String.valueOf(etudiantAfficherAbsenceEtudiants.get(position).getNbr_absences()));
        } else {
            holder.num.setText(String.valueOf(position + 1));
            holder.type.setText(String.valueOf(etudiantAfficherAbsenceEtudiants.get(position).getType()));
            holder.code_module.setText(etudiantAfficherAbsenceEtudiants.get(position).getCode_module());
            holder.date.setText(etudiantAfficherAbsenceEtudiants.get(position).getDate_absence());
            holder.etat.setText(etudiantAfficherAbsenceEtudiants.get(position).getEtat());
            if (etudiantAfficherAbsenceEtudiants.get(position).isHistorique()) {
                holder.option_justifier.setVisibility(View.GONE);
            } else {
                holder.show_justification.setVisibility(View.GONE);
            }

            switch (etudiantAfficherAbsenceEtudiants.get(position).getEtat_default()) {
                case "valide":
                    holder.option_justifier.setVisibility(View.GONE);
                    holder.etat.setTextColor(Color.GREEN);
                    break;
                case "refuse":
                    holder.etat.setTextColor(Color.RED);
                    break;
                case "nonTraite":
                    holder.etat.setTextColor(Color.BLUE);
                    break;
                default:
                    holder.etat.setTextColor(Color.BLACK);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (etudiantAfficherAbsenceEtudiants != null) {
            return etudiantAfficherAbsenceEtudiants.size();
        } else {
            return 0;
        }
    }


    private int getNumeroAbsence(int id) {
        return etudiantAfficherAbsenceEtudiants.get(id).getNumero_absence();
    }

    // allows clicks events to be caught
    public void setClickListener(EtudiantFragmentReleveAbsences itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void Display(View view, int position);

        void gererJustification(int numero_absence, String redirection);

    }

}
