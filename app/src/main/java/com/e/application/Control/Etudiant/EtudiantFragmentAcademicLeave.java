package com.e.application.Control.Etudiant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e.application.API.API;
import com.e.application.Dots.Dot_CongeAcademique_Android;
import com.e.application.Model.CongeAcademique;
import com.e.application.Model.Etudiant;
import com.e.application.R;
import com.e.application.SharedPrefrences.LoginPreferencesConfig;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class EtudiantFragmentAcademicLeave extends Fragment {

    private byte[] image_bytesArray;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView mImageView;
    private Uri mImageUri;
    private ProgressBar pgsBar, progress_circular;
    private Button mButtonChooseImage, mButtonUpload, bouton_uploadin;
    private TextView message_error, txtView, title;
    private CongeAcademique congeAcademique;
    private LoginPreferencesConfig loginPreferenceConfig;
    private Gson gson = new Gson();
    private Etudiant etudiant;
    private String message = "", etat = "", token, extension;

    public EtudiantFragmentAcademicLeave() {
    }

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.etudiant_fragment_academic_leave, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.conge_academique);

        // récupération d'enseignant et token depuis sahredPrefrences
        loginPreferenceConfig = new LoginPreferencesConfig(Objects.requireNonNull(getActivity()).getApplicationContext());
        token = loginPreferenceConfig.getToken();
        String json_etudiant = loginPreferenceConfig.getEtudiant();
        etudiant = gson.fromJson(json_etudiant, Etudiant.class);
        //
        final RelativeLayout demande_existe = view.findViewById(R.id.demande_existe);
        RelativeLayout demander_conge = view.findViewById(R.id.demander_conge);
        TextView message_top = view.findViewById(R.id.message_top);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getSerializable("message") != null) {
                message = (String) bundle.getSerializable("message");
                etat = (String) bundle.getSerializable("etat");
            }
            if (!message.equals("")) {
                message_top.setVisibility(View.VISIBLE);
                message_top.setText(message);
                if (etat.equals("ok")) {
                    message_top.setBackground(getResources().getDrawable(R.drawable.background_green));
                    message_top.setTextColor(getResources().getColor(R.color.black));
                } else {
                    message_top.setBackground(getResources().getDrawable(R.drawable.background_red_light));
                    message_top.setTextColor(getResources().getColor(R.color.red));
                }
            }
        }
        //-------------------------affichage du conge--------------------------
        String json_congeAcademique = loginPreferenceConfig.getCongeAcademique();
        if (!json_congeAcademique.equals("")) {
            congeAcademique = gson.fromJson(json_congeAcademique, CongeAcademique.class);
        }
        if (congeAcademique != null) {
            demander_conge.setVisibility(View.GONE);
            TextView name = view.findViewById(R.id.etudiant);
            name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
            TextView date_naissance = view.findViewById(R.id.date_naissance);
            StringBuilder date = new StringBuilder();
            for (int i = 0; i < etudiant.getDate().length() - 1; i++) {
                date.append(etudiant.getDate().charAt(i));
            }
            date_naissance.setText(date.toString());
            TextView etat;
            etat = view.findViewById(R.id.etat);
            String etat_afficher = congeAcademique.getEtat_demande().toString();
            switch (congeAcademique.getEtat_demande().toString()) {
                case "valide":
                    etat_afficher = getResources().getString(R.string.valide);
                    break;
                case "refuse":
                    etat_afficher = getResources().getString(R.string.refuse);
                    break;
                case "nonTraite":
                    etat_afficher = getResources().getString(R.string.non_traite);
                    break;
            }
            etat.setText(etat_afficher);


            final ImageView image = view.findViewById(R.id.image);
            final PhotoView wallpaper = view.findViewById(R.id.wallpaper);
            Bitmap bitmap = BitmapFactory.decodeByteArray(congeAcademique.getFichier(),
                    0, congeAcademique.getFichier().length);
            image.setImageBitmap(bitmap);
            wallpaper.setImageBitmap(bitmap);

            final ImageView zoomIn, zoomOUT, download, delete;
            zoomIn = view.findViewById(R.id.zoom);
            zoomOUT = view.findViewById(R.id.zoomOUT);
            download = view.findViewById(R.id.download);
            delete = view.findViewById(R.id.supprimer);

            final CardView cardView = view.findViewById(R.id.cardview);
            final ConstraintLayout top = view.findViewById(R.id.top);
            final ConstraintLayout options = view.findViewById(R.id.options_plus);

            zoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomIn.setVisibility(View.GONE);
                    zoomOUT.setVisibility(View.VISIBLE);
                    wallpaper.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    // garder la boutons au dessous avec une arrière plan 25% transparente
                    options.setAlpha((float) 0.64);
                    cardView.setVisibility(View.GONE);
                    top.setVisibility(View.GONE);
                }
            });

            zoomOUT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomIn.setVisibility(View.VISIBLE);
                    zoomOUT.setVisibility(View.GONE);
                    wallpaper.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    // garder la boutons au dessous avec une arrière plan 25% transparente
                    options.setAlpha(1);
                    cardView.setVisibility(View.VISIBLE);
                    top.setVisibility(View.VISIBLE);
                }
            });

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadJustification(congeAcademique.getFichier());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteCongeAcademique();
                }
            });

        } else {
            //--------------------------------------upload d'image----------------------------------------
            demande_existe.setVisibility(View.GONE);
            // récupération depuis layout
            // bouttons
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
//                    wait.setVisibility(View.VISIBLE);
                    progress_circular.setVisibility(View.VISIBLE);

                    viewUploading();
                }
            });
        }

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
        if (Objects.requireNonNull(uri.getScheme()).equals(ContentResolver.SCHEME_CONTENT)) {
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
                // appel la méthode qui stock la justification du congé dans la BD via retrofit
                UploadCongeAcademiqueFile();
            }
        }, 100);
    }

    //la méthode qui stock la justification du congé dans la BD via retrofit
    @SuppressLint("SetTextI18n")
    private void UploadCongeAcademiqueFile() {
        final Dot_CongeAcademique_Android congeAcademique = new Dot_CongeAcademique_Android(etudiant.getId_utilisateur(), image_bytesArray, extension);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/upload/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        final Call<ResponseBody> call = api.UploadCongeAcademiqueFile(congeAcademique, "Bearer " + token);
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
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            GetDemandeOfEtudiant();
                        } else {
                            viewAfterFail("other");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        viewAfterFail("network");
                    }
                });
            }
        }, 1);

    }

    // l'affichage si le stockage de la justification du congé échoue
    @SuppressLint("SetTextI18n")
    private void viewAfterFail(String cause) {
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

    /* la récupération de conge depuis la base de données */
    private void GetDemandeOfEtudiant() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        Call<CongeAcademique> call = api1.GetDemandeOfEtudiant(etudiant.getId_utilisateur(), "Bearer " + token);
        call.enqueue(new Callback<CongeAcademique>() {
            @Override
            public void onResponse(@NonNull Call<CongeAcademique> call, @NonNull Response<CongeAcademique> response) {
                if (response.isSuccessful()) {
                    CongeAcademique congeAcademique = response.body();
                    loginPreferenceConfig.setCongeAcademique(congeAcademique);
                    message = getResources().getString(R.string.conge_ok);
                    etat = "ok";
                    refresh(message, etat);
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "ok";
                    refresh(message, etat);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CongeAcademique> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "ok";
                refresh(message, etat);
            }
        });
    }

    private void DeleteCongeAcademique() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/delete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.DeleteCongeAcademique(congeAcademique.getNumero_conge_academique(), "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loginPreferenceConfig.setCongeAcademique(null);
                    message = getResources().getString(R.string.conge_delete_ok);
                    etat = "ok";
                    refresh(message, etat);
                } else {
                    message = getResources().getString(R.string.erreur_not_succ);
                    etat = "ok";
                    refresh(message, etat);
                }
            }

            @SuppressLint("NewApi")
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                message = getResources().getString(R.string.network);
                etat = "ok";
                refresh(message, etat);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void downloadJustification(byte[] image) {
        String save;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_PERMISSION = 1;
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        }
        Toast.makeText(getActivity(), getResources().getString(R.string.start_downloading),
                Toast.LENGTH_SHORT).show();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        save = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getActivity()).getContentResolver(), bitmap, "", "");
        if (!save.equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.completed_download),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.cant_download),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void refresh(String message, String etat) {
        EtudiantFragmentAcademicLeave fragmentAcademicLeave = new EtudiantFragmentAcademicLeave();
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        bundle.putSerializable("etat", etat);
        fragmentAcademicLeave.setArguments(bundle);
        @SuppressLint({"NewApi", "LocalSuppress"})
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentAcademicLeave);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}






