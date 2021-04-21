package com.e.application.Control.Etudiant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.API.API;
import com.e.application.Adapters.AdapterEtudiant.EtudiantAdapter_FragmentDisplayJustifications;
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Justification;
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

public class EtudiantFragmentDisplayJustification extends Fragment implements EtudiantAdapter_FragmentDisplayJustifications.ItemClickListener {

    private static final int REQUEST_PERMISSION = 1;
    private View view;
    private Etudiant etudiant;
    private Button releve;
    private RelativeLayout wait;
    private LoginPreferencesConfig loginPreferenceConfig;
    private Gson gson = new Gson();
    private String message_afficher = "", etat_message = "", token;

    public EtudiantFragmentDisplayJustification() {
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.etudiant_fragment_display_justification, container, false);

        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        // récupération d'enseignant et token depuis sahredPrefrences

        token = loginPreferenceConfig.getToken();
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        String json_absence = loginPreferenceConfig.getAbsence();
        Absence absence = gson.fromJson(json_absence, Absence.class);
        // texte
        TextView name = view.findViewById(R.id.etudiant);
        name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        TextView date_absence = view.findViewById(R.id.date_absence);
        date_absence.setText(absence.getDate_absence());
        TextView message = view.findViewById(R.id.message);

        Bundle bundle = getArguments();
        if (bundle != null) {
            message_afficher = (String) bundle.getSerializable("message");
            etat_message = (String) bundle.getSerializable("etat");
            if (!message_afficher.equals("")) {
                message.setVisibility(View.VISIBLE);
                message.setText(message_afficher);
                if (etat_message.equals("ok")) {
                    message.setBackgroundColor(getResources().getColor(R.color.light_back));
                    message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
                } else {
                    message.setBackgroundColor(getResources().getColor(R.color.red_light));
                    message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }
        }
        // boutons
        releve = view.findViewById(R.id.releve);
        // image
        wait = view.findViewById(R.id.wait);
        // implémentation des boutons
        releve.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                releve.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });

        String json_liste_etudiants_absences = loginPreferenceConfig.getAbsenceDepartementResponses();
        ArrayList<AbsenceDepartementResponse> liste_etudiants_absences = gson.fromJson(
                json_liste_etudiants_absences,
                new TypeToken<ArrayList<AbsenceDepartementResponse>>() {
                }.getType()
        );
        ArrayList<Justification> justifications = new ArrayList<>();
        if (liste_etudiants_absences != null) {
            for (AbsenceDepartementResponse absenceResponse : liste_etudiants_absences) {
                if (absenceResponse.getAbsence().getNumero_absence() == absence.getNumero_absence()) {
                    justifications = absenceResponse.getJustifications();
                }
            }
        }

        setView(justifications);

        return view;
    }

    void setView(ArrayList<Justification> justification) {
        if (justification.size() >= 1) {
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            EtudiantAdapter_FragmentDisplayJustifications adapter = new EtudiantAdapter_FragmentDisplayJustifications(this, justification);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void downloadJustification(byte[] image) {
        String save;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        }
        Toast.makeText(getActivity(), getResources().getString(R.string.start_downloading),
                Toast.LENGTH_SHORT).show();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        save = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getActivity()).getContentResolver(), bitmap, "", "");
        if (!save.equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.completed_download),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.cant_download),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteJustification(int numero_absence, int numero_justification) {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.DeleteJustification(numero_absence, numero_justification, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                message_afficher = getResources().getString(R.string.justification_delete_succ);
                etat_message = "ok";
                getAbsencesOfEtudiant();
            }

            @SuppressLint("NewApi")
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message_afficher = getResources().getString(R.string.justification_delete_fail);
                etat_message = "not ok";
                wait.setVisibility(View.GONE);
                goToOptionDeGestion("FragmentDisplayJustification");
            }
        });
    }

    /* la récupération des absences depuis la base de données */
    private void getAbsencesOfEtudiant() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<AbsenceDepartementResponse>> call = api1.GetAbsencesOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<AbsenceDepartementResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Response<ArrayList<AbsenceDepartementResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<AbsenceDepartementResponse> absenceDepartementResponses = response.body();
                    loginPreferenceConfig.setAbsenceDepartementResponses(absenceDepartementResponses);
                    wait.setVisibility(View.GONE);
                    goToOptionDeGestion("FragmentDisplayJustifications");
                } else {
                    wait.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
            }
        });

    }

    // redirection vers d'autres fragments
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(String redirection) {
        Fragment fragment;
        switch (redirection) {
            case "FragmentReleveAbsences":
                fragment = new EtudiantFragmentReleveAbsences();
                break;
            case "FragmentDisplayJustifications":
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("message", message_afficher);
                bundle1.putSerializable("etat", etat_message);
                fragment = new EtudiantFragmentDisplayJustification();
                fragment.setArguments(bundle1);
                break;
            default:
                fragment = new EtudiantFragmentStudentSpace();
                break;
        }
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}