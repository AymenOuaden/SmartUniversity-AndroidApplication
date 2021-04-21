package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentMarquerPresence;
import com.e.application.Control.OfflineMode.AbsenceSQL;
import com.e.application.Control.OfflineMode.MainHorsLigne;
import com.e.application.Control.OfflineMode.SQLiteHelper;
import com.e.application.Dots.Dot_Create_Absence;
import com.e.application.Dots.Dot_Create_QR;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentTakePresences extends Fragment implements Adapter_FragmentMarquerPresence.ItemClickListener, Adapter_FragmentConsulterSeance.ItemClickListener {
    // les attributs
    View view;
    private Bitmap bitmap;
    private Dot_Create_QR dot_create_qr;
    private String token, etat = "", message = "", texte_qr, jour_affiche = "";
    Enseignant enseignant;
    private ArrayList<Etudiant> liste_etudiants = new ArrayList<>();
    private ArrayList<Integer> liste_absences = new ArrayList<>();
    private Seance seance;
    private TextView mDisplayDate = null, msg, msg2;
    private Button valider;
    private RelativeLayout option_qr;
    private EditText date;
    private ArrayList<String> dates_absences;
    private ArrayList<Seance> seances;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private ProgressDialog mProgressDialog;
    private String s_texte_qr = "";

    public FragmentTakePresences() {
    }

    @SuppressLint({"NewApi", "SetTextI18n", "CutPasteId"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        token = loginPreferenceConfig.getToken();
        //liste des séances
        String json_seances = loginPreferenceConfig.getSeances();
        seances = gson.fromJson(json_seances, new TypeToken<ArrayList<Seance>>() {
        }.getType());
        // récupération des attributs
        Bundle bundle = getArguments();
        assert bundle != null;
        seance = (Seance) bundle.getSerializable("seance");
        // la liste des séances est récupéré dans l'attribut seances
        String affichage = (String) bundle.getSerializable("affichage");
        assert affichage != null;
        if (affichage.equals("consulter_liste_seances")) {
            // le choix d'affichage de toutes les séances
            view = inflater.inflate(R.layout.fragment_consulter_seances, container, false);
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.consulter_seance);
            TextView title = view.findViewById(R.id.title);
            title.setText(getResources().getString(R.string.title_consulter_marquer));
            // on affiche les séances à l'aide d'un adapter qui gère les séances dans un recylerviwe
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentConsulterSeance adapter = new Adapter_FragmentConsulterSeance(getContext(), seances);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            // le choix d'une séance , ce choix est fait grace à la méthode "gerer"
            view = inflater.inflate(R.layout.fragment_take_presences, container, false);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(getResources().getString(R.string.marquer_presence));
            TextView code_module = view.findViewById(R.id.code_module);
            code_module.setText(seance.getCode_module());
            TextView groupe = view.findViewById(R.id.groupe);
            groupe.setText(String.valueOf(seance.getGroupe()));
            TextView date_d = view.findViewById(R.id.date_d);
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
            date_d.setText(jour_affiche + "  " + seance.getHeure());
            final RelativeLayout manuel = view.findViewById(R.id.manuel);
            final RelativeLayout auto = view.findViewById(R.id.auto);
            option_qr = view.findViewById(R.id.options_qr);
            Button supprimer = view.findViewById(R.id.supprimer_code_qr);
            valider = view.findViewById(R.id.save);
            Button changer = view.findViewById(R.id.changer_code_qr);

            msg = view.findViewById(R.id.message);
            msg2 = view.findViewById(R.id.message2);
            date = view.findViewById(R.id.date);


            // implementation des bouttons
            // marquer présence
            final Button button_manuel = view.findViewById(R.id.button_manuel);
            button_manuel.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    FragmentTakePresences fragmentTakePresences = new FragmentTakePresences();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("token", token);
                    bundle.putSerializable("enseignant", enseignant);
                    bundle.putSerializable("seances", seances);
                    bundle.putSerializable("seance", seance);
                    bundle.putSerializable("affichage", "");
                    fragmentTakePresences.setArguments(bundle);
                    @SuppressLint({"NewApi", "LocalSuppress"})
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentTakePresences);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            // redrection vers l'affichage de code qr
            final Button generate_qr = view.findViewById(R.id.button_qr);
            generate_qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msg.setVisibility(View.GONE);
                    msg2.setVisibility(View.GONE);
                    generate_qr.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    generate_qr.setTextColor(getResources().getColor(R.color.white));
                    button_manuel.setBackground(getResources().getDrawable(R.drawable.button_empty_black_round));
                    button_manuel.setTextColor(getResources().getColor(R.color.black));
                    manuel.setVisibility(View.GONE);
                    auto.setVisibility(View.VISIBLE);
                    valider.setVisibility(View.GONE);
                    option_qr.setVisibility(View.VISIBLE);
                    if (liste_etudiants.size() < 1) {
                        TextView title = view.findViewById(R.id.title_qr);
                        title.setText(getResources().getString(R.string.message_error_no_student_found));
                        option_qr.setVisibility(View.GONE);
                    }
                    getCodeQR();
                    // teste s'il y a un code QR , il faut qu'un seul code exist dans la bd
                    if (dot_create_qr != null && !s_texte_qr.equals("")) {
                        // affichage d'ancienne code QR
                        generateQR(s_texte_qr);
                    } else {
                        // si y a aucun code
                        generateQR("");
                        if (bitmap != null) {
                            if (liste_etudiants.size() >= 1) {
                                createCodeQR(bitmap, "ajouter_absences");
                            }
                        }
                    }
                }
            });

            // importation de liste d'absences depuis une base de données local SQLlite
            Button importer_liste = view.findViewById(R.id.importer_liste);
            importer_liste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.searching_data),
                            getResources().getString(R.string.please_wait), false, false);

                    dates_absences = new ArrayList<>();
                    checkListeImporter();
                    if (liste_absences.size() >= 1) {
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            public void run() {
                                mProgressDialog.dismiss();
                                createAbsences("SQL");
                            }
                        }, 1500);
                    } else {
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            public void run() {
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), getResources().getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
                            }
                        }, 1500);
                    }
                }
            });
            // affichage des séances
            final Button afficher_seances = view.findViewById(R.id.afficher_seances);
            afficher_seances.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_black_round));
                    afficher_seances.setTextColor(getResources().getColor(R.color.white));
                    afficher_seances();
                }
            });
            // suppression du code qr
            supprimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCodeQR("supprimer");
                }
            });
            // changement de code qr , ( supprimer puis créer un autre)
            changer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCodeQR("changer");
                }
            });

            // le bouton "valider" appel la méthode qui stock les absences dans la base de données
            valider.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    valider.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                    valider.setTextColor(getResources().getColor(R.color.white));
                    /* teste s'il y a des étudiants absents.
                       l'ajout et la suppression des étudiants dans "liste_absences" se font
                       avec les méthodes "chek" et "inchek" */
                    if (liste_absences.size() >= 1) {
                        // le cas ou la liste des étudiants absents contient au minimum un étudiant
                        // teste de la date s'elle  est différente de null
                        if (!date.getText().toString().equals(getResources().getString(R.string.date_seance))) {
                            //  on appel la méthode qui crée l'absence
                            createAbsences("manuel");
                        } else {
                            // le cas ou la date est null , on affiche un message d'erreur
                            valider.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
                            msg.setVisibility(View.VISIBLE);
                            msg.setText(getResources().getString(R.string.message_error_date));
                            msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                            valider.setBackground(getResources().getDrawable(R.drawable.button_empty_color_accent_round));
                            valider.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    } else {
                        /* le cas ou la liste des étudiants absents est vide,
                         on affiche un message d'erreur */
                        valider.setBackground(getResources().getDrawable(R.drawable.button_full_green_round));
                        msg.setVisibility(View.VISIBLE);
                        msg.setText(getResources().getString(R.string.message_error_empty_liste));
                        msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                        valider.setBackground(getResources().getDrawable(R.drawable.button_empty_color_accent_round));
                        valider.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
            });

            // picker date
            mDisplayDate = view.findViewById(R.id.date);
            mDisplayDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialog = new DatePickerDialog(
                            FragmentTakePresences.this.getActivity(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year, month, day);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
            final TextView finalMDisplayDate = mDisplayDate;
            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    String date = year + "-" + month + "-" + day;
                    finalMDisplayDate.setText(date);
                }
            };

            // appel la méthode de récupération des étudiants
            getListeEtudiants(seance);
        }
        return view;
    }

    // la méthode qui fait la sélectionne d'une séance
    @Override
    public void gerer(View view, int position) {
        seance = seances.get(position);
        FragmentTakePresences fragmentTakePresences = new FragmentTakePresences();
        Bundle bundle = new Bundle();
        bundle.putSerializable("token", token);
        bundle.putSerializable("enseignant", enseignant);
        bundle.putSerializable("seances", seances);
        bundle.putSerializable("seance", seance);
        bundle.putSerializable("affichage", "");
        fragmentTakePresences.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentTakePresences);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //--------------------------------Manuelle------------------------------------------------------

    //recupération de la liste des etudiants d'une séance
    private void getListeEtudiants(Seance seance) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ArrayList<Etudiant>> call = api.getEtudiant(seance.getAnnee(),
                seance.getSpecialite(),
                seance.getGroupe(),
                "Bearer " + token);
        call.enqueue(new Callback<ArrayList<Etudiant>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Etudiant>> call, @NonNull Response<ArrayList<Etudiant>> response) {
                if (response.isSuccessful()) {
                    liste_etudiants = new ArrayList<>();
                    liste_etudiants = response.body();
                    assert liste_etudiants != null;
                    setView(liste_etudiants);
                } else {
                    msg2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Etudiant>> call, @NonNull Throwable t) {
                msg.setVisibility(View.VISIBLE);
                msg.setText(getResources().getString(R.string.network));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    //afficher la liste des étudiants
    public void setView(ArrayList<Etudiant> liste_etudiants) {
        if (liste_etudiants.size() == 0) {
            // cas ou la s'éance ne correspond à aucun étudiant
            msg2.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Adapter_FragmentMarquerPresence adapter = new Adapter_FragmentMarquerPresence(this, liste_etudiants, liste_absences);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    // la méthode est appelée automatiquement lorsqu'on sélectionne un étudiant
    @Override
    public void Check(View view, int id) {
        // l'etudiants sélectionné est ajouté à la liste des absences
        liste_absences.add(id);
    }

    // la méthode est appelée automatiquement lorsqu'on désélectionne un étudiant
    @Override
    public void inCheck(View view, int id) {
        // l'etudiants désélectionné est supprimé de la liste des absences
        liste_absences.remove((Integer) id);
    }

    // la méthode appel l'API pour stocker les absences
    private void createAbsences(final String operation) {
        String date;
        if (operation.equals("manuel")) {
            mDisplayDate = view.findViewById(R.id.date);
            date = mDisplayDate.getText().toString();
        } else {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            month = month + 1;
            date = year + "-" + month + "-" + day;
        }
        // on appel l'api pour chaque étudiant
        int i = 0;
        for (int id : liste_absences) {
            final Dot_Create_Absence absence;
            if (operation.equals("SQL")) {
                absence = new Dot_Create_Absence(seance.getCode_seance(), id, dates_absences.get(i));
                i++;
            } else {
                absence = new Dot_Create_Absence(seance.getCode_seance(), id, date);
            }
            Retrofit retrofit1 = new Retrofit.Builder()
                    .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/create/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final API api1 = retrofit1.create(API.class);
            Call<ResponseBody> call = api1.createAbsence(absence, "Bearer " + token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (operation.equals("manuel") || operation.equals("SQL")) {
                            if (operation.equals("SQL")) {
                                MainHorsLigne.sqLiteHelper.deleteData(new AbsenceSQL(absence.getId_etudiant(), 0,
                                        seance.getJour().toString(), seance.getHeure(), absence.getDate_absence()));
                            }
                            message = "-" + getResources().getString(R.string.marquer_presence_ok);
                            message = message + "\n" + seance.getType() + " " + seance.getCode_module() + "\n" + jour_affiche + " " + seance.getHeure();
                            etat = "ok";
                            goToEspaceEnseignant(message, etat);
                        }
                    } else {
                        message = message + "- " + getResources().getString(R.string.marquer_presence_not_ok);
                        message = message + "\n" + seance.getType() + " " + seance.getCode_module() + "\n" + jour_affiche + " " + seance.getHeure();
                        etat = "not_ok";
                        goToEspaceEnseignant(message, etat);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    message = message + "- " + getResources().getString(R.string.marquer_presence_not_ok);
                    message = message + "\n" + getResources().getString(R.string.network);
                    message = message + "\n" + seance.getType() + " " + seance.getCode_module() + "\n" + seance.getJour() + " " + seance.getHeure();
                    etat = "not ok";
                    goToEspaceEnseignant(message, etat);
                }
            });
        }
    }

    //--------------------------------QR CODE------------------------------------------------------
    // récupération de code depuis api
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getCodeQR() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<Dot_Create_QR> call = api1.GetCodeQR(seance.getCode_seance(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<Dot_Create_QR>() {
            @Override
            public void onResponse(@NonNull Call<Dot_Create_QR> call, @NonNull Response<Dot_Create_QR> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        dot_create_qr = response.body();
                        s_texte_qr = response.body().getTexte_qr();
                    } else {
                        dot_create_qr = null;
                    }
                } else {
                    dot_create_qr = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Dot_Create_QR> call, @NonNull Throwable t) {
                msg.setVisibility(View.VISIBLE);
                msg.setText(getResources().getString(R.string.network));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    // générer le code qr
    private void generateQR(String text) {
        if (text.equals("")) {
            texte_qr = String.valueOf(System.currentTimeMillis());
        } else {
            texte_qr = text;
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(texte_qr, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ((ImageView) view.findViewById(R.id.code_qr)).setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    // stocker le code qr dans la base de données
    private void createCodeQR(Bitmap bmp, final String operation) {
        // get array byte from bitmap ( code qr)
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        // stock le code dans la base de données
        Dot_Create_QR dot_create_qr = new Dot_Create_QR(enseignant.getId_utilisateur(), seance.getCode_seance(), byteArray, texte_qr);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/upload/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.CreateCodeQR(dot_create_qr, "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (operation.equals("ajouter_absences")) {
                        // liste_absences.add()
                        for (Etudiant etudiant : liste_etudiants) {
                            liste_absences.add(etudiant.getId_utilisateur());
                        }
                        createAbsences("qr");
                    }
                } else {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText(getResources().getString(R.string.erreur_not_succ));
                    msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                msg.setVisibility(View.VISIBLE);
                msg.setText(getResources().getString(R.string.network));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    private void deleteCodeQR(final String operation) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ResponseBody> call = api1.DeleteCodeQR(seance.getCode_seance(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (operation.equals("changer")) {
                        generateQR("");
                        if (bitmap != null) {
                            createCodeQR(bitmap, "changer");
                        }
                    } else {
                        message = "-" + getResources().getString(R.string.marquer_presence_ok);
                        message = message + "\n" + seance.getType() + " " + seance.getCode_module() + "\n" + seance.getJour() + " " + seance.getHeure();
                        etat = "ok";
                        goToEspaceEnseignant(message, etat);
                    }
                } else {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText(getResources().getString(R.string.erreur_not_succ));
                    msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                msg.setVisibility(View.VISIBLE);
                msg.setText(getResources().getString(R.string.network));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
        });
    }

    //--------------------------------Importer liste SQLlite----------------------------------------
    // vérification s'il y a des absences
    private void checkListeImporter() {

        MainHorsLigne.sqLiteHelper = new SQLiteHelper(getActivity(), "liste.sqlite", null, 1);
        MainHorsLigne.sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ABSENCE(Id_etudiant INTEGER PRIMARY KEY ,id_enseignant INTEGER," +
                " date_absence VARCHAR(8), jour_seance VARCHAR(30), heure_seance VARCHAR(30))");
        Cursor cursor = MainHorsLigne.sqLiteHelper.getData("SELECT * FROM ABSENCE");
        while (cursor.moveToNext()) {
            int id_etudiant = cursor.getInt(0);
            int id_enseignant = cursor.getInt(1);
            String jour_seance = cursor.getString(3);
            String heure_seance = cursor.getString(4);
            String date_absence = cursor.getString(2);
            if (id_enseignant == enseignant.getId_utilisateur() && jour_seance.equals(seance.getJour().toString())
                    && heure_seance.equals(seance.getHeure())) {
                for (Etudiant etudiant : liste_etudiants) {
                    if (id_etudiant == etudiant.getId_utilisateur()) {
                        liste_absences.add(id_etudiant);
                        dates_absences.add(date_absence);
                    }
                }
            }
        }
    }

    // redirection vers la page espace enseignant
    private void goToEspaceEnseignant(String message, String etat) {
        Intent i = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        i.putExtra("message", message);
        i.putExtra("etat", etat);
        startActivity(i);
    }

    /* l'affichage de toutes les séances (appel la page actuelle avec
    l'attribut affichage 'consulter_liste_seances') */
    private void afficher_seances() {
        FragmentTakePresences fragmentTakePresences = new FragmentTakePresences();
        Bundle bundle = new Bundle();
        bundle.putSerializable("token", token);
        bundle.putSerializable("enseignant", enseignant);
        bundle.putSerializable("seances", seances);
        bundle.putSerializable("affichage", "consulter_liste_seances");
        fragmentTakePresences.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentTakePresences);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
