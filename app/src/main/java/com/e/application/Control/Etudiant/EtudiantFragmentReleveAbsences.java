package com.e.application.Control.Etudiant;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Adapters.AdapterEtudiant.Etudiant_Adapter_FragmentReleveAbsence;
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Helpers.EtudiantAfficherAbsences_Etudiant;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Justification;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class EtudiantFragmentReleveAbsences extends Fragment implements Etudiant_Adapter_FragmentReleveAbsence.ItemClickListener {
    // les attributs
    private View view;
    private Etudiant_Adapter_FragmentReleveAbsence adapter;
    private ArrayList<AbsenceDepartementResponse> absenceDepartementResponse = new ArrayList<>();
    private LoginPreferencesConfig loginPreferenceConfig;
    private Gson gson = new Gson();
    private RelativeLayout wait;
    private Button afficher_default, afficher_tous;
    private ArrayList<Seance> seances;
    private TextView aucune_absence;
    private String json_absences_informations1 = "", affichage = "";
    private TableLayout layout_options, table_afficher_absence;

    public EtudiantFragmentReleveAbsences() {
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.etudiant_fragment_releve_absences, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.toolbar_releve_absence);

        // affichage au top
        final TextView message_top = view.findViewById(R.id.message_top);
        wait = view.findViewById(R.id.wait);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String message = (String) bundle.getSerializable("message");
            String etat = (String) bundle.getSerializable("etat");
            if (message != null && !message.equals("")) {
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                assert etat != null;
                if (etat.equals("ok")) {
                    message_top.setTextColor(getResources().getColor(R.color.black));
                    message_top.setBackgroundColor(getResources().getColor(R.color.light_back));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
                } else {
                    message_top.setTextColor(getResources().getColor(R.color.red));
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }
        }
        aucune_absence = view.findViewById(R.id.aucune_absence);
        table_afficher_absence = view.findViewById(R.id.table_afficher_absence);
        layout_options = view.findViewById(R.id.layout_options);

        // récupération d'etudiant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String json_etudiant = loginPreferenceConfig.getEtudiant();
        Etudiant etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        // affichage default
        TextView name = view.findViewById(R.id.name);
        name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        TextView annee_spclt = view.findViewById(R.id.annee_spclt);
        annee_spclt.setText(etudiant.getAnnee() + " " + etudiant.getSpecialite());
        TextView section_groupe = view.findViewById(R.id.section_groupe);
        section_groupe.setText(getResources().getString(R.string.section) + " " + etudiant.getSection() + " " +
                getResources().getString(R.string.groupe) + " " + etudiant.getGroupe() + "  ");

        // affichage par default , la liste des séances avec le nombre d'absences dans chaque séance
        String json_seance_responses = loginPreferenceConfig.getSeanceResponseEtudiant();
        ArrayList<SeanceResponse> seanceResponses = gson.fromJson(
                json_seance_responses,
                new TypeToken<ArrayList<SeanceResponse>>() {
                }.getType()
        );

        // get la liste des seances from seance response
        seances = new ArrayList<>();
        for (SeanceResponse seanceResponse : seanceResponses) {
            seances.add(seanceResponse.getSeance());
        }
        json_absences_informations1 = loginPreferenceConfig.getAbsenceDepartementResponses();
        absenceDepartementResponse = gson.fromJson(
                json_absences_informations1,
                new TypeToken<ArrayList<AbsenceDepartementResponse>>() {
                }.getType()
        );

        if (bundle != null) {
            if (bundle.getSerializable("affichage") != null) {
                affichage = (String) bundle.getSerializable("affichage");
            }
        }

        assert affichage != null;
        if (affichage.equals("AfficherToutesAbsences")) {
            afficherTousAbsences();
        } else {
            String json_absences_informations = loginPreferenceConfig.getAbsenceDepartementResponses();
            if (json_absences_informations != null && !json_absences_informations.equals("null")) {
                absenceDepartementResponse = gson.fromJson(
                        json_absences_informations,
                        new TypeToken<ArrayList<AbsenceDepartementResponse>>() {
                        }.getType()
                );
                // EtudiantAfficherAbsences_Etudiant aide a l'affichage
                ArrayList<EtudiantAfficherAbsences_Etudiant> etudiantAfficherAbsenceEtudiants = new ArrayList<>();
                for (Seance seance : seances) {
                    int nbr_absences = 0;
                    for (AbsenceDepartementResponse absenceResponse : absenceDepartementResponse) {
                        if (absenceResponse.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                            nbr_absences++;
                        }
                    }
                    EtudiantAfficherAbsences_Etudiant e = new EtudiantAfficherAbsences_Etudiant(seance.getCode_module(),
                            seance.getType().toString(), seance.getJour().toString(), seance.getHeure(), nbr_absences);
                    etudiantAfficherAbsenceEtudiants.add(e);

                }
                adapter = new Etudiant_Adapter_FragmentReleveAbsence(this, etudiantAfficherAbsenceEtudiants, "afficher_default");
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            } else {
                aucune_absence.setVisibility(View.VISIBLE);
            }
        }
        // implementation des boutons

        // affichage par default
        afficher_default = view.findViewById(R.id.afficher_default);
        afficher_default.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {
                AfficherDefault();
            }
        });
        /* le bouton appel la méthode qui affiche toutes les absences de tous le étudiants
           de la séance concernée*/
        afficher_tous = view.findViewById(R.id.afficher_tous);
        afficher_tous.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {
                refreshFragment();
            }
        });

        return view;
    }

    @Override
    public void Display(View view, int position) {
        afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
        afficher_default.setTextColor(getResources().getColor(R.color.black));
        Seance seance = seances.get(position);
        String code_seance = seance.getCode_seance();
        ArrayList<EtudiantAfficherAbsences_Etudiant> absencesInformations = new ArrayList<>();
        for (AbsenceDepartementResponse absenceResponse : absenceDepartementResponse) {
            if (absenceResponse.getSeance().getCode_seance().equals(code_seance)) {
                boolean historique = false;
                String etat_non_justifie = getResources().getString(R.string.non_justifie);
                String etat_valide = "";
                String etat_refuse = "";
                String etat_non_traite = "";

                if (absenceResponse.getJustifications() != null) {
                    int j = 0;
                    for (Justification justification : absenceResponse.getJustifications()) {
                        // pour toutes justification ; s'il ya une qui a une état valide , donc l'icon_manage_absences est justifie, valide
                        j++;
                        if (j >= 1) {
                            historique = true;
                        }
                        if (justification.getEtat_justification().toString().equals("valide")) {
                            etat_valide = getResources().getString(R.string.valide);
                        } else if (justification.getEtat_justification().toString().equals("nonTraite")) {
                            etat_non_traite = getResources().getString(R.string.non_traite);
                        } else if (justification.getEtat_justification().toString().equals("refuse")) {
                            etat_refuse = getResources().getString(R.string.refuse);
                        }
                    }
                }
                String etat_afficher;
                String etat_default = "";
                if (!etat_valide.equals("")) {
                    etat_afficher = etat_valide;
                    etat_default = "valide";
                } else if (!etat_non_traite.equals("")) {
                    etat_afficher = etat_non_traite;
                    etat_default = "nonTraite";
                } else if (!etat_refuse.equals("")) {
                    etat_afficher = etat_refuse;
                    etat_default = "refuse";
                } else {
                    etat_afficher = etat_non_justifie;
                }
                EtudiantAfficherAbsences_Etudiant e = new EtudiantAfficherAbsences_Etudiant(
                        absenceResponse.getAbsence().getNumero_absence(),
                        seance.getCode_module(),
                        seance.getType().toString(),
                        absenceResponse.getAbsence().getDate_absence(),
                        etat_afficher, etat_default, historique);
                absencesInformations.add(e);
            }
        }
        setViewDisplay(absencesInformations);
    }

    private void setViewDisplay(ArrayList<EtudiantAfficherAbsences_Etudiant> absencesInformations) {
        // changer le style du bouton
        final Button afficher_default = view.findViewById(R.id.afficher_default);
        afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
        afficher_default.setTextColor(getResources().getColor(R.color.black));
        // modification sur layout actuelle
        TableLayout option = view.findViewById(R.id.layout_options);
        option.setVisibility(View.GONE);
        TableLayout table_afficher = view.findViewById(R.id.table_afficher_absence);
        table_afficher.setVisibility(View.VISIBLE);
        RecyclerView recyclerView1 = view.findViewById(R.id.recycler);

        if (absencesInformations.size() < 1) {
            aucune_absence.setVisibility(View.VISIBLE);
            recyclerView1.setVisibility(View.GONE);
        } else {
            // affichage via adapter
            recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new Etudiant_Adapter_FragmentReleveAbsence(this, absencesInformations, "afficher_options");
            adapter.setClickListener(this);
            recyclerView1.setAdapter(adapter);
            recyclerView1.setItemAnimator(new DefaultItemAnimator());
        }
    }

    // la méthode redirect l'application vers une autre fragment selon l'attribut " redirection"
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void AfficherDefault() {
        wait.setVisibility(View.GONE);
        Fragment fragment;
        fragment = new EtudiantFragmentReleveAbsences();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void afficherTousAbsences() {
        afficher_default = view.findViewById(R.id.afficher_default);
        afficher_tous = view.findViewById(R.id.afficher_tous);
        afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
        afficher_default.setTextColor(getResources().getColor(R.color.black));
        afficher_tous.setBackground(getResources().getDrawable(R.drawable.button_full_black_round));
        afficher_tous.setTextColor(getResources().getColor(R.color.white));
        layout_options.setVisibility(View.GONE);
        table_afficher_absence.setVisibility(View.VISIBLE);
        ArrayList<EtudiantAfficherAbsences_Etudiant> absencesInformations = new ArrayList<>();

        for (Seance seance : seances) {
            String code_seance = seance.getCode_seance();
            if (json_absences_informations1.length() > 4) {
                for (AbsenceDepartementResponse absenceResponse : absenceDepartementResponse) {
                    if (absenceResponse.getSeance() != null) {
                        if (absenceResponse.getSeance().getCode_seance().equals(code_seance)) {
                            boolean historique = false;
                            String etat_non_justifie = getResources().getString(R.string.non_justifie);
                            String etat_valide = "";
                            String etat_refuse = "";
                            String etat_non_traite = "";

                            if (absenceResponse.getJustifications() != null) {
                                int j = 0;
                                for (Justification justification : absenceResponse.getJustifications()) {
                                    // pour toutes justification ; s'il ya une qui a une état valide , donc l'icon_manage_absences est justifie, valide
                                    j++;
                                    if (j >= 1) {
                                        historique = true;
                                    }
                                    if (justification.getEtat_justification().toString().equals("valide")) {
                                        etat_valide = getResources().getString(R.string.valide);
                                    } else if (justification.getEtat_justification().toString().equals("nonTraite")) {
                                        etat_non_traite = getResources().getString(R.string.non_traite);
                                    } else if (justification.getEtat_justification().toString().equals("refuse")) {
                                        etat_refuse = getResources().getString(R.string.refuse);
                                    }
                                }
                            }

                            String etat_afficher;
                            String etat_default = "";
                            if (!etat_valide.equals("")) {
                                etat_afficher = etat_valide;
                                etat_default = "valide";
                            } else if (!etat_non_traite.equals("")) {
                                etat_afficher = etat_non_traite;
                                etat_default = "nonTraite";
                            } else if (!etat_refuse.equals("")) {
                                etat_afficher = etat_refuse;
                                etat_default = "refuse";
                            } else {
                                etat_afficher = etat_non_justifie;
                            }
                            EtudiantAfficherAbsences_Etudiant e = new EtudiantAfficherAbsences_Etudiant(
                                    absenceResponse.getAbsence().getNumero_absence(), seance.getCode_module(),
                                    seance.getType().toString(), absenceResponse.getAbsence().getDate_absence(),
                                    etat_afficher, etat_default, historique);
                            absencesInformations.add(e);
                        }
                    }
                }
            }
        }
        RecyclerView recyclerView1 = view.findViewById(R.id.recycler);
        if (absencesInformations.size() < 1) {
            aucune_absence.setVisibility(View.VISIBLE);
            recyclerView1.setVisibility(View.GONE);
        } else {
            // affichage via adapter
            recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
            Etudiant_Adapter_FragmentReleveAbsence adapter1 = new Etudiant_Adapter_FragmentReleveAbsence(this, absencesInformations, "afficher_options");
            adapter1.setClickListener(this);
            recyclerView1.setAdapter(adapter1);
            recyclerView1.setItemAnimator(new DefaultItemAnimator());
        }
    }


    // redirection vers fragment upload justification ou show justification selon l'argument redirection
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void gererJustification(int numero_absence, String redirection) {
        Absence absence = null;
        for (AbsenceDepartementResponse abs : absenceDepartementResponse) {
            if (abs.getAbsence().getNumero_absence() == numero_absence) {
                absence = abs.getAbsence();
            }
        }
        Fragment fragment = null;
        if (redirection.equals("FragmentUploadJustification")) {
            fragment = new EtudiantFragmentUploadJustification();
        } else if (redirection.equals("FragmentDisplayJustification")) {
            fragment = new EtudiantFragmentDisplayJustification();
        }
        loginPreferenceConfig.setAbsence(absence);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        assert fragment != null;
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void refreshFragment() {

        EtudiantFragmentReleveAbsences fragment = new EtudiantFragmentReleveAbsences();
        Bundle bundle = new Bundle();
        bundle.putSerializable("affichage", "AfficherToutesAbsences");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
