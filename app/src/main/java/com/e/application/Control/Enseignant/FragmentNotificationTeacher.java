package com.e.application.Control.Enseignant;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

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
import com.e.application.Adapters.AdapterEnseignant.Adapter_FragmentNotifications;
import com.e.application.Helpers.AfficherNotification;
import com.e.application.Helpers.NotificationResponse;
import com.e.application.Model.Enseignant;
import com.e.application.Model.NotificationChangementSeance;
import com.e.application.Model.NotificationSeanceSupp;
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

public class FragmentNotificationTeacher extends Fragment implements Adapter_FragmentNotifications.ItemClickListener {

    View view;
    Enseignant enseignant;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    String message = "", etat = "";
    private ArrayList<Seance> seances;
    private TextView vu_all, notification_vide, delete_all;
    private TableLayout options;
    private ArrayList<AfficherNotification> afficherNotifications;

    public FragmentNotificationTeacher() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification_enseignant, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.notifications);

        // affichage de message au top de la page
        TextView message_top = view.findViewById(R.id.message_top);
        notification_vide = view.findViewById(R.id.notification_vide);
        options = view.findViewById(R.id.options);
        Bundle bundle = getArguments();
        // vérification s'il y a un message.(le message est défini aprèes la modification du notificaiton)
        assert bundle != null;
        if (bundle.getSerializable("message") != null) {
            // l'attribut message contient la valeur du message à afficher
            message = (String) bundle.getSerializable("message");
            // l'attribut etat peut etre"ok"( message du succés) ou "not ok"(message d'erreur)
            etat = (String) bundle.getSerializable("etat");
        }
        assert etat != null;
        switch (etat) {
            case "ok":
                // affichage d'un message du succès
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setBackgroundColor(getResources().getColor(R.color.light_back));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
                break;
            case "not ok":
                // affichage d'un message d'erreur
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                message_top.setBackgroundColor(getResources().getColor(R.color.red_light));
                message_top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_error_red);
                break;
            case "vide":
                notification_vide.setVisibility(View.VISIBLE);
                options.setVisibility(View.GONE);
                break;
        }

        if (!etat.equals("vide")) {
            // récupération des seances et enseignant
            loginPreferenceConfig = new LoginPreferencesConfig(getActivity().getApplicationContext());
            String json_enseignant = loginPreferenceConfig.getEnseignant();
            enseignant = gson.fromJson(json_enseignant, Enseignant.class);
            String json_seances = loginPreferenceConfig.getSeances();
            seances = gson.fromJson(json_seances, new TypeToken<ArrayList<Seance>>() {
            }.getType());

            // récupération de notificaiton
            getNotification();
        }

        delete_all = view.findViewById(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_all.setBackground(getResources().getDrawable(R.drawable.box_full_black));
                supprimerAll(afficherNotifications);
            }
        });
        vu_all = view.findViewById(R.id.vu_all);
        vu_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vu_all.setBackground(getResources().getDrawable(R.drawable.box_full_black));
                vuAll(afficherNotifications);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNotification() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<NotificationResponse> call = api1.GetNotificationsOfUser(enseignant.getId_utilisateur(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                if (response.isSuccessful()) {
                    NotificationResponse notificationResponses = response.body();
                    assert notificationResponses != null;
                    setView(notificationResponses);
                } else {
                    notification_vide.setVisibility(View.VISIBLE);
                    options.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not ok";
                refresh();
            }
        });
    }

    private void setView(NotificationResponse notificationResponses) {
        if (notificationResponses.getNotificationsChangement().size() + notificationResponses.getNotificationsSupp().size() >= 1) {
            notification_vide.setVisibility(View.GONE);
            afficherNotifications = new ArrayList<>();

            if (notificationResponses.getNotificationsSupp().size() >= 1) {
                ArrayList<NotificationSeanceSupp> notificationSeanceSupps = notificationResponses.getNotificationsSupp();
                for (NotificationSeanceSupp notificationSeanceSupp : notificationSeanceSupps) {

                    AfficherNotification afficherNotification = new AfficherNotification(notificationSeanceSupp,
                            "SeanceSupp", notificationSeanceSupp.getSeanceSupp().getCode_seance(),
                            notificationSeanceSupp.getSeanceSupp().getJour().toString(), notificationSeanceSupp.getSeanceSupp().getHeure());
                    afficherNotifications.add(afficherNotification);
                }
            }

            if (notificationResponses.getNotificationsChangement().size() >= 1) {
                ArrayList<NotificationChangementSeance> notificationChangementSeances = notificationResponses.getNotificationsChangement();
                for (NotificationChangementSeance notificationChangementSeance : notificationChangementSeances) {
                    AfficherNotification afficherNotification = new AfficherNotification(notificationChangementSeance,
                            "ChangementSeance", notificationChangementSeance.getChangementSeance().getCode_seance(),
                            notificationChangementSeance.getChangementSeance().getNouveau_jour().toString(), notificationChangementSeance.getChangementSeance().getheure());
                    afficherNotifications.add(afficherNotification);
                }
            }

            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Adapter_FragmentNotifications adapter = new Adapter_FragmentNotifications(this, afficherNotifications, seances);
            adapter.setClickListener();
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        } else {
            notification_vide.setVisibility(View.VISIBLE);
            options.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void supprimer(int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.DeleteNotification(afficherNotifications.get(position).getId_notification(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    message = getResources().getString(R.string.delete_notification_ok);
                    etat = "ok";
                    refresh();

                } else {
                    message = getResources().getString(R.string.delete_notification_not_ok);
                    etat = "not_ok";
                    refresh();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                refresh();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void supprimerAll(ArrayList<AfficherNotification> afficherNotifications) {

        for (AfficherNotification afficherNotification : afficherNotifications) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final API api = retrofit.create(API.class);
            Call<ResponseBody> call = api.DeleteNotification(afficherNotification.getId_notification(), "Bearer " + loginPreferenceConfig.getToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    message = getResources().getString(R.string.network);
                    etat = "not_ok";
                }
            });
        }
        refresh();
    }

    @Override
    public void setVue(int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/update/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.SetNotificationVue(afficherNotifications.get(position).getId_notification(), "Bearer " + loginPreferenceConfig.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    message = getResources().getString(R.string.delete_notification_ok);
                    etat = "ok";
                    getNotification();

                } else {
                    message = getResources().getString(R.string.delete_notification_not_ok);
                    etat = "not_ok";
                    refresh();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "not_ok";
                refresh();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void vuAll(ArrayList<AfficherNotification> afficherNotifications) {

        for (AfficherNotification afficherNotification : afficherNotifications) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/update/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final API api = retrofit.create(API.class);
            Call<ResponseBody> call = api.SetNotificationVue(afficherNotification.getId_notification(), "Bearer " + loginPreferenceConfig.getToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    message = getResources().getString(R.string.network);
                    etat = "not_ok";
                }
            });
        }
        refresh();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void refresh() {
        Fragment fragment = new FragmentNotificationTeacher();
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        bundle.putSerializable("etat", etat);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.popBackStack();// on peut pas back à cet fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}