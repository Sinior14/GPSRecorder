package com.example.sinior.gpsrecorderv3.BDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.SQL.PointSQLite;

import java.util.ArrayList;

public class PointsBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "points.db";
    private static final String TABLE_PointS = "Points";
    private static final String COL_ID = "Id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_At = "latitude";
    private static final int NUM_COL_At = 1;
    private static final String COL_Lg = "longitude";
    private static final int NUM_COL_Lg = 2;
    private static final String COL_createDate = "createDate";
    private static final int NUM_COL_createDate = 3;
    private static final String COL_Etat = "etat";
    private static final int NUM_COL_Etat = 4;

    private SQLiteDatabase bdd;

    private PointSQLite maBaseSQLite;

    public PointsBDD(Context context) {
        //On crée la BDD et sa table
        maBaseSQLite = new PointSQLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open() {
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public long insertPoint(Point Pt) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_At, Pt.getAtitude());
        values.put(COL_Lg, Pt.getLongtude());
        values.put(COL_createDate, Pt.getCreateDate());
        values.put(COL_Etat, Pt.getStat());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_PointS, null, values);
    }

    public int updatePoint(int id, Point Pt) {
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_At, Pt.getAtitude());
        values.put(COL_Lg, Pt.getLongtude());
        values.put(COL_createDate, Pt.getCreateDate());
        values.put(COL_Etat, Pt.getStat());
        return bdd.update(TABLE_PointS, values, COL_ID + " = " + id, null);
    }

    public int removePointWithID(int id) {
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_PointS, COL_ID + " = " + id, null);
    }

    public int removeAllPoints() {
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_PointS, null, null);
    }

    public Point getPointWithNom(String Nom) {
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(TABLE_PointS, new String[]{COL_ID, COL_At, COL_Lg, COL_createDate, COL_Etat}, COL_Etat + " LIKE \"" + Nom + "\"", null, null, null, null);
        return cursorToPoint(c);
    }

    public Point getPointWithID(int ID) {
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(TABLE_PointS, new String[]{COL_ID, COL_At, COL_Lg, COL_createDate, COL_Etat}, COL_ID + " LIKE \"" + ID + "\"", null, null, null, null);
        return cursorToPoint(c);
    }

    //Cette méthode permet de convertir un cursor en un livre
    private Point cursorToPoint(Cursor c) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Point Pt = new Point();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        Pt.setId(c.getInt(NUM_COL_ID));
        Pt.setAtitude(c.getString(NUM_COL_At));
        Pt.setLongtude(c.getString(NUM_COL_Lg));
        Pt.setCreateDate(c.getString(NUM_COL_createDate));
        Pt.setStat(c.getString(NUM_COL_Etat));
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return Pt;
    }

    // Getting All Contacts
    public ArrayList<Point> getAllPoint() {
        ArrayList<Point> etudiantList = new ArrayList<Point>();
        // Select All Query
        Cursor c = bdd.query(TABLE_PointS, new String[]{COL_ID, COL_At, COL_Lg, COL_createDate, COL_Etat}, null, null, null, null, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Point Pt = new Point();
                Pt.setId(c.getInt(NUM_COL_ID));
                Pt.setAtitude(c.getString(NUM_COL_At));
                Pt.setLongtude(c.getString(NUM_COL_Lg));
                Pt.setCreateDate(c.getString(NUM_COL_createDate));
                Pt.setStat(c.getString(NUM_COL_Etat));
                //Adding contact to list
                etudiantList.add(Pt);
            } while (c.moveToNext());
        }

        // return contact list
        return etudiantList;
    }

    // Getting All Contacts
    public Point getLastPoint() {
        Point Pt = new Point();
        // Select All Query
        Cursor c = bdd.query(TABLE_PointS, new String[]{COL_ID, COL_At, COL_Lg, COL_createDate, COL_Etat}, null, null, null, null, null);

        // looping through all rows and adding to list
        if (c.moveToLast()) {

            Pt.setId(c.getInt(NUM_COL_ID));
            Pt.setAtitude(c.getString(NUM_COL_At));
            Pt.setLongtude(c.getString(NUM_COL_Lg));
            Pt.setCreateDate(c.getString(NUM_COL_createDate));
            Pt.setStat(c.getString(NUM_COL_Etat));
        }

        // return contact list
        return Pt;
    }
}