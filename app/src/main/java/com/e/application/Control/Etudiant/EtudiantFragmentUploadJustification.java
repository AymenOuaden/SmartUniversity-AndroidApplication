package com.e.application.Control.Etudiant;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e.application.API.API;
import com.e.application.Dots.Dot_Justification_Android;
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Model.Absence;
import com.e.application.Model.Etudiant;
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
public class EtudiantFragmentUploadJustification extends Fragment {

    private byte[] image_bytesArray;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView mImageView;
    private Uri mImageUri;
    private String extension, token;
    private ProgressBar pgsBar, progress_circular;
    private Button releve, show_ustification, mButtonChooseImage, mButtonUpload, bouton_uploadin;
    private RelativeLayout after_upload, wait;
    private TextView title, message_error, txtView;
    private Absence absence;
    private LoginPreferencesConfig loginPreferenceConfig;
    private Gson gson = new Gson();
    private Etudiant etudiant;
    private ConstraintLayout option;

    public EtudiantFragmentUploadJustification() {
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.etudiant_fragement_upload_justification, container, false);
        wait = view.findViewById(R.id.wait);
        // récupération d'etudiant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        token = loginPreferenceConfig.getToken();
        String json_absence = loginPreferenceConfig.getAbsence();
        absence = gson.fromJson(json_absence, Absence.class);
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        // récupération depuis layout
        after_upload = view.findViewById(R.id.apres_upload);
        option = view.findViewById(R.id.options_plus);
        // bouttons
        releve = view.findViewById(R.id.releve);
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
                wait.setVisibility(View.VISIBLE);
                progress_circular.setVisibility(View.VISIBLE);

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

        releve.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                releve.setBackground(getResources().getDrawable(R.drawable.button_full_color_accent_round));
                releve.setTextColor(getResources().getColor(R.color.white));
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });

        return view;
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
        getAbsencesOfEtudiant();
        wait.setVisibility(View.GONE);
        pgsBar.setProgress(100);
        txtView.setText(100 + "/" + pgsBar.getMax());
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
        option.setVisibility(View.VISIBLE);
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

    /* la récupération des absences depuis la base de données , puis les envoyés
 à la page concernée basant sur la redirection*/
    private void getAbsencesOfEtudiant() {
        wait.setVisibility(View.VISIBLE);
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<ArrayList<AbsenceDepartementResponse>> call = api1.GetAbsencesOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<ArrayList<AbsenceDepartementResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<AbsenceDepartementResponse>> call, Response<ArrayList<AbsenceDepartementResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<AbsenceDepartementResponse> absenceDepartementResponses = response.body();
                    loginPreferenceConfig.setAbsenceDepartementResponses(absenceDepartementResponses);
                } else {
                    goToOptionDeGestion("FragmentReleveAbsences");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AbsenceDepartementResponse>> call, Throwable t) {
                goToOptionDeGestion("FragmentReleveAbsences");
            }
        });

    }

    // redirection vers d'autres fragments
    private void goToOptionDeGestion(String redirection) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (redirection) {
            case "FragmentReleveAbsences":
                fragment = new EtudiantFragmentReleveAbsences();
                break;
            case "FragmentDisplayJustification":
                fragment = new EtudiantFragmentDisplayJustification();
                bundle.putSerializable("message", "");
                bundle.putSerializable("etat", "");

                break;

            default:
                fragment = new EtudiantFragmentStudentSpace();
                break;
        }
        fragment.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.popBackStack();// on peut pas back à cet fragment seulement depuis le icon_menu
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}






