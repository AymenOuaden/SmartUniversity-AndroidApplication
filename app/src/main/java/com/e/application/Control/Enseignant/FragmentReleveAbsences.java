package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.API.API;
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentReleveAbsence;
import com.e.application.Helpers.AbsenceResponse;
import com.e.application.Helpers.EtudiantAfficherAbsences_Enseignant;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Justification;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentReleveAbsences extends Fragment implements Adapter_FragmentReleveAbsence.ItemClickListener {
    // les attributs
    View view;
    private Adapter_FragmentReleveAbsence adapter;
    Enseignant enseignant;
    String token;
    private Seance seance;
    LoginPreferencesConfig loginPreferenceConfig;
    private ArrayList<EtudiantResponse> liste_etudiants_absences;
    private String message = "", etat = "", jour_affiche = "";
    Gson gson = new Gson();
    private TextView tableau_vide;
    private RelativeLayout wait;
    private RecyclerView recyclerView;
    private Button afficher_default;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_releve_absences, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.toolbar_releve_absence);

        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();

        String json_seance = loginPreferenceConfig.getSeance();
        seance = gson.fromJson(json_seance, Seance.class);
        final String json_liste_etudiants_absences = loginPreferenceConfig.getListeEtudiantsEtLeursAbsences();
        liste_etudiants_absences = gson.fromJson(
                json_liste_etudiants_absences,
                new TypeToken<ArrayList<EtudiantResponse>>() {
                }.getType()
        );
        final ArrayList<Etudiant> etudiants = new ArrayList<>();
        if (!json_liste_etudiants_absences.equals("null")) {
            for (EtudiantResponse e : liste_etudiants_absences) {
                etudiants.add(e.getEtudiant());
            }
        }

        // affichage au top
        TextView message_top = view.findViewById(R.id.message_top);
        wait = view.findViewById(R.id.wait);
        Bundle bundle = getArguments();
        if (bundle != null) {
            message = (String) bundle.getSerializable("message");
            etat = (String) bundle.getSerializable("etat");
            if (!message.equals("")) {
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                if (etat.equals("ok")) {
                    message_top.setBackgroundColor(getResources().getColor(R.color.light_back));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
                } else {
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }
        }

        tableau_vide = view.findViewById(R.id.tableau_vide);
        recyclerView = view.findViewById(R.id.recycler);


        // affichage default
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
        date.setText(jour_affiche + "  " + seance.getHeure());
        // vérification si la séance contient des étudiants
        if (etudiants.size() >= 1) {
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new Adapter_FragmentReleveAbsence(this, liste_etudiants_absences, "afficher opptions");
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            TextView vide = view.findViewById(R.id.liste_etudiant_vide);
            vide.setVisibility(View.VISIBLE);
        }
        // implementation des boutons
        final Button exclus = view.findViewById(R.id.exclus);
        exclus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "NewApi"})
            @Override
            public void onClick(View v) {
                exclus.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                exclus.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentExcludedStudents");
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
            @SuppressLint({"ResourceAsColor", "NewApi"})
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                afficher_seances.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("AfficherToutesSeances");
            }
        });
        // affichage par default
        afficher_default = view.findViewById(R.id.afficher_default);
        afficher_default.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {
                wait.setVisibility(View.VISIBLE);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        goToOptionDeGestion("FragmentReleveAbsence");
                    }
                }, 100);

            }
        });
        /* le bouton appel la méthode qui affiche toutes les absences de tous le étudiants
           de la séance concernée
         */
        final Button afficher_tous = view.findViewById(R.id.afficher_tous);
        afficher_tous.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {

                wait.setVisibility(View.VISIBLE);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        tableau_vide.setVisibility(View.GONE);
                        afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                        afficher_default.setTextColor(getResources().getColor(R.color.black));
                        afficher_tous.setBackground(getResources().getDrawable(R.drawable.button_full_black_round));
                        afficher_tous.setTextColor(getResources().getColor(R.color.white));
                        TableLayout option = view.findViewById(R.id.layout_options);
                        option.setVisibility(View.GONE);
                        TableLayout table_afficher = view.findViewById(R.id.table_afficher_absence);
                        table_afficher.setVisibility(View.VISIBLE);

                        String json_liste_etudiants_absences = loginPreferenceConfig.getListeEtudiantsEtLeursAbsences();
                        liste_etudiants_absences = gson.fromJson(
                                json_liste_etudiants_absences,
                                new TypeToken<ArrayList<EtudiantResponse>>() {
                                }.getType()
                        );
                        final ArrayList<Etudiant> etudiants = new ArrayList<>();
                        if (!json_liste_etudiants_absences.equals("null")) {
                            for (EtudiantResponse e : liste_etudiants_absences) {
                                etudiants.add(e.getEtudiant());
                            }
                        }
                        //  afficher_absences_groupe(liste_etudiants_absences);
                        if (etudiants.size() >= 1) {
                            afficherToutesAbsences(liste_etudiants_absences);
                        } else {
                            TextView vide = view.findViewById(R.id.liste_etudiant_vide);
                            vide.setVisibility(View.VISIBLE);
                            wait.setVisibility(View.GONE);
                        }
                    }
                }, 100);
            }
        });

        return view;
    }

    public FragmentReleveAbsences() {
    }

    // afficher absences pour tous le groupe
    private void afficherToutesAbsences(ArrayList<EtudiantResponse> liste_etudiants_absences) {
        ArrayList<EtudiantAfficherAbsences_Enseignant> etudiant = new ArrayList<>();
        if (liste_etudiants_absences.size() >= 1) {
            for (EtudiantResponse etudiantResponse : liste_etudiants_absences) {
                if (etudiantResponse.getAbsences() != null) {
                    for (AbsenceResponse absence : etudiantResponse.getAbsences()) {
                        boolean historique = false;
                        String etat = getResources().getString(R.string.non_justifie);
                        if (absence.getJustification() != null) {
                            int j = 0;
                            for (Justification justification : absence.getJustification()) {
                                // pour toutes justification ; s'il ya une qui a une état valide , donc l'icon_manage_absences est justifie, valide
                                j++;
                                if (j >= 1) {
                                    historique = true;
                                }
                                etat = justification.getEtat_justification().toString();
                            }
                        }

                        String etat_afficher = etat;
                        String etat_default = etat;
                        switch (etat) {
                            case "valide":
                                etat_afficher = getResources().getString(R.string.valide);
                                break;
                            case "refuse":
                                etat_afficher = getResources().getString(R.string.refuse);
                                break;
                            case "nonTraite":
                                etat_afficher = getResources().getString(R.string.non_traite);
                                break;
                        }
                        EtudiantAfficherAbsences_Enseignant e = new EtudiantAfficherAbsences_Enseignant(etudiantResponse.getEtudiant().getId_utilisateur(),
                                etudiantResponse.getEtudiant().getNom(), etudiantResponse.getEtudiant().getPrenom(),
                                etat_afficher, etat_default, absence.getAbsence(), historique);
                        etudiant.add(e);
                    }
                }
            }
        }
        RecyclerView recyclerView2 = view.findViewById(R.id.recycler);
        if (etudiant.size() >= 1) {
            recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentReleveAbsence adapter2 = new Adapter_FragmentReleveAbsence(this, etudiant, "afficher_tous", 2);
            adapter2.setClickListener(this);
            recyclerView2.setAdapter(adapter2);
            recyclerView2.setItemAnimator(new DefaultItemAnimator());
            wait.setVisibility(View.GONE);
        } else {
            afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
            tableau_vide.setVisibility(View.VISIBLE);
            tableau_vide.setText(getResources().getString(R.string.groupe_aucune_asbence));
            recyclerView.setVisibility(View.GONE);
            wait.setVisibility(View.GONE);
        }
    }


    // afficher absences pour un seul etudiant
    @SuppressLint("SetTextI18n")
    @Override
    public void Display(View view, int position, EtudiantResponse etudiantResponse1) {
        ArrayList<EtudiantAfficherAbsences_Enseignant> etudiant = new ArrayList<>();
        EtudiantResponse etudiantResponse = liste_etudiants_absences.get(position);
        if (etudiantResponse.getAbsences() != null) {
            for (AbsenceResponse absence : etudiantResponse.getAbsences()) {
                boolean historique = false;
                String etat = getResources().getString(R.string.non_justifie);

                if (absence.getJustification() != null) {
                    int j = 0;
                    for (Justification justification : absence.getJustification()) {
                        // pour toutes justification ; s'il ya une qui a une état valide , donc l'icon_manage_absences est justifie, valide
                        j++;
                        if (j >= 1) {
                            historique = true;
                        }
                        etat = justification.getEtat_justification().toString();
                    }
                }

                String etat_afficher = etat;
                String etat_default = etat;
                switch (etat) {
                    case "valide":
                        etat_afficher = getResources().getString(R.string.valide);
                        break;
                    case "refuse":
                        etat_afficher = getResources().getString(R.string.refuse);
                        break;
                    case "nonTraite":
                        etat_afficher = getResources().getString(R.string.non_traite);
                        break;
                }
                EtudiantAfficherAbsences_Enseignant e = new EtudiantAfficherAbsences_Enseignant(etudiantResponse.getEtudiant().getId_utilisateur(),
                        etudiantResponse.getEtudiant().getNom(), etudiantResponse.getEtudiant().getPrenom(),
                        etat_afficher, etat_default, absence.getAbsence(), historique);
                etudiant.add(e);
            }

        }

        if (etudiant.size() >= 1) {
            setViewDisplay(etudiant);
        } else {
            afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
            afficher_default.setTextColor(getResources().getColor(R.color.black));

            tableau_vide.setVisibility(View.VISIBLE);
            tableau_vide.setText(etudiantResponse1.getEtudiant().getNom() +
                    " " + etudiantResponse1.getEtudiant().getPrenom() + " " +
                    getResources().getString(R.string.etudiant_aucune_asbence));
            recyclerView.setVisibility(View.GONE);

        }
    }

    //la méthode affiche toutes les absences d'un seul étudiant
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setViewDisplay(ArrayList<EtudiantAfficherAbsences_Enseignant> etudiantAfficherAbsencesEnseignantArrayList) {
        // changer le style du bouton
        afficher_default.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
        afficher_default.setTextColor(getResources().getColor(R.color.black));
        // modification sur layout actuelle
        TableLayout option = view.findViewById(R.id.layout_options);
        option.setVisibility(View.GONE);
        TableLayout table_afficher = view.findViewById(R.id.table_afficher_absence);
        table_afficher.setVisibility(View.VISIBLE);

        // affichage via adapter
        RecyclerView recyclerView1 = view.findViewById(R.id.recycler);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Adapter_FragmentReleveAbsence(this, etudiantAfficherAbsencesEnseignantArrayList, "afficher absences", 2);
        adapter.setClickListener(this);
        recyclerView1.setAdapter(adapter);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

    }


    // redirection vers fragment upload justification ou show justification selon l'argument redirection
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void gererJustification(Etudiant etudiant, Absence absence, String redirection) {
        Fragment fragment = null;
        if (redirection.equals("FragmentUploadJustification")) {
            fragment = new FragmentUploadJustification();
        } else if (redirection.equals("FragmentDisplayJustification")) {

            fragment = new FragmentDisplayJustification();
        }
        loginPreferenceConfig.setEtudiant(etudiant);
        loginPreferenceConfig.setAbsence(absence);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        assert fragment != null;
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    // la méthode supprime une icon_manage_absences
    @Override
    public void supprimerAbsence(int numero_absence) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.DeleteAbsenceByNumero(numero_absence, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    message = getResources().getString(R.string.supprimer_absence_succ);
                    etat = "ok";
                    GetSeancesCompleteEnseignant(seance);

                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    GetSeancesCompleteEnseignant(seance);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });
    }

    // récupération des nouveaux données aprés la suppression d'une sénace
    private void GetSeancesCompleteEnseignant(final Seance seance) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<SeanceResponse>> call = api1.GetSeancesCompleteEnseignant(enseignant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<SeanceResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Response<ArrayList<SeanceResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<SeanceResponse> seanceResponses;
                    seanceResponses = response.body();
                    assert seanceResponses != null;
                    for (SeanceResponse r : seanceResponses) {
                        if (r.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                            liste_etudiants_absences = r.getEtudiants();
                        }
                    }
                    loginPreferenceConfig.setListeEtudiantsEtLeursAbsences(liste_etudiants_absences);
                    goToOptionDeGestion("FragmentReleveAbsences");
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    goToOptionDeGestion("FragmentReleveAbsences");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });
    }

    // la méthode redirect l'application vers une autre fragment selon l'attribut " redirection"
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(String redirection) {
        wait.setVisibility(View.GONE);
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (redirection) {
            case "FragmentExcludedStudents":
                fragment = new FragmentExcludedStudents();
                break;
            case "FragmentReleveAbsences":
                bundle.putSerializable("message", message);
                bundle.putSerializable("etat", etat);
                fragment = new FragmentReleveAbsences();
                fragment.setArguments(bundle);
                break;
            case "AfficherToutesSeances":
                fragment = new FragmentManageAbsences();
                break;
            default:
                fragment = new FragmentReleveAbsences();
                break;
        }

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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