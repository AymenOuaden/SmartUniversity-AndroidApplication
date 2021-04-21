package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.e.application.API.API;
import com.e.application.Control.Login;
import com.e.application.Model.Enseignant;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("NullableProblems")
public class FragmentDeleteAccount extends Fragment {

    Enseignant enseignant;
    String token;
    View view;
    private Button yes, no, delete;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private TextView message;

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_delete_account, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.delete);
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        // bouton de confirmation de la suppression du compte
        yes = view.findViewById(R.id.yes);
        // bouton d'annulation de la suppression du compte
        no = view.findViewById(R.id.no);
        // bouton du supprésion
        delete = view.findViewById(R.id.delete_account);
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                yes.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                yes_delete();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                no.setBackground(getResources().getDrawable(R.drawable.button_full_red_light_round));
                no_delete();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                delete.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                check_pass();
            }
        });

        return view;
    }

    public FragmentDeleteAccount() {

    }

    // méthode de confirmation, elle affiche la page du suppression
    private void yes_delete() {
        ConstraintLayout constraintLayout1 = view.findViewById(R.id.conf);
        RelativeLayout relativeLayout2 = view.findViewById(R.id.confirmation);
        constraintLayout1.setVisibility(View.GONE);
        relativeLayout2.setVisibility(View.VISIBLE);
    }

    // méthode d'annulation, elle sort de la page de la suppression et ouvre la page espace enseignant
    private void no_delete() {
        Intent i = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        i.putExtra("token", token);
        i.putExtra("enseignant", enseignant);
        startActivity(i);
    }

    // vérification du mot de passe
    private void check_pass() {
        message = view.findViewById(R.id.erreur);
        EditText p = view.findViewById(R.id.pass);
        String pass = p.getText().toString();
        if (pass.equals(enseignant.getPass())) {
            // cas mot de passe correct , appel la méthode du supprésion
            delete();
        } else if (pass.length() == 0) {
            // cas de mot passe vide, erreur
            message.setText(getResources().getString(R.string.empty_password));
            message.setVisibility(View.VISIBLE);
            delete.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
        } else if (pass.length() < 6) {
            // cas de mot passe inférieur à 6 caractères, erreur
            message.setText(getResources().getString(R.string.error_lenght));
            message.setVisibility(View.VISIBLE);
            delete.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));

        } else {
            // cas de mot passe incorrect, erreur
            message.setText(getResources().getString(R.string.invalide_password));
            message.setVisibility(View.VISIBLE);
            delete.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
        }
    }

    // appel l'api pour supprimer le compte
    public void delete() {
        final RelativeLayout wait = view.findViewById(R.id.wait);
        wait.setVisibility(View.VISIBLE);
        message = view.findViewById(R.id.erreur);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.supprimer(enseignant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    goToMain();
                } else {
                    wait.setVisibility(View.GONE);
                    delete.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
                    message.setText(getResources().getString(R.string.erreur_not_succ));
                    message.setVisibility(View.VISIBLE);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                wait.setVisibility(View.GONE);
                message.setText(getResources().getString(R.string.network));
                message.setVisibility(View.VISIBLE);
                delete.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
            }
        });
    }

    // ouvrire la page main après la suppression du compte
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToMain() {
        final Intent intent = new Intent(getContext(), Login.class);
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()));
        loginPreferenceConfig.setLoginStatusEnseignant(0, enseignant, token);
        intent.putExtra("message", getResources().getString(R.string.delete_succ));
        startActivity(intent);
    }

}
