package com.e.application.Control.Enseignant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e.application.API.API;
import com.e.application.Control.Login;
import com.e.application.Helpers.NotificationResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Enseignant;
import com.e.application.Model.NotificationChangementSeance;
import com.e.application.Model.NotificationSeanceSupp;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentMainActivityTeacher extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // attributs concernes le icon_menu
    private DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    RelativeLayout wait;
    Enseignant enseignant;
    String token, message = "", etat = "";
    ArrayList<Seance> seances;
    String redirection;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    Fragment fragment = null;
    ArrayList<SeanceResponse> seanceResponses;
    ImageView notification_non, notification_oui;
    TextView notification_texte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_activity);

        // barre d'action de l'application
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // difinition des attributs dans le Drawer(le icon_menu )
        drawer = findViewById(R.id.drawer_layout);
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        // recupération des notifications
        notification_non = findViewById(R.id.notifiaction_non);
        notification_oui = findViewById(R.id.notifiaction_oui);
        notification_texte = findViewById(R.id.notification_texte);

        notification_oui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_oui.setVisibility(View.GONE);
                notification_non.setVisibility(View.VISIBLE);
                getNotification();
            }
        });
        notification_non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_oui.setVisibility(View.GONE);
                notification_non.setVisibility(View.VISIBLE);
                getNotification();
            }
        });

        getNotificationDefault();


        Intent intent = getIntent();
        // l'attribut message définie s'il y a un message a afficher au haut de la page d'acceuil ( espace enseignant)
        if (intent.getStringExtra("message") != null) {
            message = intent.getStringExtra("message");
            // l'attribut etat défénie l'etat de l'attribut message s'il est ok (green) or not_ok(red)
            etat = intent.getStringExtra("etat");
        } else {
            message = "";
            etat = "";
        }
        redirection = "";
        if (intent.getStringExtra("redirection") != null) {
            redirection = intent.getStringExtra("redirection");
        }
        wait = findViewById(R.id.wait);

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.email);
        email.setText(enseignant.getEmail());
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* si l'enseignant connecte à l'application, il est redirigé automatiquement
        à la page espace enseignant */
        if (!redirection.equals("")) {
            switch (redirection) {
                case "update_profile":
                case "delete_account":
                    goToFragment(redirection);
                    break;
                case "statistics":
                    GetSeancesCompleteEnseignant();
                    break;
                default:
                    getListeSeances(redirection);
                    break;
            }
        } else if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_espace_enseignant);
            goToFragment("espace_enseignant");
        }
    }

    public FragmentMainActivityTeacher() {
    }

    // la méthode qui permet le passage entre les éléments du icon_menu de l'application
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_espace_enseignant:
                message = "";
                etat = "";
                getNotificationDefault();
                goToFragment("espace_enseignant");
                break;
                /* les 4 cas suivants nécessitent la récupération des séances
                avant dû passer à une autre page */
            case R.id.nav_marquer_presence:
                getNotificationDefault();
                getListeSeances("take_presences");
                break;
            case R.id.nav_gerer_absence:
                getNotificationDefault();
                getListeSeances("manage_absences");
                break;
            case R.id.nav_ajouter_seance:
                getNotificationDefault();
                getListeSeances("add_class");
                break;
            case R.id.nav_changer_seance:
                getNotificationDefault();
                getListeSeances("change_class");
                break;
            case R.id.nav_modifier_profile:
                getNotificationDefault();
                goToFragment("update_profile");
                break;
            case R.id.nav_supprimer_profile:
                getNotificationDefault();
                goToFragment("delete_account");
                break;
            case R.id.nav_deconnecter:
                logOut();
                break;
        }
        // fermeture de icon_menu
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* la récupération des séances depuis la base de données , puis les envoyés
     à la page concernée basant sur la redirection*/
    void getListeSeances(final String redirection) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<Seance>> call = api1.getSeance(enseignant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<Seance>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Seance>> call, @NonNull Response<ArrayList<Seance>> response) {
                if (response.isSuccessful()) {
                    seances = new ArrayList<>();
                    seances = response.body();
                    loginPreferenceConfig.setSeances(seances);
                    if (!redirection.equals("This"))
                        goToFragment(redirection);
                    else {
                        wait.setVisibility(View.GONE);
                    }
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not ok";
                    navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                    goToFragment("espace_enseignant");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Seance>> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_enseignant");
            }
        });
    }

    private void GetSeancesCompleteEnseignant() {
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
                    loginPreferenceConfig.setSeanceResponsesEtudiant(seanceResponses);
                    goToFragment("statistics");
                } else {
                    wait.setVisibility(View.GONE);
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not ok";
                    navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                    goToFragment("espace_enseignant");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_enseignant");
            }
        });
    }

    // la méthode déclenche automatiquement avec chaque chargement de la page
    private void getNotificationDefault() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<NotificationResponse> call = api1.GetNotificationsOfUser(enseignant.getId_utilisateur(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                if (response.isSuccessful()) {
                    setViewNotificationDefault("succ", response.body());
                } else {
                    setViewNotificationDefault("empty", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                notification_oui.setVisibility(View.GONE);
                notification_non.setVisibility(View.VISIBLE);
                setViewNotificationDefault("network", null);
            }
        });
    }

    private void setViewNotificationDefault(String affichage, NotificationResponse notification) {
        if (affichage.equals("succ")) {
            boolean vu = true;
            for (NotificationSeanceSupp notificationSeanceSupp : notification.getNotificationsSupp()) {
                if (!notificationSeanceSupp.isIs_vue()) {
                    vu = false;
                }
            }
            for (NotificationChangementSeance notificationChangementSeance : notification.getNotificationsChangement()) {
                if (!notificationChangementSeance.isIs_vue()) {
                    vu = false;
                }
            }
            if (vu) {
                notification_non.setVisibility(View.VISIBLE);
                notification_oui.setVisibility(View.GONE);
            } else {
                notification_oui.setVisibility(View.VISIBLE);
                notification_non.setVisibility(View.GONE);
            }
            getListeSeances("This");
        } else {
            notification_non.setVisibility(View.VISIBLE);
        }
    }

    // la méthode déclenche lorsque on clique sur l icon de notification
    private void getNotification() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<NotificationResponse> call = api1.GetNotificationsOfUser(enseignant.getId_utilisateur(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                if (response.isSuccessful()) {
                    goToFragment("notification");
                } else {
                    message = getResources().getString(R.string.notification_vide);
                    etat = "vide";
                    goToFragment("notification");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_enseignant");
            }
        });
    }


    // redirection vers fragment selon redirection
    public void goToFragment(String redirection) {
        wait.setVisibility(View.GONE);
        Bundle bundle = new Bundle();
        switch (redirection) {
            case "espace_enseignant":
                fragment = new FragmentTeacherSpace();
                bundle.putSerializable("message", message);
                bundle.putSerializable("etat", etat);
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                break;
            case "take_presences":
                fragment = new FragmentTakePresences();
                bundle.putSerializable("affichage", "consulter_liste_seances");
                navigationView.setCheckedItem(R.id.nav_marquer_presence);
                break;
            case "manage_absences":
                fragment = new FragmentManageAbsences();
                navigationView.setCheckedItem(R.id.nav_gerer_absence);
                break;
            case "statistics":
                fragment = new FragmentStatistics();
                navigationView.setCheckedItem(R.id.nav_gerer_absence);
                break;
            case "add_class":
                fragment = new FragmentAddExtraClass();
                bundle.putSerializable("affichage", "consulter_liste_seances");
                navigationView.setCheckedItem(R.id.nav_ajouter_seance);
                break;
            case "change_class":
                fragment = new FragmentChangeClasse();
                bundle.putSerializable("affichage", "consulter_liste_seances");
                navigationView.setCheckedItem(R.id.nav_changer_seance);
                break;
            case "update_profile":
                fragment = new FragmentEditProfile();
                navigationView.setCheckedItem(R.id.nav_modifier_profile);
                break;
            case "delete_account":
                fragment = new FragmentDeleteAccount();
                navigationView.setCheckedItem(R.id.nav_supprimer_profile);
                break;
            case "notification":
                bundle.putSerializable("message", message);
                bundle.putSerializable("etat", etat);
                fragment = new FragmentNotificationTeacher();
                break;
            case "This":
                fragment = new FragmentNotificationTeacher();
                break;
        }
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "FragmentMainActivityTeacher");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // This makes sure the NavigationDrawer gets opened when the upper left button is pressed
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        getNotificationDefault();
        message = "";
        etat = "";
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof FragmentManageAbsences
                    || fragment instanceof FragmentReleveAbsences
                    || fragment instanceof FragmentExcludedStudents
                    || fragment instanceof FragmentDisplayJustification
                    || fragment instanceof FragmentUploadJustification
                    || fragment instanceof FragmentStatistics
            ) {
                navigationView.setCheckedItem(R.id.nav_gerer_absence);
            } else if (fragment instanceof FragmentAddExtraClass) {
                navigationView.setCheckedItem(R.id.nav_ajouter_seance);
            } else if (fragment instanceof FragmentTakePresences) {
                navigationView.setCheckedItem(R.id.nav_marquer_presence);
            } else if (fragment instanceof FragmentChangeClasse) {
                navigationView.setCheckedItem(R.id.nav_changer_seance);
            } else if (fragment instanceof FragmentDeleteAccount) {
                navigationView.setCheckedItem(R.id.nav_supprimer_profile);
            } else if (fragment instanceof FragmentEditProfile) {
                navigationView.setCheckedItem(R.id.nav_modifier_profile);
            } else if (fragment instanceof FragmentTeacherSpace) {
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);

            } else if (fragment instanceof FragmentNotificationTeacher) {
                System.out.println();
            } else {
                int statu = loginPreferenceConfig.getLoginStatuKey();
                if (redirection.equals("")) {
                    wait.setVisibility(View.VISIBLE);
                    loginPreferenceConfig.clearAll();
                    loginPreferenceConfig.setLoginStatusEnseignant(statu, enseignant, token);
                    this.finishAffinity();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.back),
                            Toast.LENGTH_SHORT).show();
                    loginPreferenceConfig.clearAll();
                    loginPreferenceConfig.setLoginStatusEnseignant(statu, enseignant, token);
                    Intent intent = new Intent(this, FragmentMainActivityTeacher.class);
                    startActivity(intent);
                }
            }
        }
    }

    // se déconnecter, redirection vers la page de connexion ( main)
    void logOut() {
        loginPreferenceConfig.clearAll();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }


}