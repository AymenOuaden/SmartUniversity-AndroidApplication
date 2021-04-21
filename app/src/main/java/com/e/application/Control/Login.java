package com.e.application.Control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.application.API.API;
import com.e.application.Control.Enseignant.FragmentMainActivityTeacher;
import com.e.application.Control.Etudiant.EtudiantFragmentMainActivity;
import com.e.application.Control.JavaMail.ForgotPassword;
import com.e.application.Control.OfflineMode.MainHorsLigne;
import com.e.application.Dots.Dot_Login_User;
import com.e.application.Helpers.LoginResponse;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Utilisateur;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@SuppressWarnings({"deprecation"})
public class Login extends AppCompatActivity {
    // les attributs
    private LoginPreferencesConfig loginPreferenceConfig;
    EditText user, pass;
    TextView message;
    private int s = 0;
    RelativeLayout wait;
    ImageView visible_pass, invisible_pass;
    TextView offline_mode;

    public Login() {
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //récupération du nom d'utilisateur et mot de passe
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isRTL(this)) {
            // Force a right-aligned text entry
            pass.setTextDirection(View.TEXT_DIRECTION_RTL);
            user.setTextDirection(View.TEXT_DIRECTION_RTL);
            // Make the "Enter password" hint display on the right hand side
            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        message = findViewById(R.id.msg);

        // la visibilité de mot de passe
        visible_pass = findViewById(R.id.visible_pass);
        invisible_pass = findViewById(R.id.invisible_pass);
        visible_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // invisible password
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                visible_pass.setVisibility(View.GONE);
                invisible_pass.setVisibility(View.VISIBLE);

            }
        });
        invisible_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // visible password
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                visible_pass.setVisibility(View.VISIBLE);
                invisible_pass.setVisibility(View.GONE);
            }
        });

        // relative wait est affichée lorsque on attend la reponse de l'api
        wait = findViewById(R.id.wait);
        // boutons
        final Button button_login = findViewById(R.id.log);
        final Button button_forgot = findViewById(R.id.forg);
        final ImageView facebook = findViewById(R.id.fb);
        final ImageView github = findViewById(R.id.git);
        final ImageView linkedin = findViewById(R.id.in);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                button_login.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                login(arg0);
            }
        });

        button_forgot.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View arg0) {
                button_forgot.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                button_forgot.setTextColor(getResources().getColor(R.color.white));
                forgotPassword();
            }
        });
        offline_mode = findViewById(R.id.offline_mode);
        offline_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offline_mode.setTextColor(getResources().getColor(R.color.colorAccent));
                goToModeOfline();
            }
        });

        // direction vers la page de facebook
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getOpenWebIntent("Facebook");
            }
        });
        // direction vers la page de github
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOpenWebIntent("GitHub");
            }
        });
        // direction vers la page de linkedin
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOpenWebIntent("LinkedIn");
            }
        });
        // vérification des erreurs de onnexion
        Intent intent = getIntent();
        if (intent.getStringExtra("message") != null) {
            message.setVisibility(View.VISIBLE);
            String message_affiche = intent.getStringExtra("message");
            message.setText(message_affiche);
            // s'il sagit pas d'un message d'erreur
            if (message_affiche != null) {
                if (message_affiche.equals(getResources().getString(R.string.delete_succ))) {
                    message.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_checkbox_coloraccent, 0, 0, 0);
                    message.setTextColor(getResources().getColor(R.color.black));
                } else if (message_affiche.equals(getResources().getString(R.string.network))) {
                    offline_mode.setVisibility(View.VISIBLE);
                }
            }
        }
        // vérification si la session est enregistrée
        loginPreferenceConfig = new LoginPreferencesConfig(getApplicationContext());
        if (loginPreferenceConfig.getLoginStatuKey() == 1) {
            Gson gson = new Gson();
            String json_enseignant = loginPreferenceConfig.getEnseignant();
            String json_etudiant = loginPreferenceConfig.getEtudiant();
            if (!json_enseignant.equals("null") && json_enseignant.length() > 4) {
                    /* si la session est enregistré , on affiche directement la page d'acceil du l'enseignant
             sinon on affiche la page de connexion */
                Enseignant ens = gson.fromJson(json_enseignant, Enseignant.class);
                GoToEnseignant(1, ens, loginPreferenceConfig.getToken());
            } else {
                Etudiant etudiant = gson.fromJson(json_etudiant, Etudiant.class);
                GoToEtudiant(1, etudiant, loginPreferenceConfig.getToken());
            }
        }
    }


    //checkbox (rester connecter )
    public void chk(View v) {
        CheckBox check = findViewById(R.id.chk);
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            check.setButtonDrawable(R.drawable.icon_chekbox_full_colorprimarydark);
            s = 1;
        } else {
            check.setButtonDrawable(R.drawable.icon_chekbox_empty_colorprimarydark);
        }
    }

    // la méthode de vérification des informations saisies par l'utilisateur
    public void login(View view) {
        wait.setVisibility(View.VISIBLE);
        /* s'il y a des erreurs de la taille des informations saisies, on envoie un message d'erreur
         dans "Intent" , et redémarre l'application*/
        final Intent intent = new Intent(this, Login.class);
        if (pass.getText().length() == 0 || user.getText().length() == 0) {
            intent.putExtra("message", getResources().getString(R.string.not_empty));
            startActivity(intent);
        } else if (pass.getText().toString().length() < 6) {
            intent.putExtra("message", getResources().getString(R.string.error_lenght));
            startActivity(intent);
        }
        /* s'il y a aucune erreur, on fait un appel a 'L'API' avec 'retrofit'*/
        else {
            String url = "http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            final API api = retrofit.create(API.class);
            //boolean isAndroid = true;
            Dot_Login_User dots_Login_User = new Dot_Login_User(user.getText().toString(), pass.getText().toString());
            Call<LoginResponse> call = api.AuthenticationService(dots_Login_User);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        /* si la réponse qui vient de l'api est correcte , on appelle la méthode
                        'getUser'*/
                        LoginResponse loginResponse = response.body();
                        assert loginResponse != null;
                        getUser(loginResponse.getId(), loginResponse.getToken());
                    } else {
                        /* s'il y a aucun utilisateur dans la base de données avec
                        les informations saisies */
                        wait.setVisibility(View.GONE);
                        intent.putExtra("message", getResources().getString(R.string.error_user_pass));
                        startActivity(intent);
                    }
                }

                @Override
                // s'il y a une erreur d'appel d'API
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    wait.setVisibility(View.GONE);
                    System.out.println("fail !");
                    intent.putExtra("message", getResources().getString(R.string.network));
                    startActivity(intent);
                }
            });
        }
    }


    // la récupération des informations d'enseignant
    public void getUser(final int id, final String token) {
        final Intent intent = new Intent(this, Login.class);
        String url = "http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/";
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        final Call<Utilisateur> call = api1.GetUserByID(id, "Bearer " + token);
        call.enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(@NonNull Call<Utilisateur> call, @NonNull Response<Utilisateur> response) {
                if (response.isSuccessful()) {
                    Utilisateur utilisateur = response.body();
                    assert utilisateur != null;
                    switch (utilisateur.getUser_type()) {
                        case enseignant:
                        case chefDepartement:
                        case responsableFormation:
                            getEnseignant(id, token);
                            break;
                        case etudiant:
                            getEtudiant(id, token);
                            break;
                        default:
                            wait.setVisibility(View.GONE);
                            intent.putExtra("message", getResources().getString(R.string.admin));
                            startActivity(intent);
                            break;
                    }
                } else {
                    intent.putExtra("message", getResources().getString(R.string.erreur_not_succ));
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Utilisateur> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                intent.putExtra("message", getResources().getString(R.string.network));
                startActivity(intent);
            }
        });
    }

    // la récupération des informations d'enseignant
    public void getEnseignant(int id, final String token) {
        final Intent intent = new Intent(this, Login.class);
        String url = "http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/";
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<Enseignant> call = api1.getEnsignant(id, "Bearer " + token);
        call.enqueue(new Callback<Enseignant>() {
            @Override
            public void onResponse(@NonNull Call<Enseignant> call, @NonNull Response<Enseignant> response) {
                if (response.isSuccessful()) {
                    Enseignant enseignant = response.body();
                    GoToEnseignant(s, enseignant, token);
                } else {
                    wait.setVisibility(View.GONE);
                    intent.putExtra("message", getResources().getString(R.string.erreur_not_succ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Enseignant> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                intent.putExtra("message", getResources().getString(R.string.network));
                startActivity(intent);
            }
        });
    }

    // la récupération des informations d'etudiant
    public void getEtudiant(int id, final String token) {
        final Intent intent = new Intent(this, Login.class);
        String url = "http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/";
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<Etudiant> call = api1.getEtudiant(id, "Bearer " + token);
        call.enqueue(new Callback<Etudiant>() {
            @Override
            public void onResponse(@NonNull Call<Etudiant> call, @NonNull Response<Etudiant> response) {
                if (response.isSuccessful()) {
                    Etudiant etudiant = response.body();
                    assert etudiant != null;
                    etudiant.setUser(user.getText().toString());
                    etudiant.setPass(pass.getText().toString());
                    // teste si l'etudiant a un congé academique
                    if (etudiant.getEtat_etudiant().toString().equals("actif")) {
                        GoToEtudiant(s, etudiant, token);
                    } else {
                        intent.putExtra("message", getResources().getString(R.string.conge_existe));
                        startActivity(intent);
                    }
                } else {
                    wait.setVisibility(View.GONE);
                    intent.putExtra("message", getResources().getString(R.string.erreur_not_succ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Etudiant> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                intent.putExtra("message", getResources().getString(R.string.network));
                startActivity(intent);
            }
        });
    }

    // méthode de redirection vers l'espace enseignant
    public void GoToEnseignant(int s, Enseignant enseignant, String token) {
        //on envoie les informations vers l'espace enseignant dans une 'Intent'
        Intent intent = new Intent(this, FragmentMainActivityTeacher.class);
        intent.putExtra("enseignant", enseignant);
        intent.putExtra("token", token);
        startActivity(intent);
        wait.setVisibility(View.GONE);
        loginPreferenceConfig.setLoginStatusEnseignant(s, enseignant, token);
    }

    // méthode de redirection vers l'espace etudiant
    public void GoToEtudiant(int s, Etudiant etudiant, String token) {
        //on envoie les informations vers l'espace etudiant dans une 'Intent'
        Intent intent = new Intent(this, EtudiantFragmentMainActivity.class);
        intent.putExtra("etudiant", etudiant);
        intent.putExtra("token", token);
        startActivity(intent);
        wait.setVisibility(View.GONE);
        loginPreferenceConfig.setLoginStatusEtudiant(s, etudiant, token);
    }

    // la méthode de récupération de mot de passe
    public void forgotPassword() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    private void goToModeOfline() {
        Intent intent = new Intent(this, MainHorsLigne.class);
        startActivity(intent);
    }

    public static boolean isRTL(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.getResources().getConfiguration().getLayoutDirection()
                    == View.LAYOUT_DIRECTION_RTL;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    public void getOpenWebIntent(String redirection) {
        Intent intent;
        switch (redirection) {
            case "Facebook":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/faculteNTICUC2"));
                break;
            case "GitHub":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AymenOuaden"));
                break;
            case "LinkedIn":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/university-abdelhamid-mehri-constantine-2/"));
                break;
            default:
                intent = new Intent(this, Login.class);
                break;
        }
        startActivity(intent);
    }

}
