package com.e.application.Control.Enseignant;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import com.e.application.API.API;
import com.e.application.Dots.Dot_Justification_Android;
import com.e.application.Helpers.EtudiantResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Justification;
import com.e.application.Model.Seance;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings({"NullableProblems"})
public class FragmentUploadJustification extends Fragment {

    View view;
    String token;
    private byte[] image_bytesArray;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView mImageView;
    private Uri mImageUri;
    private String extension;
    private ArrayList<EtudiantResponse> liste_etudiants_absences;
    private ProgressBar pgsBar, progress_circular;
    private Button exclus, releve, statistiques, afficher_seances, show_ustification, mButtonChooseImage, mButtonUpload, bouton_uploadin;
    private RelativeLayout after_upload;
    private TextView title, message_error, txtView;
    private Absence absence;
    Enseignant enseignant;
    private Seance seance;
    LoginPreferencesConfig loginPreferenceConfig;
    Gson gson = new Gson();
    private RelativeLayout wait;

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_upload_justification, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.upload_justification);

        wait = view.findViewById(R.id.wait);
        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        token = loginPreferenceConfig.getToken();
        String json_absence = loginPreferenceConfig.getAbsence();
        absence = gson.fromJson(json_absence, Absence.class);
        String json_enseignant = loginPreferenceConfig.getEnseignant();
        enseignant = gson.fromJson(json_enseignant, Enseignant.class);
        String json_seance = loginPreferenceConfig.getSeance();
        seance = gson.fromJson(json_seance, Seance.class);
        // récupération depuis layout
        after_upload = view.findViewById(R.id.apres_upload);
        // bouttons
        exclus = view.findViewById(R.id.exclus);
        releve = view.findViewById(R.id.releve);
        statistiques = view.findViewById(R.id.statistiques);
        afficher_seances = view.findViewById(R.id.afficher_seances);
        show_ustification = view.findViewById(R.id.show_justification);
        mButtonChooseImage = view.findViewById(R.id.button_choose_image);
        mButtonUpload = view.findViewById(R.id.button_upload);
        bouton_uploadin = view.findViewById(R.id.uploading);
        // progress bar
        pgsBar = view.findViewById(R.id.progress_bar);
        progress_circular = view.findViewById(R.id.progress_circular);
        // texte
        txtView = view.findViewById(R.id.tView);
        title = view.findViewById(R.id.title);
        message_error = view.findViewById(R.id.message_error);
        // image
        mImageView = view.findViewById(R.id.imageview);

        // implementation des boutons
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openFileChooser();
                openFileChooser();
            }
        });
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // wait.setVisibility(View.VISIBLE);
                //progress_circular.setVisibility(View.VISIBLE);
                viewUploading();
            }
        });
        show_ustification.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                show_ustification.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                show_ustification.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentDisplayJustification");
            }
        });
        exclus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                exclus.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                exclus.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentExcludedStudents");
            }
        });
        statistiques.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                statistiques.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                statistiques.setTextColor(getResources().getColor(R.color.white));
                goTo();
            }
        });
        releve.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                releve.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });
        afficher_seances.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                afficher_seances.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                afficher_seances.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentManageAbsences");
            }
        });

        return view;
    }

    public FragmentUploadJustification() {
    }

    // la méhode ouvre l'espace de rangement des images dans le mobile
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /* la méthode déclenche automatiquement après la sélectionne d'une image, elle récupère l'image
       depuis le mobile, elle appelle la méthode qui reduce la taile de l'image*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonChooseImage.setText(getResources().getString(R.string.select_another_image));
        mImageView.setVisibility(View.VISIBLE);
        mButtonUpload.setVisibility(View.VISIBLE);
        pgsBar.setVisibility(View.VISIBLE);
        txtView.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
            BackgroundImageResize backgroundImageResize = new BackgroundImageResize(
                    ((BitmapDrawable) mImageView.getDrawable()).getBitmap()
            );
            // la méthode "doInBackgroun" compresse l'image
            image_bytesArray = backgroundImageResize.doInBackground(mImageUri);
            extension = getExtension(getContext(), mImageUri);
        }
    }

    // la classe compresse la taille de l'image
    @SuppressLint("StaticFieldLeak")
    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        BackgroundImageResize(Bitmap bitmap) {
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "compressing image", Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("NewApi")
        @Override
        protected byte[] doInBackground(Uri... params) {
            if (mBitmap == null) {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), params[0]);
                } catch (IOException ignored) {
                }
            }
            byte[] bytes;
            bytes = getBytesFromBitmap(mBitmap);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            image_bytesArray = bytes;
        }
    }

    // la méthode convert un bitmap vers an arrayliste des bytes
    private static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // retourne l'extension de l'image
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getExtension(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(Objects.requireNonNull(uri.getPath()))).toString());
        }
        return extension;
    }

    // le nouveau affichage apres le click sur le bouton upload
    @SuppressLint("SetTextI18n")
    private void viewUploading() {

        mButtonUpload.setVisibility(View.GONE);
        bouton_uploadin.setVisibility(View.VISIBLE);
        progress_circular.setVisibility(View.VISIBLE);
        pgsBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        txtView.setVisibility(View.VISIBLE);
        pgsBar.setProgress(10);
        txtView.setText("100/10");

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                // appel la méthode qui stock la justification dans la BD via retrofit
                justifier();
            }
        }, 100);


    }

    //la méthode qui stock la justification dans la BD via retrofit
    @SuppressLint("SetTextI18n")
    private void justifier() {
        final Dot_Justification_Android justification = new Dot_Justification_Android(absence.getNumero_absence(), image_bytesArray, extension);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/upload/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        final Call<ResponseBody> call = api.UploadJustificationFile(justification, "Bearer " + token);

        int i = (int) (Math.random() * 60 + 39);
        for (int j = 15; j <= i; j++) {
            pgsBar.setProgress(j);
            txtView.setText(j + "/100");
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run(
            ) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loginPreferenceConfig.setJustification(new Justification(
                                    0, absence.getNumero_absence(), image_bytesArray,
                                    extension, Calendar.getInstance().getTime().toString(),
                                    Seance.Etat_Demande.nonTraite));
                            viewAfterUpload();
                        } else {
                            viewAfterFail("other");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        viewAfterFail("network");
                    }
                });

            }
        }, 1);

    }

    // le nouveau affichage apres le click sur le bouton upload
    @SuppressLint("SetTextI18n")
    private void viewAfterUpload() {
        setNewListeEtudiantsAbsences();
        wait.setVisibility(View.GONE);
        pgsBar.setProgress(100);
        txtView.setVisibility(View.GONE);
        progress_circular.setVisibility(View.GONE);
        bouton_uploadin.setVisibility(View.GONE);
        bouton_uploadin.setText(getResources().getString(R.string.completed));
        mButtonChooseImage.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);
        after_upload.setVisibility(View.VISIBLE);
        message_error.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.upload_succ));
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_checkbox_coloraccent);
    }

    // l'affichage si le stockage de la justification échoue
    @SuppressLint("SetTextI18n")
    private void viewAfterFail(String cause) {
        // l'argument "cause" définie la cause d'erreur
        wait.setVisibility(View.GONE);
        pgsBar.setProgress(0);
        txtView.setText(0 + "/" + pgsBar.getMax());
        progress_circular.setVisibility(View.GONE);
        bouton_uploadin.setVisibility(View.GONE);
        mButtonUpload.setVisibility(View.VISIBLE);
        mButtonChooseImage.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        message_error.setVisibility(View.VISIBLE);
        if (cause.equals("network")) {
            message_error.setText(getResources().getString(R.string.upload_fail));
        } else {
            if (mImageUri == null) {
                message_error.setText(getResources().getString(R.string.upload_not_succ) + getResources().getString(R.string.image_vide));
            } else {
                message_error.setText(getResources().getString(R.string.upload_not_succ) + getResources().getString(R.string.image_larg));
            }
        }
    }

    // la méthode récupère les nouvelles données aprés le téléchargement de la justification
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
            public void onResponse(Call<ArrayList<SeanceResponse>> call, Response<ArrayList<SeanceResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<SeanceResponse> seanceResponses;
                    seanceResponses = response.body();
                    if (seanceResponses != null) {
                        for (SeanceResponse r : seanceResponses) {
                            if (r.getSeance().getCode_seance().equals(seance.getCode_seance())) {
                                liste_etudiants_absences = r.getEtudiants();
                            }
                        }
                    }
                    loginPreferenceConfig.setListeEtudiantsEtLeursAbsences(liste_etudiants_absences);
                } else {
                    message_error.setText(getResources().getString(R.string.erreur_not_succ));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SeanceResponse>> call, Throwable t) {
                viewAfterFail("network");
            }
        });
    }

    // redirection vers d'autres fragments
    private void goToOptionDeGestion(String redirection) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (redirection) {
            case "FragmentExcludedStudents":
                fragment = new FragmentExcludedStudents();
                break;
            case "FragmentReleveAbsences":
                fragment = new FragmentReleveAbsences();
                break;
            case "FragmentDisplayJustification":
                fragment = new FragmentDisplayJustification();
                bundle.putSerializable("affichage", "display_after_upload");
                break;
            case "FragmentManageAbsences":
                fragment = new FragmentManageAbsences();
                break;
            default:
                fragment = new FragmentTeacherSpace();
                break;
        }
        bundle.putSerializable("message", "");
        bundle.putSerializable("etat", "");
        fragment.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.popBackStack();// on peut pas back à cet fragment seulement depuis le icon_menu
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goTo() {
        Intent intent = new Intent(getActivity(), FragmentMainActivityTeacher.class);
        intent.putExtra("redirection", "statistics");
        startActivity(intent);

    }

}






