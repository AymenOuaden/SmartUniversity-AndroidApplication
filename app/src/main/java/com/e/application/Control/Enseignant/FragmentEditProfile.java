package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.e.application.Model.Enseignant;
import com.e.application.Model.Utilisateur;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("NullableProblems")
public class FragmentEditProfile extends Fragment {
    // les attributs
    View view;
    Enseignant enseignant;
    String token;
    private RelativeLayout wait;
    private EditText user, pass, nom, prenom, adress, email, telephone;
    private String message = "";
    private String etat = "";
    private Button save;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();

    @SuppressLint({"ResourceType", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.edit);

        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
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
        assert etat != null;
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
        setView(enseignant);

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

    public FragmentEditProfile() {
    }

    // la méthode appel l'API pour récupérer les données d'un enseignant
    public void getEnseignant() {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<Enseignant> call = api1.getEnsignant(enseignant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<Enseignant>() {
            @Override
            public void onResponse(Call<Enseignant> call, Response<Enseignant> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    assert response.body() != null;
                    enseignant = response.body();
                    loginPreferenceConfig.setEnseignant(enseignant);
                    String m = getResources().getString(R.string.modifier_ok);
                    refreshApp(m, "ok");

                } else {
                    wait.setVisibility(View.GONE);
                    refreshApp(getResources().getString(R.string.modifier_not_ok), "not ok");
                }
            }

            @Override
            public void onFailure(Call<Enseignant> call, Throwable t) {
                wait.setVisibility(View.GONE);
                refreshApp(getResources().getString(R.string.network), "not ok");

            }
        });
    }

    // la méthode affiche les informations de l'enseignant
    public void setView(Enseignant enseignant) {
        // récupération des attributs depuis layout
        user = view.findViewById(R.id.user);
        pass = view.findViewById(R.id.pass);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        mDisplayDate = view.findViewById(R.id.date);
        adress = view.findViewById(R.id.adress);
        email = view.findViewById(R.id.mail);
        telephone = view.findViewById(R.id.telephone);
        TextView grade = view.findViewById(R.id.grade);
        TextView departement = view.findViewById(R.id.departement);
        // définition des attributs avec les données de l'enseignant
        user.setText(enseignant.getUser());
        pass.setText(enseignant.getPass());
        nom.setText(enseignant.getNom());
        prenom.setText(enseignant.getPrenom());
        adress.setText(enseignant.getAdresse());
        email.setText(enseignant.getEmail());
        telephone.setText(enseignant.getTelephone());
        departement.setText(enseignant.getCode_departement().toString());
        grade.setText(enseignant.getGrade());
        /*  la ate contient à la fin 2 caractères, un espace et le "z" de "UTC"
        (Temps universel coordonné), la boucle for élimine ces deux denrniers */
        String strDate = enseignant.getDate();
        StringBuilder date = new StringBuilder();
        for (int i = 0; i <= strDate.length() - 2; i++) {
            date.append(strDate.charAt(i));
        }
        enseignant.setDate(date.toString());
        mDisplayDate.setText(date.toString());
        // picker de date
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                @SuppressLint({"NewApi", "LocalSuppress"})
                DatePickerDialog dialog = new DatePickerDialog(
                        Objects.requireNonNull(getActivity()),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                mDisplayDate.setText(date);
            }
        };
    }

    // la méthoode edit vérifie les nouvelles informations, puis appel l api
    private void edit() {
        // récupération et vérification des nouvelles informations
        user = view.findViewById(R.id.user);
        pass = view.findViewById(R.id.pass);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        adress = view.findViewById(R.id.adress);
        email = view.findViewById(R.id.mail);
        telephone = view.findViewById(R.id.telephone);
        if (pass.getText().toString().length() < 6) {
            refreshApp(getResources().getString(R.string.error_lenght), "not ok");
        } else if (telephone.getText().toString().length() != 10) {
            refreshApp(getResources().getString(R.string.phone_erreur), "not ok");
        } else {
            // créaction d'un nouveau objet après la vérification des nouvelles données
            int id = enseignant.getId_utilisateur();
            String string_user = user.getText().toString();
            String string_pass = pass.getText().toString();
            String string_nom = nom.getText().toString();
            String string_prenom = prenom.getText().toString();
            mDisplayDate = view.findViewById(R.id.date);
            String d = mDisplayDate.getText().toString();
            String string_adress = adress.getText().toString();
            String string_email = email.getText().toString();
            String string_telephone = telephone.getText().toString();
            // le nouveau objet
            final Utilisateur utilisateur = new Utilisateur(id, string_user, string_pass, string_nom, string_prenom, string_adress, d, string_email, string_telephone, enseignant.getUser_type());
            final Enseignant enseignant_new = new Enseignant(utilisateur, enseignant.getGrade(), enseignant.getCode_departement());
            // vérification si le nouveau objet est egale à l'ancien( pas de modification)
            if (enseignant.toString().equals(enseignant_new.toString())) {
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
                            wait.setVisibility(View.GONE);
                            getEnseignant();

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
        FragmentEditProfile fragmentEditProfile = new FragmentEditProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        bundle.putSerializable("etat", etat);
        fragmentEditProfile.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentEditProfile);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}