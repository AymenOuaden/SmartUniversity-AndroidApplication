package com.e.application.Control.OfflineMode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(int id_etudiant, int id_enseignant, String date_absence, String jour_seance, String heure_seance) {

        SQLiteDatabase database = getWritableDatabase();

        String sql = "INSERT INTO ABSENCE VALUES (?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, id_etudiant);
        statement.bindDouble(2, id_enseignant);
        statement.bindString(3, date_absence);
        statement.bindString(4, jour_seance);
        statement.bindString(5, heure_seance);

        statement.executeInsert();
    }


    public void deleteData(AbsenceSQL absenceSQL) {

        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM Absence WHERE id_etudiant = ? and jour_seance = ? and heure_seance = ? and date_absence = ? ";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) absenceSQL.getId_etudiant());
        statement.bindString(2, absenceSQL.getJour_seance());
        statement.bindString(3, absenceSQL.getHeure_seance());
        statement.bindString(4, absenceSQL.getDate_absence());

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
