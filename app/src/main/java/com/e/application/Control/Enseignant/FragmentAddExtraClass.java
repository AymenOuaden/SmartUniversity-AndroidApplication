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
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentConsulterSeance;
import com.e.application.Adapters.AdapterEnseignant.Adapter_Options_classes;
import com.e.application.Dots.Dot_Create_SeanceSupp;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Seance;
import com.e.application.Model.SeanceSupp;
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

public class FragmentAddExtraClass extends Fragment implements Adapter_FragmentConsulterSeance.ItemClickListener, Adapter_Options_classes.ItemClickListener {

    View view;
    String token, affichage;
   private String jour_affiche = "";
    Enseignant enseignant;
    private ArrayList<Seance> seances;
    private ArrayList<SeanceResponse> seanceResponses;
    private Seance seance;
    private Button demander;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private RelativeLayout wait;
    private TextView message_top;
    private String message = "", etat = "";


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
        // récupération depuis bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        affichage = (String) bundle.getSerializable("affichage");
        // selon la valeur de l'attribut "affichage", on récupère la page(layout) concernée
        assert affichage != null;
        if (affichage.equals("consulter_liste_seances")) {
            // le choix d'affichage de toutes les séances
            view = inflater.inflate(R.layout.fragment_consulter_seances, container, false);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.consulter_seance);
            TextView title = view.findViewById(R.id.title);
            title.setText(getResources().getString(R.string.title_consulter_ajouter));
            // on affiche les séances à l'aide d'un adapter qui gère les séances dans un recylerviwe
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentConsulterSeance adapter = new Adapter_FragmentConsulterSeance(getContext(), seances);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            // le choix d'une séance depuis la liste précédente ( dans le cas if )
            view = inflater.inflate(R.layout.fragment_add_extra_class, container, false);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.ajouter_seance);

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
                }
            });
            change_classe.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    change_classe.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    change_classe.setTextColor(getResources().getColor(R.color.white));
                    //goToOptionDeGestion("ChangeClass");
                    goTo();
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
            seance = (Seance) bundle.getSerializable("seance");
            TextView code_module = view.findViewById(R.id.code_module);
            code_module.setText(seance.getCode_module());
            TextView groupe = view.findViewById(R.id.groupe);
            groupe.setText(String.valueOf(seance.getGroupe()));
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
            // récupération de spinner depuis layout
           /* le "spinner" qui nous donne la possibilité de choisir un élément depuis plusieurs
            éléments, dans notre cas on choisit les jours et les heures des séances supplémentaires */
            final Spinner dropdown_jour = view.findViewById(R.id.spinner1);
            final Spinner dropdown_heure = view.findViewById(R.id.spinner2);
            //creation les listes des élémnets des spinners.
            String[] items_jour = new String[]{getResources().getString(R.string.dimanche), getResources().getString(R.string.lundi),
                    getResources().getString(R.string.mardi), getResources().getString(R.string.mercredi),
                    getResources().getString(R.string.jeudi)};
            String[] items_heure = new String[]{"8:30", "10:00", "11:30", "13:00", "14:30"};
            /*création des adaptateurs pour décrire comment les éléments sont affichés, les adaptateurs
             sont utilisés à plusieurs endroits dans Android. */
            final ArrayAdapter<String> adapter_jour = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items_jour);
            final ArrayAdapter<String> adapter_heure = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items_heure);
            dropdown_jour.setAdapter(adapter_jour);
            dropdown_heure.setAdapter(adapter_heure);
            // le bouton "demander" appel la méthode "ajouterSeanceSupp"
            demander = view.findViewById(R.id.demander);
            demander.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    demander.setBackground(getResources().getDrawable(R.drawable.button_full_black_round));
                    demander.setTextColor(getResources().getColor(R.color.white));
                    //récupération la valeur du spinner
                    Spinner spinner_jour = view.findViewById(R.id.spinner1);
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

                    Spinner spinner_heure = view.findViewById(R.id.spinner2);
                    String heure = spinner_heure.getSelectedItem().toString();

                    // création d'un objet de séance supplémentaire avec les nouvelles informations
                    Dot_Create_SeanceSupp dot_create_seanceSupp = new Dot_Create_SeanceSupp(seance.getCode_seance(), jour, heure);
                    dot_create_seanceSupp.setCode_seance(seance.getCode_seance());
                    dot_create_seanceSupp.setJour(jour);
                    dot_create_seanceSupp.setHeure(heure);
                    // appel la méthode d'ajout de séance supplémentaire
                    ajouterSeanceSupp(dot_create_seanceSupp);
                }
            });

            GetSeancesCompleteEnseignant(seance);
        }
        return view;
    }

    public FragmentAddExtraClass() {
    }


    // la méthode qui fait la sélectionne d'une séance
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void gerer(View view, int position) {
        seance = seances.get(position);
        goToOptionDeGestion("AddClass");
    }

    // récupération des séances supplémentaires s'ils existent
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
                    wait.setVisibility(View.GONE);
                    seanceResponses = response.body();
                    assert seanceResponses != null;
                    for (SeanceResponse seanceResponse : seanceResponses) {
                        if (seanceResponse.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                            setView(seanceResponse);
                        }
                    }
                } else {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.erreur_not_succ);
                    message_top.setVisibility(View.VISIBLE);
                    message_top.setTextColor(getResources().getColor(R.color.red));
                    message_top.setText(message);
                    message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                //etat = "not_ok";
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setTextColor(getResources().getColor(R.color.red));
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    // afficher les séances supplémentaire dans layout
    public void setView(SeanceResponse seanceResponses) {
        ArrayList<SeanceSupp> seancesSupps = new ArrayList<>();
        // test s'il existe des sénaces supplémentaire
        if (seanceResponses.getSeancesSupp() != null) {
            for (SeanceSupp seanceSupp : seanceResponses.getSeancesSupp()) {
                if (seanceSupp != null) {
                    seancesSupps.add(seanceSupp);
                }
            }
            // on affiche les séances supplémentaire à l'aide d'un adapter
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_Options_classes adapter_options_classes = new Adapter_Options_classes(getContext(),seancesSupps);
            adapter_options_classes.setClickListener(this);
            recyclerView.setAdapter(adapter_options_classes);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            // s'il y a aucune séance supplémentaire , on affiche un message
            TextView empty = view.findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }
        wait.setVisibility(View.GONE);
    }

    
    // la méthode qui appel l API pour ajouter une séance supplémentaire
    private void ajouterSeanceSupp(Dot_Create_SeanceSupp dot_create_seanceSupp) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/create/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.CreateSeanceSupp(dot_create_seanceSupp, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.ajouter_seance_ok);
                    etat = "ok";
                    // refreshThisFragment();
                    goToOptionDeGestion("This");

                } else {
                    //   TextView message = view.findViewById(R.id.message);
                    // message.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.GONE);
                    demander = view.findViewById(R.id.demander);
                    demander.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                    demander.setTextColor(getResources().getColor(R.color.black));
                    message = getResources().getString(R.string.erreur_jour_heure);
                    etat = "not_ok";
                    goToOptionDeGestion("This");
                }
            }

            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                //TextView message = view.findViewById(R.id.message_top);
                //message.setVisibility(View.VISIBLE);
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                demander = view.findViewById(R.id.demander);
                demander.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                demander.setTextColor(getResources().getColor(R.color.black));
                //  refreshThisFragment();
                goToOptionDeGestion("This");
            }
        });
    }

    // suppression d'une séance suplaimentaire
    @Override
    public void supprimer(View view, String code_seance, int code_seance_supp) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.DeleteSeanceSupp(code_seance, code_seance_supp, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.supprimer_seance_ok);
                    etat = "ok";
                    //refreshThisFragment();
                    goToOptionDeGestion("This");
                } else {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    goToOptionDeGestion("This");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                goToOptionDeGestion("This");
            }
        });
    }

    // la méthode redirect l'application vers une fragment selon l'attribut " redirection"
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
                fragment = new FragmentAddExtraClass();
                break;
            case "AddClass":
                fragment = new FragmentAddExtraClass();
                bundle.putSerializable("seance", seance);
                bundle.putSerializable("affichage", "");
                break;
            case "AfficherTous":
                fragment = new FragmentAddExtraClass();
                bundle.putSerializable("affichage", "consulter_liste_seances");
                break;
            case "ChangeClass":
                wait.setVisibility(View.GONE);
                bundle.putSerializable("affichage", "");
                fragment = new FragmentChangeClasse();
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

    private void goTo() {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", "change_class");
        startActivity(intent);
    }
}
