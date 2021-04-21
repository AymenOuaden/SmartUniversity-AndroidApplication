package com.e.application.Control.Etudiant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.application.API.API;
import com.e.application.Dots.Dot_Create_QR;
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Etudiant;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;

public class EtudiantActivityTakePresence extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    byte[] imagebyte;
    private String texte_qr_scan;
    private Gson gson = new Gson();
    LoginPreferencesConfig loginPreferencesConfig;
    Dot_Create_QR dot_create_qr_chek = null;
    ArrayList<String> codes_seances = new ArrayList<>();
    Etudiant etudiant;
    String message = "", etat = "";
    int boucle1 = 0, indice1 = 0;

    public EtudiantActivityTakePresence() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPreferencesConfig = new LoginPreferencesConfig(getApplicationContext());

        String json_etudiant = loginPreferencesConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        String json_seance_responses = loginPreferencesConfig.getSeanceResponseEtudiant();
        ArrayList<SeanceResponse> seanceResponses = gson.fromJson(
                json_seance_responses,
                new TypeToken<ArrayList<SeanceResponse>>() {
                }.getType()
        );
        codes_seances = new ArrayList<>();
        for (SeanceResponse seanceResponse : seanceResponses) {
            codes_seances.add(seanceResponse.getSeance().getCode_seance());
        }

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }


    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA},
                                                    REQUEST_CAMERA);
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EtudiantActivityTakePresence.this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // résultat de scannage
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void handleResult(Result result) {
        texte_qr_scan = result.getText();
        imagebyte = result.getRawBytes();
        getCodeQR();
    }

    // récupération du code QR from base de données
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getCodeQR() {

        indice1 = 0;
        while (boucle1 == 0 && indice1 <= codes_seances.size() - 1) {
            indice1++;

            String code_seance = codes_seances.get(indice1 - 1);
            Retrofit retrofit1 = new Retrofit.Builder()
                    .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final API api1 = retrofit1.create(API.class);
            Call<Dot_Create_QR> call = api1.GetCodeQR(code_seance, "Bearer " + loginPreferencesConfig.getToken());
            call.enqueue(new Callback<Dot_Create_QR>() {
                @Override
                public void onResponse(@NonNull Call<Dot_Create_QR> call, @NonNull Response<Dot_Create_QR> response) {
                    if (response.isSuccessful()) {
                        dot_create_qr_chek = response.body();
                        if (dot_create_qr_chek != null) {
                            boucle1 = 1;
                            chekCode(dot_create_qr_chek);
                        } else {
                            if (indice1 >= codes_seances.size()) {
                                message = getResources().getString(R.string.pas_de_seances);
                                etat = "not_ok";
                                goToEspaceEtudiant();
                            }
                        }

                    } else {
                        if (indice1 >= codes_seances.size()) {
                            message = getResources().getString(R.string.pas_de_seances);
                            etat = "not_ok";
                            goToEspaceEtudiant();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Dot_Create_QR> call, @NonNull Throwable t) {
                    message = getResources().getString(R.string.network);
                    etat = "not_ok";
                    goToEspaceEtudiant();
                }
            });
        }


    }

    //vérification de code
    private void chekCode(Dot_Create_QR dot_create_qr) {
        if (dot_create_qr == null) {
            message = getResources().getString(R.string.pas_de_seances);
            etat = "not_ok";
            goToEspaceEtudiant();
        } else {
            if (dot_create_qr.getTexte_qr().equals(texte_qr_scan)) {
                getAbsencesOfEtudiant(dot_create_qr_chek);
            } else {
                message = getResources().getString(R.string.qr_faux);
                etat = "not_ok";
                goToEspaceEtudiant();
            }
        }
    }

    /* la récupération des absences depuis la base de données*/
    void getAbsencesOfEtudiant(final Dot_Create_QR dot_create_qr_chek) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<AbsenceDepartementResponse>> call = api1.GetAbsencesOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + loginPreferencesConfig.getToken());
        call.enqueue(new Callback<ArrayList<AbsenceDepartementResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Response<ArrayList<AbsenceDepartementResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<AbsenceDepartementResponse> absenceDepartementResponses = response.body();
                    if (absenceDepartementResponses != null) {
                        deleteAbsence(absenceDepartementResponses, dot_create_qr_chek);
                    } else {
                        message = getResources().getString(R.string.pas_abs);
                        etat = "not_ok";
                        goToEspaceEtudiant();
                    }
                } else {
                    message = getResources().getString(R.string.pas_abs);
                    etat = "not_ok";
                    goToEspaceEtudiant();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<AbsenceDepartementResponse>> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                goToEspaceEtudiant();
            }
        });
    }

    private void deleteAbsence(ArrayList<AbsenceDepartementResponse> absenceDepartementResponses, Dot_Create_QR dot_create_qr_chek) {
        int numero_absence;
        int i = 0;
        for (AbsenceDepartementResponse absenceDepartementResponse : absenceDepartementResponses) {
            i++;
            if (absenceDepartementResponse.getAbsence().getCode_seance().equals(dot_create_qr_chek.getCode_seance())) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                month = month + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String date;
                if (month < 10) {
                    if (day < 10) {
                        date = year + "-0" + month + "-" + "0" + day;
                    } else {
                        date = year + "-0" + month + "-" + day;
                    }
                } else {
                    if (day < 10) {
                        date = year + "-" + month + "-" + "0" + day;
                    } else {
                        date = year + "-" + month + "-" + day;
                    }
                }
                if (absenceDepartementResponse.getAbsence().getDate_absence().equals(date)) {
                    numero_absence = absenceDepartementResponse.getAbsence().getNumero_absence();
                    supprimerAbsence(numero_absence);
                } else if (!absenceDepartementResponse.getAbsence().getDate_absence().equals(date) && i >= absenceDepartementResponses.size() - 1) {
                    message = getResources().getString(R.string.pas_abs);
                    etat = "not_ok";
                    goToEspaceEtudiant();
                }
            }
        }
    }

    public void supprimerAbsence(int numero_absence) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.DeleteAbsenceByNumero(numero_absence, "Bearer " + loginPreferencesConfig.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    message = getResources().getString(R.string.supprimer_absence_succ);
                    etat = "ok";
                    goToEspaceEtudiant();
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "not_ok";
                    goToEspaceEtudiant();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                goToEspaceEtudiant();
            }
        });
    }

    // redirection vers la page espace etudiant
    private void goToEspaceEtudiant() {
        Intent i = new Intent(this, EtudiantFragmentMainActivity.class);
        i.putExtra("message", message);
        i.putExtra("etat", etat);
        startActivity(i);
    }
}


