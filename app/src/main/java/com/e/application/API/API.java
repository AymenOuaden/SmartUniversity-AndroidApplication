package com.e.application.API;

import com.e.application.Dots.Dot_CongeAcademique_Android;
import com.e.application.Dots.Dot_Create_Absence;
import com.e.application.Dots.Dot_Create_ChangementSeance;
import com.e.application.Dots.Dot_Create_QR;
import com.e.application.Dots.Dot_Create_SeanceSupp;
import com.e.application.Dots.Dot_Justification_Android;
import com.e.application.Dots.Dot_Login_User;
import com.e.application.Helpers.AbsenceDepartementResponse;
import com.e.application.Helpers.LoginResponse;
import com.e.application.Helpers.NotificationResponse;
import com.e.application.Helpers.SeanceResponse;
import com.e.application.Model.CongeAcademique;
import com.e.application.Model.Enseignant;
import com.e.application.Model.Etudiant;
import com.e.application.Model.Seance;
import com.e.application.Model.Utilisateur;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    //-------------------------------------- communes ----------------------------------------------
    @POST("auth")
    Call<LoginResponse> AuthenticationService(@Body Dot_Login_User dots_Login_User);

    @GET("user/{id}")
    Call<Utilisateur> GetUserByID(@Path("id") int id, @Header("Authorization") String auth);

    @GET("user")
    Call<Integer> CheckUserByEmail(@Query("email") String email);

    @POST("updateUser")
    Call<LoginResponse> UpdateUser(@Body Utilisateur utilisateur, @Header("Authorization") String auth);


    @POST("updatePassword")
    Call<ResponseBody> UpdatePassword(@Query("id") int id, @Query("password") String pass);

    //------------------------------------- Enseignant----------------------------------------------
    @GET("user/{id}")
    Call<Enseignant> getEnsignant(@Path("id") int id, @Header("Authorization") String auth);


    @GET("seances/full/{id_enseignant}")
    Call<ArrayList<SeanceResponse>> GetSeancesCompleteEnseignant(@Path("id_enseignant") int id,
                                                                 @Header("Authorization") String auth);

    @GET("seances/{id}")
    Call<ArrayList<Seance>> getSeance(@Path("id") int id, @Header("Authorization") String auth);

    @GET("etudiants")
    Call<ArrayList<Etudiant>> getEtudiant(@Query("annee") Etudiant.Annee annee,
                                          @Query("specialite") Etudiant.Specialite specialite,
                                          @Query("groupe") int groupe,
                                          @Header("Authorization") String auth);

    @GET("qr/android")
    Call<Dot_Create_QR> GetCodeQR(@Query("code_seance") String code_seance,
                                  @Header("Authorization") String auth);

    @PUT("absence")
    Call<ResponseBody> createAbsence(@Body Dot_Create_Absence absence,
                                     @Header("Authorization") String auth);

    @GET("notification")
    Call<NotificationResponse> GetNotificationsOfUser(@Query("id_utilisateur") int id_utilisateur,
                                                      @Header("Authorization") String auth);

    @PUT("justification/android")
    Call<ResponseBody> UploadJustificationFile(@Body Dot_Justification_Android justification,
                                               @Header("Authorization") String auth);

    @PUT("seance/supp")
    Call<ResponseBody> CreateSeanceSupp(@Body Dot_Create_SeanceSupp seanceSupp,
                                        @Header("Authorization") String auth);

    @PUT("changementSeance")
    Call<ResponseBody> CreateChangementSenace(@Body Dot_Create_ChangementSeance changementSeance,
                                              @Header("Authorization") String auth);


    @PUT("qr/android")
    Call<ResponseBody> CreateCodeQR(@Body Dot_Create_QR dot_create_qr, @Header("Authorization") String auth);


    @POST("notification/setVue")
    Call<ResponseBody> SetNotificationVue(@Query("id_notification") int id_notification, @Header("Authorization") String auth);

    @DELETE("utilisateur/{id}")
    Call<ResponseBody> supprimer(@Path("id") int id, @Header("Authorization") String auth);

    @DELETE("absence/{numero_absence}")
    Call<ResponseBody> DeleteAbsenceByNumero(@Path("numero_absence") int numero_absence,
                                             @Header("Authorization") String auth);

    @DELETE("justification/android/{numero_absence}/{numero_justification}")
    Call<ResponseBody> DeleteJustification(@Path("numero_absence") int numero_absence,
                                           @Path("numero_justification") int numero_justification,
                                           @Header("Authorization") String auth);

    @DELETE("seance/supp/{code_seance}/{code_seance_supp}")
    Call<ResponseBody> DeleteSeanceSupp(@Path("code_seance") String code_seance,
                                        @Path("code_seance_supp") int code_seance_supp,
                                        @Header("Authorization") String auth);

    @DELETE("changementSeance/{code_seance}")
    Call<ResponseBody> DeleteChangementSeance(@Path("code_seance") String code_seance,
                                              @Header("Authorization") String auth);

    @DELETE("qr/{code_seance}")
    Call<ResponseBody> DeleteCodeQR(@Path("code_seance") String code_seance,
                                    @Header("Authorization") String auth);

    @DELETE("notification")
    Call<ResponseBody> DeleteNotification(@Query("id_notification") int id_notification,
                                          @Header("Authorization") String auth);

    //----------------------------------------------------etudiant---------------------------------------------------------------

    @GET("user/{id}")
    Call<Etudiant> getEtudiant(@Path("id") int id, @Header("Authorization") String auth);

    @GET("seances/etudiant")
    Call<ArrayList<SeanceResponse>> GetSeancesOfEtudiant(@Query("id_etudiant") int id_etudiant,
                                                         @Header("Authorization") String auth);

    @GET("absences/etudiant")
    Call<ArrayList<AbsenceDepartementResponse>> GetAbsencesOfEtudiant(@Query("id_etudiant") int id_etudiant,
                                                                      @Header("Authorization") String auth);

    @GET("demande/etudiant")
    Call<CongeAcademique> GetDemandeOfEtudiant(@Query("id_etudiant") int id_etudiant,
                                               @Header("Authorization") String auth);


    @PUT("congeAcademique/android")
    Call<ResponseBody> UploadCongeAcademiqueFile(@Body Dot_CongeAcademique_Android congeAcademique,
                                                 @Header("Authorization") String auth);


    @DELETE("congeAcademique/android/{numero_conge_academique}")
    Call<ResponseBody> DeleteCongeAcademique(@Path("numero_conge_academique") int numero_conge_academique,
                                             @Header("Authorization") String auth);




}
