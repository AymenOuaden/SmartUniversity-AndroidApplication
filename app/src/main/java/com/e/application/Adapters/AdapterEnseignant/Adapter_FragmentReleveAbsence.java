package com.e.application.Adapters.AdapterEnseignant;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Enseignant.FragmentReleveAbsences;
import com.e.application.Helpers.EtudiantAfficherAbsences_Enseignant;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Etudiant;
import com.e.application.R;

import java.util.ArrayList;

public class Adapter_FragmentReleveAbsence extends RecyclerView.Adapter<Adapter_FragmentReleveAbsence.ViewHolder> {

    private ArrayList<EtudiantResponse> liste_etudiants_et_absences;
    private ArrayList<EtudiantAfficherAbsences_Enseignant> EtudiantAfficherAbsences_Enseignant;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String recy;

    // data is passed into the constructor
    public Adapter_FragmentReleveAbsence(FragmentReleveAbsences context, ArrayList<EtudiantResponse> liste_etudiants_et_absences, String recy) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.liste_etudiants_et_absences = liste_etudiants_et_absences;
        this.recy = recy;
    }

    public Adapter_FragmentReleveAbsence(FragmentReleveAbsences context, ArrayList<EtudiantAfficherAbsences_Enseignant> etudiantAfficherAbsencesEnseignantArrayList, String recy, int help) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.EtudiantAfficherAbsences_Enseignant = etudiantAfficherAbsencesEnseignantArrayList;
        this.recy = recy;
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (recy.equals("afficher opptions")) {
            view = mInflater.inflate(R.layout.recyclerview_releve_absence, parent, false);
            return new ViewHolder(view);
        } else {
            view = mInflater.inflate(R.layout.recyclerview_show_absences, parent, false);
            return new ViewHolder(view);
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView num, nom, prenom, nombre_absence_non_justife, nombre_absence_justife, nom_etudiant, prenom_etudiant,
                etat, date, historique;
        ImageView display;
        ImageView option_justifier, option_supprimer,option_show_justifications ;

        ViewHolder(View itemView) {
            super(itemView);

            num = itemView.findViewById(R.id.num);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            nombre_absence_non_justife = itemView.findViewById(R.id.nombre_absences_non_justifié);
            nombre_absence_justife = itemView.findViewById(R.id.nombre_absences_justifié);
            display = itemView.findViewById(R.id.display);
            nom_etudiant = itemView.findViewById(R.id.nom_etudiant);
            prenom_etudiant = itemView.findViewById(R.id.prenom_etudiant);
            etat = itemView.findViewById(R.id.etat);
            date = itemView.findViewById(R.id.date);

            if (recy.equals("afficher opptions")) {
                display.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.Display(v, getAdapterPosition(), liste_etudiants_et_absences.get(getAdapterPosition()));
                    }
                });


            } else {

                option_justifier = itemView.findViewById(R.id.option_justifier);
                option_supprimer = itemView.findViewById(R.id.option_supprimer);
                option_show_justifications = itemView.findViewById(R.id.show_justification);


                option_justifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.gererJustification(
                                getEtudiant(getAdapterPosition()),
                                getAbsence(getAdapterPosition()),
                                "FragmentUploadJustification"
                        );
                    }
                });

                option_supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.supprimerAbsence(getNumeroAbsence(getAdapterPosition()));
                    }
                });

                option_show_justifications.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.gererJustification(
                                getEtudiant(getAdapterPosition()),
                                getAbsence(getAdapterPosition()),
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

        if (recy.equals("afficher opptions")) {
            holder.num.setText(String.valueOf(position + 1));
            holder.nom.setText(liste_etudiants_et_absences.get(position).getEtudiant().getNom());
            holder.prenom.setText(liste_etudiants_et_absences.get(position).getEtudiant().getPrenom());
            holder.nombre_absence_non_justife.setText(String.valueOf(liste_etudiants_et_absences.get(position).getAbsencesNonJustifier()));
            holder.nombre_absence_justife.setText(String.valueOf(liste_etudiants_et_absences.get(position).getAbsencesJusifiter()));

        } else {

            holder.nom_etudiant.setText(EtudiantAfficherAbsences_Enseignant.get(position).getNom());
            holder.prenom_etudiant.setText(EtudiantAfficherAbsences_Enseignant.get(position).getPrenom());
            holder.date.setText(EtudiantAfficherAbsences_Enseignant.get(position).getAbsence().getDate_absence());
            holder.etat.setText(EtudiantAfficherAbsences_Enseignant.get(position).getEtat());
            if (EtudiantAfficherAbsences_Enseignant.get(position).isHistorique()) {
                holder.option_justifier.setVisibility(View.GONE);
                holder.option_show_justifications.setVisibility(View.VISIBLE);
            }else{
                holder.option_show_justifications.setVisibility(View.GONE);
                holder.option_justifier.setVisibility(View.VISIBLE);
            }
            switch (EtudiantAfficherAbsences_Enseignant.get(position).getDefault_etat()) {
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

        if (recy.equals("afficher opptions")) {
            if (liste_etudiants_et_absences != null) {
                return liste_etudiants_et_absences.size();
            } else {
                return 0;
            }
        } else {
            if (EtudiantAfficherAbsences_Enseignant != null) {
                return EtudiantAfficherAbsences_Enseignant.size();
            } else {
                return 0;
            }
        }
    }


    private int getNumeroAbsence(int id) {
        if (recy.equals("afficher opptions")) {

            return liste_etudiants_et_absences.get(id).getAbsences().get(id).getAbsence().getNumero_absence();
        } else {
            return EtudiantAfficherAbsences_Enseignant.get(id).getAbsence().getNumero_absence();

        }
    }


    public Etudiant getEtudiant(int position) {
        if (recy.equals("afficher opptions")) {
            return liste_etudiants_et_absences.get(position).getEtudiant();
        } else {
            return new Etudiant(
                    EtudiantAfficherAbsences_Enseignant.get(position).getNom(),
                    EtudiantAfficherAbsences_Enseignant.get(position).getPrenom());
        }
    }

    public Absence getAbsence(int position) {
        return EtudiantAfficherAbsences_Enseignant.get(position).getAbsence();
    }

    // allows clicks events to be caught
    public void setClickListener(FragmentReleveAbsences itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void Display(View view, int position, EtudiantResponse etudiantResponse);

        void supprimerAbsence(int numero);

        void gererJustification(Etudiant etudiant, Absence absence, String redirection);

    }

}
