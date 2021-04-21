package com.e.application.Control.Etudiant;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.CongeAcademique;
import com.e.application.Model.Etudiant;
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

public class EtudiantFragmentMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // attributs concernes le icon_menu
    private DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    RelativeLayout wait;
    String token, message, etat, redirection;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    Fragment fragment = null;
    private Etudiant etudiant;

    public EtudiantFragmentMainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etudiant_fragment_main_activity);
        // récupération d'etudiant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getApplicationContext());
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        token = loginPreferenceConfig.getToken();

        Intent intent = getIntent();
        // l'attribut message définie s'il y a un message a afficher au haut de la page d'acceuil ( espace etudiant)
        if (intent.getStringExtra("message") != null) {
            message = intent.getStringExtra("message");
            // l'attribut etat défénie l'etat de l'attribut message s'il est ok (green) or not_ok(red)
            etat = intent.getStringExtra("etat");
        } else {
            message = "vide";
            etat = "vide";
        }
        redirection = "";
        if (intent.getStringExtra("redirection") != null) {
            redirection = intent.getStringExtra("redirection");
        }
        wait = findViewById(R.id.wait);
        wait.setVisibility(View.GONE);
        // barre d'action de l'application
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // difinition des attributs dans le Drawer(le icon_menu )
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.email);
        email.setText(etudiant.getEmail());
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* si l'etudiant connecte à l'application, il est redirigé automatiquement
        à la page espace etudiant */
        if (!redirection.equals("")) {
            switch (redirection) {
                case "update_profile":
                    goToFragment(redirection);
                    break;
                case "conge_academique":
                    GetDemandeOfEtudiant();
                    break;
                case "take_presence":
                    getSeancesOfEtudiant("qr");
                    break;
                case "releve_absence":
                    getAbsencesOfEtudiant();
                    break;
                case "schedule":
                    getSeancesOfEtudiant("schedule");
                    break;
                default:
                    goToFragment("espace_etudiant");
                    break;
            }
        } else if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_espace_etudiant);
            goToFragment("espace_etudiant");
        }
    }

    // la méthode qui permet le passage entre les éléments du icon_menu de l'application
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_espace_etudiant:
                goToFragment("espace_etudiant");
                break;
            case R.id.nav_marquer_presence:
                getSeancesOfEtudiant("qr");
                break;
            case R.id.nav_gerer_absence:
                getAbsencesOfEtudiant();
                break;
            case R.id.nav_conge_academique:
                GetDemandeOfEtudiant();
                break;
            case R.id.nav_emploi:
                getSeancesOfEtudiant("schedule");
                break;
            case R.id.nav_modifier_profile:
                goToFragment("update_profile");
                break;
            case R.id.nav_supprimer_profile:
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

    /* la récupération des absences depuis la base de données , puis les envoyés
  à la page concernée basant sur la redirection*/
    void getAbsencesOfEtudiant() {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<AbsenceDepartementResponse>> call = api1.GetAbsencesOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<AbsenceDepartementResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Response<ArrayList<AbsenceDepartementResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<AbsenceDepartementResponse> absenceDepartementResponses = response.body();
                    loginPreferenceConfig.setAbsenceDepartementResponses(absenceDepartementResponses);
                    getSeancesOfEtudiant("FragmentReleveAbsences");
                } else {
                    loginPreferenceConfig.setAbsenceDepartementResponses(null);
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not ok";
                    getSeancesOfEtudiant("FragmentReleveAbsences");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_enseignant");
            }
        });
    }

    /* la récupération des seances depuis la base de données , puis les envoyés
à la page concernée basant sur la redirection*/
    void getSeancesOfEtudiant(final String redirection) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<SeanceResponse>> call = api1.GetSeancesOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<SeanceResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Response<ArrayList<SeanceResponse>> response) {
                if (response.isSuccessful()) {
                    loginPreferenceConfig.setSeanceResponsesEtudiant(response.body());
                    if (redirection.equals("qr")) {
                        goToQR();
                    } else if (redirection.equals("schedule")) {
                        goToFragment("schedule");
                    } else {
                        goToFragment("FragmentReleveAbsences");
                    }
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not ok";
                    goToFragment("espace_etudiant");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_etudiant");
            }
        });
    }

    /* la récupération de conge depuis la base de données , puis le envoyés
à la page concernée basant sur la redirection*/
    void GetDemandeOfEtudiant() {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<CongeAcademique> call = api1.GetDemandeOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<CongeAcademique>() {
            @Override
            public void onResponse(@NonNull Call<CongeAcademique> call, @NonNull Response<CongeAcademique> response) {
                if (response.isSuccessful()) {
                    CongeAcademique congeAcademique = response.body();
                    loginPreferenceConfig.setCongeAcademique(congeAcademique);
                    goToFragment("congeAcademique");
                } else {
                    goToFragment("congeAcademique");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CongeAcademique> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not ok";
                navigationView.setCheckedItem(R.id.nav_espace_enseignant);
                goToFragment("espace_etudiant");
            }
        });
    }

    // redirection vers fragment selon redirection
    public void goToFragment(String redirection) {
        wait.setVisibility(View.GONE);
        Bundle bundle = new Bundle();
        switch (redirection) {
            case "espace_etudiant":
                fragment = new EtudiantFragmentStudentSpace();
                bundle.putSerializable("message", message);
                bundle.putSerializable("etat", etat);
                navigationView.setCheckedItem(R.id.nav_espace_etudiant);
                break;
            case "take_presences":
                goToQR();
                break;
            case "congeAcademique":
                fragment = new EtudiantFragmentAcademicLeave();
                navigationView.setCheckedItem(R.id.nav_conge_academique);

                break;
            case "update_profile":
                fragment = new EtudiantFragmentEditProfile();
                navigationView.setCheckedItem(R.id.nav_modifier_profile);

                break;
            case "schedule":
                fragment = new EtudiantFragmentSchedule();
                navigationView.setCheckedItem(R.id.nav_emploi);

                break;
            case "FragmentReleveAbsences":
                fragment = new EtudiantFragmentReleveAbsences();
                navigationView.setCheckedItem(R.id.nav_gerer_absence);
                break;
        }

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "EtudiantFragmentMainActivity");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof EtudiantFragmentReleveAbsences
                    || fragment instanceof EtudiantFragmentDisplayJustification
                    || fragment instanceof EtudiantFragmentUploadJustification) {
                navigationView.setCheckedItem(R.id.nav_gerer_absence);
            } else if (fragment instanceof EtudiantFragmentEditProfile) {
                navigationView.setCheckedItem(R.id.nav_modifier_profile);
            } else if (fragment instanceof EtudiantFragmentSchedule) {
                navigationView.setCheckedItem(R.id.nav_emploi);
            } else if (fragment instanceof EtudiantFragmentStudentSpace) {
                navigationView.setCheckedItem(R.id.nav_espace_etudiant);
            } else if (fragment instanceof EtudiantFragmentAcademicLeave) {
                navigationView.setCheckedItem(R.id.nav_conge_academique);
            } else {
                navigationView.setCheckedItem(R.id.nav_espace_etudiant);
                int statu = loginPreferenceConfig.getLoginStatuKey();
                if (redirection.equals("")) {
                    wait.setVisibility(View.VISIBLE);
                    loginPreferenceConfig.clearAll();
                    loginPreferenceConfig.setLoginStatusEtudiant(statu, etudiant, token);
                    this.finishAffinity();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.back),
                            Toast.LENGTH_SHORT).show();
                    loginPreferenceConfig.clearAll();
                    loginPreferenceConfig.setLoginStatusEtudiant(statu, etudiant, token);
                    Intent intent = new Intent(this, EtudiantFragmentMainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    void goToQR() {
        wait.setVisibility(View.GONE);
        Intent intent = new Intent(this, EtudiantActivityTakePresence.class);
        startActivity(intent);
    }

    // se déconnecter, redirection vers la page de connexion ( main)
    void logOut() {
        loginPreferenceConfig.clearAll();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

}