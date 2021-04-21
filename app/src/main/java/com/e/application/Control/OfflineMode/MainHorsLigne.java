package com.e.application.Control.OfflineMode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Login;
import com.e.application.Model.Seance;
import com.e.application.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MainHorsLigne extends AppCompatActivity implements Adapter_Ofline_take_presence.ItemClickListener {

    int id_enseignant, id_etudiant;
    String date_absence, heure_seance, jour_seance;
    EditText enseignant, etudiant;
    Button next, finish;
    ImageView ajouter;
    RelativeLayout layout1, layout2;
    ArrayList<AbsenceSQL> list;
    TextView message, title1;
    public static SQLiteHelper sqLiteHelper;

    public MainHorsLigne() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_marquer_absence);

        // date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        date_absence = year + "-" + month + "-" + day;
        //
        layout1 = findViewById(R.id.layout_1);
        layout2 = findViewById(R.id.layout_2);
        title1 = findViewById(R.id.title1);
        message = findViewById(R.id.message);

        // le premier affichage
        next = findViewById(R.id.next);
        // récupération de spinner depuis layout
           /* le "spinner" qui nous donne la possibilité de choisir un élément depuis plusieurs
            éléments, dans notre cas on choisit les jours et les heures de la séance */
        final Spinner dropdown_jour = findViewById(R.id.jour_seance);
        final Spinner dropdown_heure = findViewById(R.id.heure_seance);
        //creation les listes des élémnets des spinners.
        String[] items_jour = new String[]{String.valueOf(Seance.Jour.dimanche), String.valueOf(Seance.Jour.lundi),
                String.valueOf(Seance.Jour.mardi), String.valueOf(Seance.Jour.mercredi),
                String.valueOf(Seance.Jour.jeudi)};
        String[] items_heure = new String[]{"8:30", "10:00", "11:30", "13:00", "14:30"};
            /*création des adaptateurs pour décrire comment les éléments sont affichés, les adaptateurs
             sont utilisés à plusieurs endroits dans Android. */
        final ArrayAdapter<String> adapter_jour = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_jour);
        final ArrayAdapter<String> adapter_heure = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_heure);
        dropdown_jour.setAdapter(adapter_jour);
        dropdown_heure.setAdapter(adapter_heure);
        // passage vers le 2eme affichage
        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                message.setVisibility(View.GONE);
                Spinner spinner_jour = findViewById(R.id.jour_seance);
                jour_seance = spinner_jour.getSelectedItem().toString();
                Spinner spinner_heure = findViewById(R.id.heure_seance);
                heure_seance = spinner_heure.getSelectedItem().toString();
                enseignant = findViewById(R.id.id_enseignant);
                if (enseignant.getText().toString().length() > 0) {
                    id_enseignant = Integer.parseInt(enseignant.getText().toString());
                    if (id_enseignant != 0) {
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.VISIBLE);
                        afficherliste(getList());
                        String texte_id = getResources().getString(R.string.id) + " : " + id_enseignant;
                        title1.setText(texte_id + "  " + jour_seance + " " + heure_seance + " " + date_absence);
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText(getResources().getString(R.string.id_null));
                    }
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.id_empty));
                }
            }
        });

        // le 2eme affichage
        sqLiteHelper = new SQLiteHelper(this, "liste.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ABSENCE(Id_etudiant INTEGER  ,id_enseignant INTEGER," +
                " date_absence VARCHAR(8), jour_seance VARCHAR(30), heure_seance VARCHAR(30), primary key(id_etudiant," +
                "jour_seance,heure_seance,date_absence))");

        etudiant = findViewById(R.id.id_etudiant);
        ajouter = findViewById(R.id.ajouter);
        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message.setVisibility(View.GONE);
                if (etudiant.length() > 0) {
                    id_etudiant = Integer.parseInt(etudiant.getText().toString());
                    if (id_etudiant != 0) {
                        try {
                            sqLiteHelper.insertData(id_etudiant, id_enseignant, date_absence, jour_seance, heure_seance);
                            etudiant.getText().clear();
                            etudiant.setHint(getResources().getString(R.string.entrer_id_etudiant));
                            afficherliste(getList());
                        } catch (Exception e) {
                            message.setVisibility(View.VISIBLE);
                            message.setText(getResources().getString(R.string.duplicated));
                            e.printStackTrace();
                        }
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText(getResources().getString(R.string.id_null));
                    }
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.id_empty));
                }
            }
        });
        // la fin d'opération
        finish = findViewById(R.id.terminer_operation);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                finish.setTextColor(getResources().getColor(R.color.white));
                goToLogin();
            }
        });
    }

    private void afficherliste(ArrayList<AbsenceSQL> list) {
        // on affiche les séances à l'aide d'un adapter qui gère les absences dans un recylerviwe
        if (list.size() >= 1) {
            finish.setVisibility(View.VISIBLE);
        } else {
            finish.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter_Ofline_take_presence adapter = new Adapter_Ofline_take_presence(getApplicationContext(), list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private ArrayList<AbsenceSQL> getList() {
        list = new ArrayList<>();
        Cursor cursor = MainHorsLigne.sqLiteHelper.getData("SELECT * FROM ABSENCE");
        list.clear();
        while (cursor.moveToNext()) {
            int id_etudiant = cursor.getInt(0);
            String jour = cursor.getString(3);
            String heure = cursor.getString(4);
            String date = cursor.getString(2);

            list.add(new AbsenceSQL(id_etudiant, 0, jour, heure, date));
        }
        return list;
    }

    // suppression d'absence
    @Override
    public void supprimer(View view, int position) {
        AbsenceSQL absenceSQL = list.get(position);
        sqLiteHelper.deleteData(absenceSQL);
        afficherliste(getList());
    }

    private void goToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
