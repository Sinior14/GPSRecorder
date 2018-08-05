package com.example.sinior.gpsrecorderv3.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PointSQLite extends SQLiteOpenHelper {

    private static final String TABLE_PROGRAMMES = "Points";
    private static final String COL_ID = "Id";
    private static final String COL_At = "latitude";
    private static final String COL_Lg = "longitude";
    private static final String COL_createDate = "createDate";
    private static final String COL_Etat = "etat";


    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_PROGRAMMES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_At + " TEXT NOT NULL, "+ COL_Lg + " TEXT NOT NULL, "
            + COL_createDate + " TEXT NOT NULL, "+ COL_Etat + " TEXT NOT NULL);";

    public PointSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_PROGRAMMES + ";");
        onCreate(db);
    }

}