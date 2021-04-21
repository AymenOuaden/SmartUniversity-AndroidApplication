package com.e.application.Control.Enseignant;

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

import com.e.application.Model.Enseignant;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import java.util.Objects;

public class FragmentTeacherSpace extends Fragment {

    Enseignant enseignant;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_espace_enseignant, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.espace_enseignant);

        //récupération des attributs depuis bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        String message = (String) bundle.getSerializable("message");
        String etat = (String) bundle.getSerializable("etat");
        assert message != null;
        if (!message.equals("")) {
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
        CardView card_add_class = view.findViewById(R.id.card_add_class);
        CardView card_change_class = view.findViewById(R.id.card_change_class);
        CardView card_edit_profile = view.findViewById(R.id.card_edit_profile);
        CardView card_delete_account = view.findViewById(R.id.card_delete_account);

        card_take_presence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("take_presences");
            }
        });
        card_manage_absences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("manage_absences");
            }
        });
        card_add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("add_class");
            }
        });
        card_change_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("change_class");
            }
        });
        card_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("update_profile");
            }
        });
        card_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("delete_account");
            }
        });

        return view;
    }


    private void goTo(String redirection) {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", redirection);
        startActivity(intent);
    }
}