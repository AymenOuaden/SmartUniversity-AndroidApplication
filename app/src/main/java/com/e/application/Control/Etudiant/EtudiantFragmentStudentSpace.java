package com.e.application.Control.Etudiant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.e.application.R;

import java.util.Objects;

public class EtudiantFragmentStudentSpace extends Fragment {

    EtudiantFragmentStudentSpace() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.etudiant_fragment_student_space, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.espace_etudiant);

        //récupération des attributs depuis bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        String message = (String) bundle.getSerializable("message");
        String etat = (String) bundle.getSerializable("etat");
        assert message != null;
        if (!message.equals("vide")) {
            TextView msg = view.findViewById(R.id.message);
            msg.setVisibility(View.VISIBLE);
            assert etat != null;
            if (etat.equals("ok")) {
                msg.setBackgroundColor(getResources().getColor(R.color.light_back));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
            } else {
                msg.setBackgroundColor(getResources().getColor(R.color.red_light));
                msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
            }
            msg.setText(message);
        }

        CardView card_take_presence = view.findViewById(R.id.card_take_presences);
        CardView card_manage_absences = view.findViewById(R.id.card_manage_absences);
        CardView card_conge_academique = view.findViewById(R.id.card_conge_academique);
        CardView card_edit_profile = view.findViewById(R.id.card_edit_profile);
        CardView card_schudule = view.findViewById(R.id.card_schedule);

        card_take_presence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("take_presence");
            }
        });
        card_manage_absences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("releve_absence");
            }
        });
        card_conge_academique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("conge_academique");
            }
        });
        card_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("update_profile");
            }
        });
        card_schudule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("schedule");
            }
        });

        return view;
    }


    private void goTo(String redirection) {
        Intent intent = new Intent(getActivity(), EtudiantFragmentMainActivity.class);
        intent.putExtra("redirection", redirection);
        startActivity(intent);
    }
}