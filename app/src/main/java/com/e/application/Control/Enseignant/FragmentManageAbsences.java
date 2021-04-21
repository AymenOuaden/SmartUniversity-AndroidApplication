package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentManageAbsences;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentManageAbsences extends Fragment implements Adapter_FragmentManageAbsences.ItemClickListener {
    // les attributs
    View view;
    Enseignant enseignant;
    String token;
    private ArrayList<Seance> seances, seance_concernee;
    private Adapter_FragmentManageAbsences adapter;
    private ArrayList<SeanceResponse> seanceResponses;
    private RelativeLayout wait;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    TextView message;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_manage_absences, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.param);

        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        //seances
        String json_seances = loginPreferenceConfig.getSeances();
        seances = gson.fromJson(json_seances, new TypeToken<ArrayList<Seance>>() {
        }.getType());

        // afficher les séances via un adapter dans une recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Adapter_FragmentManageAbsences(getContext(), seances);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // relative wait est affichée loesque on attend la reponse de l'api
        wait = view.findViewById(R.id.wait);
        message = view.findViewById(R.id.message);

        // implémentation des boutons
        final Button statistiques = view.findViewById(R.id.statistiques);
        final Button releve_absences = view.findViewById(R.id.releve);
        final Button etudiants_exclus = view.findViewById(R.id.exclus);
        final Button afficher_seances = view.findViewById(R.id.afficher_seances);

        etudiants_exclus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                etudiants_exclus.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                etudiants_exclus.setTextColor(getResources().getColor(R.color.white));
                GetSeancesCompleteEnseignant(seance_concernee.get(0), "FragmentEtudiantsEsclus");
            }
        });
        releve_absences.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                releve_absences.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve_absences.setTextColor(getResources().getColor(R.color.white));
                GetSeancesCompleteEnseignant(seance_concernee.get(0), "FragmentRelveAbsences");
            }
        });
        statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistiques.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                statistiques.setTextColor(getResources().getColor(R.color.white));
                goTo();
            }
        });
        afficher_seances.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "NewApi"})
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                afficher_seances.setTextColor(getResources().getColor(R.color.white));
                afficher_seances();
            }
        });

        return view;
    }


    public FragmentManageAbsences() {
    }

    /* la méthode filtre la liste des séances, elle sélectionne juste la séance qui a été choisi
       par l'enseignant depuis la liste de toutes les séances. */
    @Override
    public void gererReleveAbsences(View view, int position) {
        /* l'argument "position" définit la position de la séance qui a été choisi
           dans la liste des séances */
        seance_concernee = new ArrayList<>();
        seance_concernee.add(seances.get(position));
        setView(seance_concernee);
    }

    // la méthode qui affiche la séance
    private void setView(ArrayList<Seance> seances) {
        TextView title = view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.gerer_seance));
        RelativeLayout relativeLayout = view.findViewById(R.id.options_glob);
        relativeLayout.setVisibility(View.VISIBLE);
        // le nouveau affichage via un adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Adapter_FragmentManageAbsences(getContext(), seances);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    /*  cette méthode récupère la séance de l'enseignant et leurs étudiants, le module,les séances
    supplémentaires s'ils sont existés et la demande de changement s'elle est existé aussi */
    private void GetSeancesCompleteEnseignant(final Seance seance, final String redirection) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<SeanceResponse>> call = api1.GetSeancesCompleteEnseignant(
                enseignant.getId_utilisateur(),
                "Bearer " + token);
        call.enqueue(new Callback<ArrayList<SeanceResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Response<ArrayList<SeanceResponse>> response) {
                if (response.isSuccessful()) {
                    seanceResponses = new ArrayList<>();
                    seanceResponses = response.body();
                    assert seanceResponses != null;
                    getSeanceCompleteconcernee(seanceResponses, seance, redirection);
                } else {
                    wait.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.erreur_not_succ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.network));
            }
        });
    }

    /* la méthode filtre la liste qui contient toutes les informations(ils sont citées dans
    le commentaire qui décrit la méthode "getSeancesCompleteEnseignant"), elle sélectionne juste
    les informations qui concernent la séance qui a été choisi par l'enseignant depuis la liste de
    toutes les séances */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getSeanceCompleteconcernee(ArrayList<SeanceResponse> seanceResponses, Seance seance, String redirection) {
        SeanceResponse seanceResponse = new SeanceResponse();
        for (SeanceResponse response : seanceResponses) {
            if (response.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                seanceResponse = response;
            }
        }
        // appel à la méthode de redirection
        goToOptionDeGestion(seanceResponse, seance, redirection);
    }


    // la méthode redirect l'application vers une autre fragment selon l'attribut " redirection"
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(SeanceResponse seanceResponse, Seance seance, String redirection) {

        Fragment fragment;
        switch (redirection) {
            case "FragmentEtudiantsEsclus":
                fragment = new FragmentExcludedStudents();
                break;
            case "statistiques":
                fragment = new FragmentStatistics();
                break;
            case "FragmentRelveAbsences":
                fragment = new FragmentReleveAbsences();
                break;
            case "AfficherToutesSeance":
            default:
                fragment = new FragmentManageAbsences();
                break;
        }
        loginPreferenceConfig.setListeEtudiantsEtLeursAbsences(seanceResponse.getEtudiants());
        loginPreferenceConfig.setSeance(seance);

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        wait.setVisibility(View.GONE);
    }

    // la méthode actualise la page actuelle (affiche toutes les séances)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void afficher_seances() {
        wait.setVisibility(View.GONE);
        FragmentManageAbsences fragmentManageAbsences = new FragmentManageAbsences();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentManageAbsences);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goTo() {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", "statistics");
        startActivity(intent);

    }

}


