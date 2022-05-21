package com.project.fotayapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class UserDataSQLite extends SQLiteOpenHelper {

    private static final String TAG = UserDataSQLite.class.getSimpleName();

    private static final int DATABASE_VERSION = 10;

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "fotay_db_sqlite";

    // Tabla usuarios
    private static final String TABLE_USER = "usuarios";


    // Login Table Columns names
    public static String SQL_ID = "usu_id";
    public static String SQL_NAME = "usu_nombre";

    //Contexo de la clase para acceder a la base de datos desde otras clases
    public UserDataSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Crear tabla usuarios
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + SQL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + SQL_NAME + " TEXT " + ")";

        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tabla anterior si existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Volver a crear la tabla
        onCreate(db);
    }

    /**
     * Guardar datos de usuario en la base de datos
     */
    public void addUserTableUsuarios(String usu_id, String usu_nombre) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQL_ID, usu_id); // Id de usuario
        values.put(SQL_NAME, usu_nombre); // Nombre de usuario

        // Insertar la fila en la tabla
        long row_id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + row_id);
    }

    //Actualizar nombre de usuario en la base de datos SQLite
    public void updateUserTableUsuarios(String newUsername, String usu_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQL_NAME, newUsername); // Nombre de usuario
        String[] args = {usu_id};
        db.update(TABLE_USER, values, SQL_ID + "=?", args);
        db.close();
    }

    // Obtener nombe de usuario desde la base de datos SQLite para las clases que lo necesiten
    public HashMap<String, String> getUserInfo() {
        HashMap<String, String> user_sqlite = new HashMap<String, String>();

        String selectQuery = "SELECT " + SQL_ID + "," + SQL_NAME + " FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        assert cursor != null;
        // Asegurarse de que el cursor tiene al menos una fila de datos
        if (cursor != null && cursor.moveToFirst()) {
            user_sqlite.put("usu_id", cursor.getInt(0) + "");
            user_sqlite.put("usu_nombre", cursor.getString(1));
        }

        cursor.close();
        //db.close();

        //Log los datos del usuario
        Log.d(TAG, "Fetching user from Sqlite: " + user_sqlite.toString());

        return user_sqlite;
    }

    /**
     * Eliminar datos de la base de datos al cerrar sesión
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}