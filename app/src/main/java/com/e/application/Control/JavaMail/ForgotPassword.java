package com.e.application.Control.JavaMail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.application.API.API;
import com.e.application.Control.Login;
import com.e.application.R;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPassword extends AppCompatActivity {

    TextView title, message;
    EditText email, pass, conf_pass, code;
    Button reset, valide, check_code, login;
    private String code_six;
    private int id;
    RelativeLayout lay_pass, lay_pass_conf, wait;
    ImageView visible_pass, visible_pass_conf, invisible_pass, invisible_pass_conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        title = findViewById(R.id.title1);
        message = findViewById(R.id.msg);
        email = findViewById(R.id.email);
        code = findViewById(R.id.code);
        pass = findViewById(R.id.pass);
        conf_pass = findViewById(R.id.pass_conf);
        reset = findViewById(R.id.reset);
        check_code = findViewById(R.id.check_code);
        valide = findViewById(R.id.valider);
        login = findViewById(R.id.login);
        lay_pass = findViewById(R.id.lay_pass);
        lay_pass_conf = findViewById(R.id.lay_pass_conf);
        visible_pass = findViewById(R.id.visible_pass);
        visible_pass_conf = findViewById(R.id.visible_pass_conf);
        invisible_pass = findViewById(R.id.invisible_pass);
        invisible_pass_conf = findViewById(R.id.invisible_pass_conf);
        wait = findViewById(R.id.wait);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  envoyer l'email
                wait.setVisibility(View.VISIBLE);
                checkUserByEmail(email.getText().toString());
            }
        });

        check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = findViewById(R.id.code);
                checkCode(code.getText().toString());

            }
        });
        valide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getText().toString().equals(conf_pass.getText().toString())) {
                    if (pass.length() >= 6) {
                        wait.setVisibility(View.VISIBLE);
                        updatePassword(pass.getText().toString());
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText(getResources().getString(R.string.error_lenght));
                    }
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.wrong_password));
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();

            }
        });

        visible_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible_pass.setVisibility(View.GONE);
                visible_pass_conf.setVisibility(View.GONE);
                invisible_pass.setVisibility(View.VISIBLE);
                invisible_pass_conf.setVisibility(View.VISIBLE);
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                conf_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            }
        });
        visible_pass_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible_pass.setVisibility(View.GONE);
                visible_pass_conf.setVisibility(View.GONE);
                invisible_pass.setVisibility(View.VISIBLE);
                invisible_pass_conf.setVisibility(View.VISIBLE);
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                conf_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        invisible_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible_pass.setVisibility(View.VISIBLE);
                visible_pass_conf.setVisibility(View.VISIBLE);
                invisible_pass.setVisibility(View.GONE);
                invisible_pass_conf.setVisibility(View.GONE);
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                conf_pass.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
        invisible_pass_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible_pass.setVisibility(View.VISIBLE);
                visible_pass_conf.setVisibility(View.VISIBLE);
                invisible_pass.setVisibility(View.GONE);
                invisible_pass_conf.setVisibility(View.GONE);
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                conf_pass.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }


    // la récupération des informations d'enseignant
    public void checkUserByEmail(final String email) {
        String url = "http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/auth/";
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api1 = retrofit1.create(API.class);
        final Call<Integer> call = api1.CheckUserByEmail(email);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.isSuccessful()) {
                    wait.setVisibility(View.GONE);
                    if (response.body() != null) {

                        id = response.body();
                        message.setVisibility(View.GONE);
                        sendMail();
                        setViewCode();
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText(getResources().getString(R.string.wrong_email));
                    }
                } else {
                    wait.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.wrong_email));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.network));
            }
        });
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int lenght = 6;
        char tempChar;
        for (int i = 0; i < lenght; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void sendMail() {
        String mail = email.getText().toString();
        code_six = random();
        String message = getResources().getString(R.string.message_mail) + "\n" + code_six + "\n" + "\n" +
                getResources().getString(R.string.ntic_admin);
        String subject = getResources().getString(R.string.password_subject);
        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        javaMailAPI.execute();
    }


    private void setViewCode() {
        title.setText(getResources().getString(R.string.enter_code));
        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mail, 0, R.drawable.icon_checkbox_green, 0);
        email.setInputType(InputType.TYPE_NULL);
        code.setVisibility(View.VISIBLE);
        reset.setVisibility(View.GONE);
        check_code.setVisibility(View.VISIBLE);
    }

    private void checkCode(String code_check) {
        if (code_check.equals(code_six)) {
            message.setVisibility(View.GONE);
            code.setInputType(InputType.TYPE_NULL);
            code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_code, 0, R.drawable.icon_checkbox_green, 0);
            lay_pass.setVisibility(View.VISIBLE);
            lay_pass_conf.setVisibility(View.VISIBLE);
            check_code.setVisibility(View.GONE);
            valide.setVisibility(View.VISIBLE);
            title.setText(getResources().getString(R.string.enter_new_pass));
        } else {
            message.setVisibility(View.VISIBLE);
            message.setText(getResources().getString(R.string.wrong_code));
        }
    }

    private void updatePassword(final String password) {
        message.setVisibility(View.GONE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + getResources().getString(R.string.ip) + ":8080/SmartUniversity-API/api/auth/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.UpdatePassword(id, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                wait.setVisibility(View.GONE);
                assert response.body() != null;
                if (response.isSuccessful()) {
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_NULL);
                    conf_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_NULL);
                    visible_pass.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkbox_green));
                    invisible_pass.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkbox_green));
                    visible_pass_conf.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkbox_green));
                    invisible_pass_conf.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkbox_green));
                    title.setText(getResources().getString(R.string.update_password_succ));
                    valide.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.erreur_not_succ));
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                wait.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(getResources().getString(R.string.network));
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}

//ouadenaymen@gmail.com