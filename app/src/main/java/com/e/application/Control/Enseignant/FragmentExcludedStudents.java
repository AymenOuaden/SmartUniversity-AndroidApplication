package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentEtudiantsExclus;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentExcludedStudents extends Fragment implements Adapter_FragmentEtudiantsExclus.ItemClickListener {
    // les attributs
    View view;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private String jour_affiche = "";

    public FragmentExcludedStudents() {
    }

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_excluded_students, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.toolbar_exclus);

        ArrayList<EtudiantResponse> etudiants_exclus = new ArrayList<>();
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_liste_etudiants_absences = loginPreferenceConfig.getListeEtudiantsEtLeursAbsences();
        ArrayList<EtudiantResponse> liste_etudiants_absences = gson.fromJson(
                json_liste_etudiants_absences,
                new TypeToken<ArrayList<EtudiantResponse>>() {
                }.getType()
        );
        String json_seance = loginPreferenceConfig.getSeance();
        Seance seance = gson.fromJson(json_seance, Seance.class);

        // teste si la séance contient des étudiants
        if (liste_etudiants_absences != null) {
            // parcourir les étudiants
            for (EtudiantResponse etudiant : liste_etudiants_absences) {
                if (etudiant.getAbsencesNonJustifier() >= 3) {
                    // 3 absences non justifiés == exclu
                    etudiants_exclus.add(etudiant);
                } else if (etudiant.getAbsencesJusifiter() >= 5) {
                    // 5 absences justifiés == exclu
                    etudiants_exclus.add(etudiant);
                } else if (etudiant.getAbsencesJusifiter() + etudiant.getAbsencesNonJustifier() >= 5) {
                    // les absences justifier + non justifier suppérieur à cinq
                    etudiants_exclus.add(etudiant);
                }
            }
        } else {
            // la liste ne contient aucun icon_manage_absences
            TextView non_etudiant_exclu = view.findViewById(R.id.non_etudiant_exclu);
            non_etudiant_exclu.setVisibility(View.VISIBLE);
        }

        // affichage
        TextView code_module = view.findViewById(R.id.code_module);
        code_module.setText(seance.getCode_module());
        TextView groupe = view.findViewById(R.id.groupe);
        groupe.setText(getResources().getString(R.string.groupe) + " " + seance.getGroupe());
        TextView date = view.findViewById(R.id.date);
        // traduction du jour selon la langue
        String jour = seance.getJour().toString();
        switch (jour) {
            case "dimanche":
                jour_affiche = getResources().getString(R.string.dimanche);
                break;
            case "lundi":
                jour_affiche = getResources().getString(R.string.lundi);
                break;
            case "mardi":
                jour_affiche = getResources().getString(R.string.mardi);
                break;
            case "mercredi":
                jour_affiche = getResources().getString(R.string.mercredi);
                break;
            case "jeudi":
                jour_affiche = getResources().getString(R.string.jeudi);
                break;
        }
        date.setText(jour_affiche + "  " + seance.getHeure());        // affichage la liste des étudiants exclus
        if (etudiants_exclus.size() >= 1) {
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentEtudiantsExclus adapter = new Adapter_FragmentEtudiantsExclus(this, etudiants_exclus);
            adapter.setClickListener();
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            TextView non_etudiant_exclu = view.findViewById(R.id.non_etudiant_exclu);
            non_etudiant_exclu.setVisibility(View.VISIBLE);
        }

        // implémentation des boutons
        final Button releve_absences = view.findViewById(R.id.releve);
        releve_absences.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                releve_absences.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve_absences.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentRelveAbsences");
            }
        });
        final Button statistiques = view.findViewById(R.id.statistiques);
        statistiques.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {
                statistiques.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                statistiques.setTextColor(getResources().getColor(R.color.white));
                goTo();
            }
        });
        final Button afficher_seances = view.findViewById(R.id.afficher_seances);
        afficher_seances.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                afficher_seances.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("AfficherToutesSeances");
            }
        });

        return view;
    }

    // la méthode redirect l'application vers une autre fragment selon l'attribut " redirection"
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(String redirection) {
        //  etudiants_exclus=null;
        Fragment fragment = null;
        switch (redirection) {
            case "FragmentExcludedStudents":
                fragment = new FragmentExcludedStudents();
                break;
            case "FragmentRelveAbsences":
                fragment = new FragmentReleveAbsences();
                break;
            case "AfficherToutesSeances":
                fragment = new FragmentManageAbsences();
                break;
        }

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        assert fragment != null;
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goTo() {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", "statistics");
        startActivity(intent);

    }

}