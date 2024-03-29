package com.e.application.Control.Etudiant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e.application.API.API;
import com.e.application.Helpers.LoginResponse;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Utilisateur;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("NullableProblems")
public class EtudiantFragmentEditProfile extends Fragment {
    // les attributs
    private View view;
    private Etudiant etudiant;
    private RelativeLayout wait;
    private EditText user, pass, adress, email, telephone;
    private String message = "", etat = "", token;
    private Button save;
    private LoginPreferencesConfig loginPreferenceConfig;
    private Gson gson = new Gson();
    private String user_s,pass_s;
    @SuppressLint({"ResourceType", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.etudiant_fragment_edit_profile, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.edit);

        // récupération d'etudiant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        token = loginPreferenceConfig.getToken();

        Bundle bundle = getArguments();
        // vérification s'il y a un message.(le message est défini aprèes la modification du profile)
        assert bundle != null;
        if (bundle.getSerializable("message") != null) {
            // l'attribut message contient la valeur du message à afficher
            message = (String) bundle.getSerializable("message");
            // l'attribut etat peut etre"ok"( message du succés) ou "not ok"(message d'erreur)
            etat = (String) bundle.getSerializable("etat");
        }
        if (etat.equals("ok")) {
            // affichage d'un message du succès
            TextView messageOk = view.findViewById(R.id.message);
            messageOk.setVisibility(View.VISIBLE);
            messageOk.setText(getResources().getString(R.string.modifier_ok));
            messageOk.setBackgroundColor(getResources().getColor(R.color.light_back));
            messageOk.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
        } else if (etat.equals("not ok")) {
            // affichage d'un message d'erreur
            TextView messageNotOk = view.findViewById(R.id.message);
            messageNotOk.setVisibility(View.VISIBLE);
            messageNotOk.setText(message);
            messageNotOk.setBackgroundColor(getResources().getColor(R.color.red_light));
            messageNotOk.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
        }
        wait = view.findViewById(R.id.wait);
        setView(etudiant);

        // bouton qui enregistre les modifications
        save = view.findViewById(R.id.save_changes);
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                save.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                edit();
            }
        });

        return view;
    }

    public EtudiantFragmentEditProfile() {
    }

    // la méthode appel l'API pour récupérer les données d'un etudiant
    public void getEtudiant(final String user_s, final String pass_s) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<Etudiant> call = api1.getEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<Etudiant>() {
            @Override
            public void onResponse(Call<Etudiant> call, Response<Etudiant> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    assert response.body() != null;
                    etudiant = response.body();
                    etudiant.setUser(user_s);
                    etudiant.setUser(pass_s);
                    loginPreferenceConfig.setEtudiant(etudiant);
                    String m = getResources().getString(R.string.modifier_ok);
                    refreshApp(m, "ok");
                } else {
                    wait.setVisibility(View.GONE);
                    refreshApp(getResources().getString(R.string.modifier_not_ok), "not ok");
                }
            }

            @Override
            public void onFailure(Call<Etudiant> call, Throwable t) {
                wait.setVisibility(View.GONE);
                refreshApp(getResources().getString(R.string.network), "not ok");

            }
        });
    }

    // la méthode affiche les informations de l'etudiant
    public void setView(Etudiant etudiant) {
        // récupération des attributs depuis layout
        user = view.findViewById(R.id.user);
        pass = view.findViewById(R.id.pass);
        EditText nom = view.findViewById(R.id.nom);
        EditText prenom = view.findViewById(R.id.prenom);
        TextView mDisplayDate = view.findViewById(R.id.date);
        adress = view.findViewById(R.id.adress);
        email = view.findViewById(R.id.mail);
        telephone = view.findViewById(R.id.telephone);
        TextView departement = view.findViewById(R.id.departement);
        TextView annee = view.findViewById(R.id.annee);
        TextView specialite = view.findViewById(R.id.specialite);
        TextView groupe = view.findViewById(R.id.groupe);
        TextView etat = view.findViewById(R.id.etat);
        // définition des attributs avec les données de l'enseignant
        user.setText(etudiant.getUser());
        pass.setText(etudiant.getPass());
        nom.setText(etudiant.getNom());
        prenom.setText(etudiant.getPrenom());
        adress.setText(etudiant.getAdresse());
        email.setText(etudiant.getEmail());
        telephone.setText(etudiant.getTelephone());
        departement.setText(etudiant.getCode_departement().toString());
        annee.setText(String.valueOf(etudiant.getAnnee()));
        specialite.setText(etudiant.getSpecialite().toString());
        groupe.setText(String.valueOf(etudiant.getGroupe()));
        etat.setText(etudiant.getEtat_etudiant().toString());
        /*  la ic_date contient à la fin 2 caractères, un espace et le "z" de "UTC"
        (Temps universel coordonné), la boucle for élimine ces deux denrniers */
        String strDate = etudiant.getDate();
        StringBuilder date = new StringBuilder();
        for (int i = 0; i <= strDate.length() - 2; i++) {
            date.append(strDate.charAt(i));
        }
        etudiant.setDate(date.toString());
        mDisplayDate.setText(date.toString());
    }

    // la méthoode edit vérifie les nouvelles informations, puis appel l api
    private void edit() {
        // récupération et vérification des nouvelles informations
        user = view.findViewById(R.id.user);
        pass = view.findViewById(R.id.pass);
        adress = view.findViewById(R.id.adress);
        email = view.findViewById(R.id.mail);
        telephone = view.findViewById(R.id.telephone);
        if (pass.getText().toString().length() < 6) {
            refreshApp(getResources().getString(R.string.error_lenght), "not ok");
        } else if (telephone.getText().toString().length() != 10) {
            refreshApp(getResources().getString(R.string.phone_erreur), "not ok");
        } else {
            // créaction d'un nouveau objet après la vérification des nouvelles données
            int id = etudiant.getId_utilisateur();
            String string_user = user.getText().toString();
            String string_pass = pass.getText().toString();
            String string_adress = adress.getText().toString();
            String string_email = email.getText().toString();
            String string_telephone = telephone.getText().toString();
            // le nouveau objet
            Utilisateur.Type_Utilisateur type = Utilisateur.Type_Utilisateur.etudiant;
            final Utilisateur utilisateur = new Utilisateur(id, string_user, string_pass, etudiant.getNom(),
                    etudiant.getPrenom(), string_adress, etudiant.getDate(), string_email, string_telephone, type);
            final Etudiant etudiant_new = new Etudiant(utilisateur, etudiant.getAnnee(), etudiant.getSpecialite(),
                    etudiant.getSection(), etudiant.getGroupe(), etudiant.getEtat_etudiant(), etudiant.getCode_departement());
            // vérification si le nouveau objet est egale à l'ancien( pas de modification)
            if (etudiant.toString().equals(etudiant_new.toString())) {
                refreshApp(getResources().getString(R.string.no_change_erreur), "not ok");
            } else {
                wait.setVisibility(View.VISIBLE);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/update/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final API api = retrofit.create(API.class);
                Call<LoginResponse> call = api.UpdateUser(utilisateur, "Bearer " + token);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        assert response.body() != null;
                        if (response.isSuccessful()) {
                            user_s = utilisateur.getUser();
                            pass_s = utilisateur.getPass();
                            wait.setVisibility(View.GONE);
                            getEtudiant(user_s,pass_s);
                        } else {
                            wait.setVisibility(View.GONE);
                            refreshApp(getResources().getString(R.string.modifier_not_ok), "not ok");
                        }
                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        wait.setVisibility(View.GONE);
                        refreshApp(getResources().getString(R.string.network), "not ok");
                    }
                });
            }
        }
    }

    // actualiser la page
    private void refreshApp(String message, String etat) {
        EtudiantFragmentEditProfile etudiantFragmentEditProfile = new EtudiantFragmentEditProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        bundle.putSerializable("etat", etat);
        etudiantFragmentEditProfile.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"}) FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, etudiantFragmentEditProfile);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}