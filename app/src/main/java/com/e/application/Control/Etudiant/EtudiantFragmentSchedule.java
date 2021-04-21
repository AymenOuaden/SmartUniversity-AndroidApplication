package com.e.application.Control.Etudiant;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Seance;
import com.e.application.Model.SeanceSupp;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class EtudiantFragmentSchedule extends Fragment {

    private Gson gson = new Gson();
    private View view;

    public EtudiantFragmentSchedule() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.etudiant_fragment_schedule, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.schedule);

        LoginPreferencesConfig loginPreferencesConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
        String json_seance_responses = loginPreferencesConfig.getSeanceResponseEtudiant();
        ArrayList<SeanceResponse> seanceResponses = gson.fromJson(
                json_seance_responses,
                new TypeToken<ArrayList<SeanceResponse>>() {
                }.getType()
        );
        // get la liste des seances from seance response
        ArrayList<Seance> seances = new ArrayList<>();
        for (SeanceResponse seanceResponse : seanceResponses) {
            seances.add(seanceResponse.getSeance());
        }
        // get la liste des seances supplémentaire from seance response
        ArrayList<SeanceSupp> seanceSupps = new ArrayList<>();
        for (SeanceResponse seanceResponse : seanceResponses) {
            seanceSupps.addAll(seanceResponse.getSeancesSupp());
        }

        if (seances.size() + seanceSupps.size() >= 1) {
            setView(seances, seanceSupps);
        } else {
            TextView vide = view.findViewById(R.id.vide);
            vide.setVisibility(View.VISIBLE);
            TableLayout emploi = view.findViewById(R.id.emploi);
            emploi.setVisibility(View.GONE);
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setView(ArrayList<Seance> seances, ArrayList<SeanceSupp> seanceSupps) {

        TextView dimanche8, dimanche10, dimanche11, dimanche13, dimanche14,
                lundi8, lundi10, lundi11, lundi13, lundi14,
                mardi8, mardi10, mardi11, mardi13, mardi14,
                mercredi8, mercredi10, mercredi11, mercredi13, mercredi14,
                jeudi8, jeudi10, jeudi11, jeudi13, jeudi14;

        dimanche8 = view.findViewById(R.id.dimanche8);
        dimanche10 = view.findViewById(R.id.dimanche10);
        dimanche11 = view.findViewById(R.id.dimanche11);
        dimanche13 = view.findViewById(R.id.dimanche13);
        dimanche14 = view.findViewById(R.id.dimanche14);
        lundi8 = view.findViewById(R.id.lundi8);
        lundi10 = view.findViewById(R.id.lundi10);
        lundi11 = view.findViewById(R.id.lundi11);
        lundi13 = view.findViewById(R.id.lundi13);
        lundi14 = view.findViewById(R.id.lundi14);
        mardi8 = view.findViewById(R.id.mardi8);
        mardi10 = view.findViewById(R.id.mardi10);
        mardi11 = view.findViewById(R.id.mardi11);
        mardi13 = view.findViewById(R.id.mardi13);
        mardi14 = view.findViewById(R.id.mardi14);
        mercredi8 = view.findViewById(R.id.mercredi8);
        mercredi10 = view.findViewById(R.id.mercredi10);
        mercredi11 = view.findViewById(R.id.mercredi11);
        mercredi13 = view.findViewById(R.id.mercredi13);
        mercredi14 = view.findViewById(R.id.mercredi14);
        jeudi8 = view.findViewById(R.id.jeudi8);
        jeudi10 = view.findViewById(R.id.jeudi10);
        jeudi11 = view.findViewById(R.id.jeudi11);
        jeudi13 = view.findViewById(R.id.jeudi13);
        jeudi14 = view.findViewById(R.id.jeudi14);
        if (seances.size() >= 1) {
            for (Seance seance : seances) {
                if (seance.getJour() == Seance.Jour.dimanche && seance.getHeure().equals("8:30")) {
                    dimanche8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        dimanche8.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        dimanche8.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.dimanche && seance.getHeure().equals("10:00")) {
                    dimanche10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        dimanche10.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        dimanche10.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.dimanche && seance.getHeure().equals("11:30")) {
                    dimanche11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        dimanche11.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        dimanche11.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.dimanche && seance.getHeure().equals("13:00")) {
                    dimanche13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        dimanche13.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        dimanche13.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.dimanche && seance.getHeure().equals("14:30")) {
                    dimanche14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        dimanche14.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        dimanche14.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.lundi && seance.getHeure().equals("8:30")) {
                    lundi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        lundi8.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        lundi8.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.lundi && seance.getHeure().equals("10:00")) {
                    lundi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        lundi10.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        lundi10.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.lundi && seance.getHeure().equals("11:30")) {
                    lundi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        lundi11.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        lundi11.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.lundi && seance.getHeure().equals("13:00")) {
                    lundi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        lundi13.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        lundi13.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.lundi && seance.getHeure().equals("14:30")) {
                    lundi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        lundi14.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        lundi14.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mardi && seance.getHeure().equals("8:30")) {
                    mardi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mardi8.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mardi8.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mardi && seance.getHeure().equals("10:00")) {
                    mardi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mardi10.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mardi10.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mardi && seance.getHeure().equals("11:30")) {
                    mardi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mardi11.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mardi11.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mardi && seance.getHeure().equals("13:00")) {
                    mardi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mardi13.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mardi13.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mardi && seance.getHeure().equals("14:30")) {
                    mardi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mardi14.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mardi14.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mercredi && seance.getHeure().equals("8:30")) {
                    mercredi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi8.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi8.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mercredi && seance.getHeure().equals("10:00")) {
                    mercredi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi10.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi10.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mercredi && seance.getHeure().equals("11:30")) {
                    mercredi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi11.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi11.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mercredi && seance.getHeure().equals("13:00")) {
                    mercredi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi13.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi13.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.mercredi && seance.getHeure().equals("14:30")) {
                    mercredi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi14.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi14.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.jeudi && seance.getHeure().equals("8:30")) {
                    jeudi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        jeudi8.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        jeudi8.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.jeudi && seance.getHeure().equals("10:00")) {
                    jeudi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        mercredi10.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        mercredi10.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.jeudi && seance.getHeure().equals("11:30")) {
                    jeudi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        jeudi11.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        jeudi11.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.jeudi && seance.getHeure().equals("13:00")) {
                    jeudi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        jeudi13.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        jeudi13.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } else if (seance.getJour() == Seance.Jour.jeudi && seance.getHeure().equals("14:30")) {
                    jeudi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                    if (seance.getType() == Seance.Type_Seance.TD) {
                        jeudi14.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        jeudi14.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                }
            }
        }

        if (seanceSupps.size() >= 1) {
            for (SeanceSupp seanceSupp : seanceSupps) {

                for (Seance seance : seances) {

                    if (seanceSupp.getCode_seance().equals(seance.getCode_seance())) {

                        if (seanceSupp.getJour() == Seance.Jour.dimanche && seanceSupp.getHeure().equals("8:30")) {
                            dimanche8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            dimanche8.setTextColor(getResources().getColor(R.color.red));
                        } else if (seanceSupp.getJour() == Seance.Jour.dimanche && seanceSupp.getHeure().equals("10:00")) {
                            dimanche10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            dimanche10.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.dimanche && seanceSupp.getHeure().equals("11:30")) {
                            dimanche11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            dimanche11.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.dimanche && seanceSupp.getHeure().equals("13:00")) {
                            dimanche13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            dimanche13.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.dimanche && seanceSupp.getHeure().equals("14:30")) {
                            dimanche14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            dimanche14.setTextColor(getResources().getColor(R.color.red));
                        } else if (seanceSupp.getJour() == Seance.Jour.lundi && seanceSupp.getHeure().equals("8:30")) {
                            lundi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            lundi8.setTextColor(getResources().getColor(R.color.red));
                        } else if (seanceSupp.getJour() == Seance.Jour.lundi && seanceSupp.getHeure().equals("10:00")) {
                            lundi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            lundi10.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.lundi && seanceSupp.getHeure().equals("11:30")) {
                            lundi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            lundi11.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.lundi && seanceSupp.getHeure().equals("13:00")) {
                            lundi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            lundi13.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.lundi && seanceSupp.getHeure().equals("14:30")) {
                            lundi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            lundi14.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mardi && seanceSupp.getHeure().equals("8:30")) {
                            mardi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mardi8.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mardi && seanceSupp.getHeure().equals("10:00")) {
                            mardi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mardi10.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mardi && seanceSupp.getHeure().equals("11:30")) {
                            mardi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mardi11.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mardi && seanceSupp.getHeure().equals("13:00")) {
                            mardi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mardi13.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mardi && seanceSupp.getHeure().equals("14:30")) {
                            mardi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mardi14.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mercredi && seanceSupp.getHeure().equals("8:30")) {
                            mercredi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi8.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mercredi && seanceSupp.getHeure().equals("10:00")) {
                            mercredi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi10.setTextColor(getResources().getColor(R.color.red));
                        } else if (seanceSupp.getJour() == Seance.Jour.mercredi && seanceSupp.getHeure().equals("11:30")) {
                            mercredi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi11.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mercredi && seanceSupp.getHeure().equals("13:00")) {
                            mercredi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi13.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.mercredi && seanceSupp.getHeure().equals("14:30")) {
                            mercredi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi14.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.jeudi && seanceSupp.getHeure().equals("8:30")) {
                            jeudi8.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            jeudi8.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.jeudi && seanceSupp.getHeure().equals("10:00")) {
                            jeudi10.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            mercredi10.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.jeudi && seanceSupp.getHeure().equals("11:30")) {
                            jeudi11.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            jeudi11.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.jeudi && seanceSupp.getHeure().equals("13:00")) {
                            jeudi13.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            jeudi13.setTextColor(getResources().getColor(R.color.red));

                        } else if (seanceSupp.getJour() == Seance.Jour.jeudi && seanceSupp.getHeure().equals("14:30")) {
                            jeudi14.setText(seance.getCode_module() + "\n" + seance.getType().toString());
                            jeudi14.setTextColor(getResources().getColor(R.color.red));
                        }
                    }
                }
            }
        }
    }
}