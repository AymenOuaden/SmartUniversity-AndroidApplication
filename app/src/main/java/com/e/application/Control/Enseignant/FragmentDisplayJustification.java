package com.e.application.Control.Enseignant;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.API.API;
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentDisplayImages;
import com.e.application.Helpers.AbsenceResponse;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Justification;
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

public class FragmentDisplayJustification extends Fragment implements Adapter_FragmentDisplayImages.ItemClickListener {

    private static final int REQUEST_PERMISSION = 1;
    Enseignant enseignant;
    private Seance seance;
    String token;
    private ImageView image;
    View view;
    private TextView name, date_absence, message;
    private Etudiant etudiant;
    private Absence absence;
    private Button releve;
    private CardView cardView;
    private ArrayList<EtudiantResponse> liste_etudiants_absences;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_image, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.display_justification);

        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        // récupération d'enseignant et token depuis sahredPrefrences
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        String json_absence = loginPreferenceConfig.getAbsence();
        absence = gson.fromJson(json_absence, Absence.class);
        String json_seance = loginPreferenceConfig.getSeance();
        seance = gson.fromJson(json_seance, Seance.class);
        // recupération depuis layout
        cardView = view.findViewById(R.id.cardview);
        // texte
        name = view.findViewById(R.id.etudiant);
        name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        date_absence = view.findViewById(R.id.date_absence);
        date_absence.setText(absence.getDate_absence());
        message = view.findViewById(R.id.message);
        // image
        image = view.findViewById(R.id.image);
        // boutons
        final Button exclus = view.findViewById(R.id.exclus);
        releve = view.findViewById(R.id.releve);
        final Button afficher_seances = view.findViewById(R.id.afficher_seances);

        // implémentation des boutons
        exclus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "NewApi"})
            @Override
            public void onClick(View v) {
                exclus.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentExcludedStudents");
            }
        });

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

        afficher_seances.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi"})
            @Override
            public void onClick(View v) {
                afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentManageAbsences");
            }
        });

        ArrayList<Justification> justifications = new ArrayList<>();

        String json_liste_etudiants_absences = loginPreferenceConfig.getListeEtudiantsEtLeursAbsences();
        liste_etudiants_absences = gson.fromJson(
                json_liste_etudiants_absences,
                new TypeToken<ArrayList<EtudiantResponse>>() {
                }.getType()
        );
        if (liste_etudiants_absences != null) {

            for (EtudiantResponse etudiantResponse : liste_etudiants_absences) {
                if (etudiantResponse.getAbsences() != null) {
                    for (AbsenceResponse absenceResponse : etudiantResponse.getAbsences()) {
                        if (absenceResponse.getAbsence().getNumero_absence() == absence.getNumero_absence()) {
                            justifications = absenceResponse.getJustification();
                        }
                    }
                }
            }
        }
        if (justifications != null) {
            setViewApresUpload(justifications);
        } else {
            TextView justifications_vide = view.findViewById(R.id.vide);
            justifications_vide.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public FragmentDisplayJustification() {
    }

    private void setViewApresUpload(ArrayList<Justification> justification) {
        if (justification.size() >= 1) {
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentDisplayImages adapter = new Adapter_FragmentDisplayImages(this, justification);
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
                && ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                cardView.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.justification_delete_succ));
                message.setBackground(getResources().getDrawable(R.drawable.background_green));
                message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
                setNewListeEtudiantsAbsences();
            }

            @SuppressLint("NewApi")
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.justification_delete_fail));
                message.setBackground(getResources().getDrawable(R.drawable.background_red_light));
                message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    // la méthode affiche la justification
    @SuppressLint("SetTextI18n")
    void setView(Justification justification) {
        // convert image(arrayliste de bytes) depuis justification vers un Bitmap pour la afficher
        Bitmap bitmap = BitmapFactory.decodeByteArray(justification.getFichier(), 0, justification.getFichier().length);
        image.setImageBitmap(bitmap);
        // définir les informations qui concernent la justification
        name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        date_absence.setText(absence.getDate_absence());

    }

    // la méthode récupère les nouvelles données aprés la suppression de la justification
    private void setNewListeEtudiantsAbsences() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ArrayList<SeanceResponse>> call = api.GetSeancesCompleteEnseignant(
                enseignant.getId_utilisateur(),
                "Bearer " + token
        );
        call.enqueue(new Callback<ArrayList<SeanceResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Response<ArrayList<SeanceResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<SeanceResponse> seanceResponses;
                    seanceResponses = response.body();
                    assert seanceResponses != null;
                    for (SeanceResponse r : seanceResponses) {
                        if (r.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                            liste_etudiants_absences = r.getEtudiants();
                        }
                    }
                    loginPreferenceConfig.setListeEtudiantsEtLeursAbsences(liste_etudiants_absences);
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.erreur_not_succ));
                    message.setBackground(getResources().getDrawable(R.drawable.background_red_light));
                    message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SeanceResponse>> call, @NonNull Throwable t) {
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.network));
                message.setBackground(getResources().getDrawable(R.drawable.background_red_light));
                message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    // redirection vers d'autres fragments
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void goToOptionDeGestion(String redirection) {
        Fragment fragment;
        switch (redirection) {
            case "FragmentExcludedStudents":
                fragment = new FragmentExcludedStudents();
                break;
            case "FragmentReleveAbsences":
                fragment = new FragmentReleveAbsences();
                break;
            case "FragmentDisplayJustification":
                fragment = new FragmentDisplayJustification();
                break;
            case "FragmentManageAbsences":
                fragment = new FragmentManageAbsences();
                break;
            default:
                fragment = new FragmentTeacherSpace();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", "");
        bundle.putSerializable("etat", "");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}