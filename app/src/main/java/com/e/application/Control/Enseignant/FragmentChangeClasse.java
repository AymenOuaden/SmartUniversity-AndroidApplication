package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.API.API;
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentConsulterSeance;
import com.e.application.Dots.Dot_Create_ChangementSeance;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.ChangementSeance;
import com.e.application.Model.Enseignant;
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

public class FragmentChangeClasse extends Fragment implements Adapter_FragmentConsulterSeance.ItemClickListener {

    View view;
    String token, affichage;

    Enseignant enseignant;
    private ArrayList<Seance> seances;
    private ArrayList<SeanceResponse> seanceResponses;
    private Seance seance;
    private ImageView changer;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private RelativeLayout wait;
    private TextView message_top;
    private String message = "", etat = "", jour_affiche = "";

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        // la liste des séances est récupéré dans l'attribut seances
        String json_seances = loginPreferenceConfig.getSeances();
        seances = gson.fromJson(json_seances, new TypeToken<ArrayList<Seance>>() {
        }.getType());
        //récupération des informations depuis bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        affichage = (String) bundle.getSerializable("affichage");
        // selon la valeur de l'attribut "affichage", on récupère la page(layout) concernée
        assert affichage != null;
        if (affichage.equals("consulter_liste_seances")) {
            //affichage toutes les séances
            view = inflater.inflate(R.layout.fragment_consulter_seances, container, false);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.consulter_seance);
            TextView title = view.findViewById(R.id.title);
            title.setText(getResources().getString(R.string.title_consulter_changer));
            // on affiche les séances à l'aide d'un adapter qui gère les séances dans un recylerviwe
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentConsulterSeance adapter = new Adapter_FragmentConsulterSeance(getContext(), seances);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            // le choix d'une séance depuis la liste précédente ( dans le cas if )
            view = inflater.inflate(R.layout.fragment_change_classe, container, false);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.change_seance);
            wait = view.findViewById(R.id.wait);
            message_top = view.findViewById(R.id.message_top);
            // vérification s'il y a un message.(le message est défini aprèes les modifications
            if (bundle.getSerializable("message") != null) {
                // l'attribut message contient la valeur du message à afficher
                message = (String) bundle.getSerializable("message");
                // l'attribut etat peut etre"ok"( message du succés) ou "not ok"(message d'erreur)
                etat = (String) bundle.getSerializable("etat");
            }
            assert etat != null;
            if (etat.equals("ok")) {
                // affichage d'un message du succès
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.black));
                message_top.setBackgroundColor(getResources().getColor(R.color.light_back));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
            } else if (etat.equals("not_ok")) {
                // affichage d'un message d'erreur
                //  messageNotOk.setText(getResources().getString(R.string.modifier_not_ok)+"\n - "+message );
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.red));
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }

            // récupération et définition des boutons et textes... de la séance choisie
            final Button add_classe, change_classe, afficher_seances;
            add_classe = view.findViewById(R.id.add_classe);
            change_classe = view.findViewById(R.id.change_classe);
            afficher_seances = view.findViewById(R.id.afficher_seances);
            add_classe.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    add_classe.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    add_classe.setTextColor(getResources().getColor(R.color.white));
                    goTo();

                }
            });
            change_classe.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    change_classe.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    change_classe.setTextColor(getResources().getColor(R.color.white));
                    goToOptionDeGestion("This");

                }
            });
            afficher_seances.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    afficher_seances.setTextColor(getResources().getColor(R.color.white));
                    goToOptionDeGestion("AfficherTous");
                }
            });
            // set les informations dans la page d'affichage
            TextView old_day, old_hour;
            seance = (Seance) bundle.getSerializable("seance");
            TextView code_module = view.findViewById(R.id.code_module);
            code_module.setText(seance.getCode_module());
            TextView groupe = view.findViewById(R.id.groupe);
            groupe.setText(getResources().getString(R.string.groupe) + " " + seance.getGroupe());
            TextView type = view.findViewById(R.id.type);
            type.setText(String.valueOf(seance.getType()));
            old_day = view.findViewById(R.id.old_day);
            old_hour = view.findViewById(R.id.old_hour);
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
            old_day.setText(jour_affiche);
            old_hour.setText(String.valueOf(seance.getHeure()));
            // récupére le changement de la séance
            GetSeancesCompleteEnseignant(seance);
        }
        return view;
    }

    // la méthode qui fait la sélectionne d'une séance
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void gerer(View view, int position) {
        seance = seances.get(position);
        FragmentChangeClasse fragmentChangeClasse = new FragmentChangeClasse();
        Bundle bundle = new Bundle();
        bundle.putSerializable("token", token);
        bundle.putSerializable("enseignant", enseignant);
        bundle.putSerializable("seances", seances);
        bundle.putSerializable("seance", seance);
        bundle.putSerializable("affichage", "");
        fragmentChangeClasse.setArguments(bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentChangeClasse, "FragmentChangeClass");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // récupération des changement s'ils existent
    private void GetSeancesCompleteEnseignant(final Seance seance) {
        wait.setVisibility(View.VISIBLE);
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
                    seanceResponses = response.body();
                    assert seanceResponses != null;
                    for (SeanceResponse seanceResponse : seanceResponses) {
                        if (seanceResponse.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                            setView(seanceResponse.getChangementSeance());
                        }
                    }
                } else {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    message_top.setVisibility(View.VISIBLE);
                    message_top.setText(message);
                    message_top.setTextColor(getResources().getColor(R.color.red));
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.red));
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    /* affiche s'il y a une demande de changement,
    sinon donne la permission pour demander un changement*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setView(ChangementSeance changementSeance) {
        final TextView new_day, new_hour, etat;
        new_day = view.findViewById(R.id.waiting_day);
        new_hour = view.findViewById(R.id.waiting_hour);
        etat = view.findViewById(R.id.etat);
        // récupération de spinner depuis layout
           /* le "spinner" qui nous donne la possibilité de choisir un élément depuis plusieurs
            éléments, dans notre cas on choisit le nouveau jour et heure
            de la nouvelle demande du changement de séance */
        final Spinner dropdown_jour = view.findViewById(R.id.new_day);
        final Spinner dropdown_heure = view.findViewById(R.id.new_hour);
        //creation les listes des élémnets des spinners.
        String[] items_jour = new String[]{getResources().getString(R.string.dimanche), getResources().getString(R.string.lundi),
                getResources().getString(R.string.mardi), getResources().getString(R.string.mercredi),
                getResources().getString(R.string.jeudi)};
        String[] items_heure = new String[]{"8:30", "10:00", "11:30", "13:00", "14:30"};
            /*création des adaptateurs pour décrire comment les éléments sont affichés, les adaptateurs
             sont utilisés à plusieurs endroits dans Android. */
        final ArrayAdapter<String> adapter_jour = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items_jour);
        final ArrayAdapter<String> adapter_heure = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items_heure);
        dropdown_jour.setAdapter(adapter_jour);
        dropdown_heure.setAdapter(adapter_heure);
        // le "CardView waiting" présente le box qui contient les informations de changement enregistrées
        // le "CardView nouveau" présente le box qui contient les champs pour demander un changement
        CardView waiting, nouveau;
        waiting = view.findViewById(R.id.waiting);
        nouveau = view.findViewById(R.id.nouveau);
        if (changementSeance != null) {
            // s'il y a un changement , le box de faire une demande est disparu
            // nouveau.setVisibility(View.GONE);
            waiting.setVisibility(View.VISIBLE);
            //  new_day.setText(String.valueOf(changementSeance.getNouveau_jour()));
            String jour = changementSeance.getNouveau_jour().toString();
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
            new_day.setText(jour_affiche);
            new_hour.setText(String.valueOf(changementSeance.getheure()));
            String etat1 = changementSeance.getEtat_demande().toString();
            String etat_afficher = "";

            switch (etat1) {
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
            etat.setText(etat_afficher);

            // le bouton "supprimer" appel la méthode de suppression du changement enregistré
            ImageView supprimer = view.findViewById(R.id.supprimer);
            supprimer.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    supprimerChangement(seance.getCode_seance());
                }
            });
        } else {
            // s'il y a pas de changement, le box qui affiche les informations de chanegement est disparu
            //waiting.setVisibility(View.GONE);
            nouveau.setVisibility(View.VISIBLE);
            // le bouton "changer " appel la méthode qui crée un changement de séance
            changer = view.findViewById(R.id.changer);
            changer.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    // récupération de nouveau jour et heure
                    Spinner spinner_jour = view.findViewById(R.id.new_day);
                    String jour = spinner_jour.getSelectedItem().toString();
                    if (jour.equals(getResources().getString(R.string.dimanche))) {
                        jour = "dimanche";
                    } else if (jour.equals(getResources().getString(R.string.lundi))) {
                        jour = "lundi";

                    } else if (jour.equals(getResources().getString(R.string.mardi))) {
                        jour = "mardi";

                    } else if (jour.equals(getResources().getString(R.string.mercredi))) {
                        jour = "mercredi";

                    } else if (jour.equals(getResources().getString(R.string.jeudi))) {
                        jour = "jeudi";
                    }
                    Spinner spinner_hour = view.findViewById(R.id.new_hour);
                    String hour = spinner_hour.getSelectedItem().toString();
                    // création d'un objet de changement séance avec les nouvelles informations
                    Dot_Create_ChangementSeance dot_create_changementSeance = new Dot_Create_ChangementSeance();
                    dot_create_changementSeance.setCode_seance(seance.getCode_seance());
                    dot_create_changementSeance.setNouveau_jour(jour);
                    dot_create_changementSeance.setNouvelle_heure(hour);
                    // appel la méthode de changement
                    demanderChangement(dot_create_changementSeance);
                }
            });
        }
        wait.setVisibility(View.GONE);
    }

    // la méthode qui appel l API pour faire une demande de changement du séance
    private void demanderChangement(Dot_Create_ChangementSeance dot_create_changementSeance) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/create/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.CreateChangementSenace(dot_create_changementSeance, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.changer_seance_ok);
                    etat = "ok";
                    // refreshThisFragment();
                    goToOptionDeGestion("This");

                } else {
                    wait.setVisibility(View.GONE);
                    changer = view.findViewById(R.id.changer);
                    //changer.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                    //changer.setTextColor(getResources().getColor(R.color.black));
                    message = getResources().getString(R.string.erreur_jour_heure);
                    etat = "not_ok";
                    //  goToOptionDeGestion("This");
                    message_top.setVisibility(View.VISIBLE);
                    message_top.setText(message);
                    message_top.setTextColor(getResources().getColor(R.color.red));
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);

                }
            }

            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                changer = view.findViewById(R.id.changer);
                //changer.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                //changer.setTextColor(getResources().getColor(R.color.black));
                //  refreshThisFragment();
                //   goToOptionDeGestion("This");
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.red));
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);

            }
        });
    }

    // la méthode qui appel l'API pour supprimer une demande de changement
    private void supprimerChangement(String code_seance) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.DeleteChangementSeance(code_seance, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.supprimer_changement_ok);
                    etat = "ok";
                    //refreshThisFragment();
                    goToOptionDeGestion("This");
                } else {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    //goToOptionDeGestion("This");
                    message_top.setVisibility(View.VISIBLE);
                    message_top.setText(message);
                    message_top.setTextColor(getResources().getColor(R.color.red));
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);

                }
            }

            @SuppressLint("NewApi")
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                //goToOptionDeGestion("This");
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.red));
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(String redirection) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("seances", seances);
        bundle.putSerializable("seance", seance);
        bundle.putSerializable("message", message);
        bundle.putSerializable("etat", etat);
        switch (redirection) {
            case "This":
                wait.setVisibility(View.GONE);
                bundle.putSerializable("affichage", "");
                fragment = new FragmentChangeClasse();
                break;
            case "AddClass":
                wait.setVisibility(View.GONE);
                fragment = new FragmentAddExtraClass();
                bundle.putSerializable("seance", seance);
                bundle.putSerializable("affichage", "");
                break;
            case "AfficherTous":
                fragment = new FragmentChangeClasse();
                bundle.putSerializable("affichage", "consulter_liste_seances");
                break;
            default:
                wait.setVisibility(View.GONE);
                System.out.println("dans esle relev");
                fragment = new FragmentTeacherSpace();
                break;
        }
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public FragmentChangeClasse() {
    }

    private void goTo() {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", "add_class");
        startActivity(intent);
    }
}