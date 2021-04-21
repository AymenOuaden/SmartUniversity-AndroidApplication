package com.e.application.Control.Enseignant;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.e.application.Helpers.AbsenceResponse;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Enseignant;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentStatistics extends Fragment {

    Enseignant enseignant;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    View view;
    private TextView title;
    private ArrayList<SeanceResponse> seanceResponses;
    private TableLayout choix;

    public FragmentStatistics() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistic, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.statistiques);

        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        String j_seances_repsonse = loginPreferenceConfig.getSeanceResponseEtudiant();
        seanceResponses = gson.fromJson(
                j_seances_repsonse,
                new TypeToken<ArrayList<SeanceResponse>>() {
                }.getType()
        );

        final BarChart chart = view.findViewById(R.id.barchart);
        final PieChart pieChart = view.findViewById(R.id.piechart);
        title = view.findViewById(R.id.title);
        choix = view.findViewById(R.id.choix);
        choix.setVisibility(View.VISIBLE);


        // récupération de spinner depuis layout
           /* le "spinner" qui nous donne la possibilité de choisir un élément depuis plusieurs
            éléments, dans notre cas on choisit les jours et les heures des séances supplémentaires */
        final Spinner spinner_type = view.findViewById(R.id.type);
        //creation les listes des élémnets des spinners.
        String[] items_type = new String[]{getResources().getString(R.string.jour), getResources().getString(R.string.heure)};
            /*création des adaptateurs pour décrire comment les éléments sont affichés, les adaptateurs
             sont utilisés à plusieurs endroits dans Android. */
        final ArrayAdapter<String> adapter_type = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items_type);
        spinner_type.setAdapter(adapter_type);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item.toString().equals(getResources().getString(R.string.jour))) {
                    afficherBar("jour");
                } else {
                    afficherBar("heure");
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        final Button bar = view.findViewById(R.id.bar);
        final Button circle = view.findViewById(R.id.circle);

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choix.setVisibility(View.GONE);
                chart.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                circle.setBackground(getResources().getDrawable(R.drawable.box_full_black));
                circle.setTextColor(getResources().getColor(R.color.white));
                bar.setBackground(getResources().getDrawable(R.drawable.box_white_x_white));
                bar.setTextColor(getResources().getColor(R.color.black));
                title.setVisibility(View.VISIBLE);
                afficherCircle();
            }
        });

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choix.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
                bar.setBackground(getResources().getDrawable(R.drawable.box_full_black));
                bar.setTextColor(getResources().getColor(R.color.white));
                circle.setBackground(getResources().getDrawable(R.drawable.box_white_x_white));
                circle.setTextColor(getResources().getColor(R.color.black));
                title.setVisibility(View.GONE);
                afficherBar("jour");
            }
        });

        // affichage bar défault par bar
        afficherBar("jour");
        return view;
    }


    private void afficherBar(String affichage) {
        BarChart chart = view.findViewById(R.id.barchart);
        chart.setDescription(null);

        int d = 0, l = 0, m = 0, me = 0, j = 0;

        for (SeanceResponse seanceResponse : seanceResponses) {
            if (seanceResponse.getEtudiants() != null) {
                for (EtudiantResponse etudiantResponse : seanceResponse.getEtudiants()) {
                    if (etudiantResponse.getAbsences() != null) {
                        for (AbsenceResponse ignored : etudiantResponse.getAbsences()) {

                            if (affichage.equals("jour")) {
                                String jour = seanceResponse.getSeance().getJour().toString();
                                switch (jour) {
                                    case "dimanche":
                                        d++;
                                        break;
                                    case "lundi":
                                        l++;
                                        break;
                                    case "mardi":
                                        m++;
                                        break;
                                    case "mercredi":
                                        me++;
                                        break;
                                    case "jeudi":
                                        j++;
                                        break;
                                }
                            } else {
                                String heure = seanceResponse.getSeance().getHeure();
                                switch (heure) {
                                    case "8:30":
                                        d++;
                                        break;
                                    case "10:00":
                                        l++;
                                        break;
                                    case "11:30":
                                        m++;
                                        break;
                                    case "13:00":
                                        me++;
                                        break;
                                    case "14:30":
                                        j++;
                                        break;
                                }

                            }
                        }
                    }
                }
            }
        }


        ArrayList<BarEntry> absences = new ArrayList<>();
        absences.add(new BarEntry(d, 0));
        absences.add(new BarEntry(l, 1));
        absences.add(new BarEntry(m, 2));
        absences.add(new BarEntry(me, 3));
        absences.add(new BarEntry(j, 4));

        ArrayList<String> choix = new ArrayList<>();
        if (affichage.equals("jour")) {
            choix.add(getResources().getString(R.string.dimanche));
            choix.add(getResources().getString(R.string.lundi));
            choix.add(getResources().getString(R.string.mardi));
            choix.add(getResources().getString(R.string.mercredi));
            choix.add(getResources().getString(R.string.jeudi));
        } else {
            choix.add("8:30");
            choix.add("10:00");
            choix.add("11:30");
            choix.add("13:00");
            choix.add("14:30");
        }
        BarDataSet bardataset = new BarDataSet(absences, "nombre of absences");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        bardataset.setValueTextSize(15f);
        BarData data = new BarData(choix, bardataset);

        data.setValueTextSize(15f);
        chart.animateY(1000);
        chart.setData(data);
        chart.getLegend().setTextSize(15f);


    }

    private void afficherCircle() {

        PieChart pieChart = view.findViewById(R.id.piechart);
        pieChart.setDescription(null);

        ArrayList<String> seances = new ArrayList<>();
        ArrayList<Integer> nbr_absences = new ArrayList<>();

        for (SeanceResponse seanceResponse : seanceResponses) {
            seances.add(seanceResponse.getSeance().getCode_module());
            if (seanceResponse.getEtudiants() != null) {
                int abs = 0;
                for (EtudiantResponse etudiantResponse : seanceResponse.getEtudiants()) {
                    abs = abs + etudiantResponse.getNombreAbsences();
                }
                nbr_absences.add(abs);
            } else {
                nbr_absences.add(0);
            }
        }

        ArrayList<com.github.mikephil.charting.data.Entry> absences = new ArrayList<>();
        ArrayList<String> module = new ArrayList<>();

        int nbr_absence_tot = 0;
        for (int absence : nbr_absences) {
            nbr_absence_tot = nbr_absence_tot + absence;
        }

        int i = 0;
        if (seances.size() >= 1) {
            for (String seance : seances) {
                if (nbr_absence_tot != 0) {
                    if (nbr_absences.get(i) != 0) {
                        absences.add(new BarEntry(nbr_absences.get(i) * 100 / (float) nbr_absence_tot, i));
                        module.add(seance);
                    }
                    i++;
                }
            }
        }

        PieDataSet dataSet = new PieDataSet(absences, "");
        PieData data = new PieData(module, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(15f);
        pieChart.animateXY(1000, 1000);
        pieChart.setCenterTextSize(15f);


    }

}