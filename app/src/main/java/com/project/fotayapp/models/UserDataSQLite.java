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

    //Tabla fotos
    private static final String TABLE_PHOTO = "fotos";

    // Login Table Columns names
    private static final String SQL_ID = "usu_id";
    private static final String SQL_NAME = "usu_nombre";
    private static final String SQL_PHOTO_ID = "foto_id";
    private static final String SQL_PHOTO_FECHA = "foto_fecha";
    private static final String SQL_PHOTO_COMENT = "foto_coment";
    private static final String SQL_PHOTO_RUTA = "foto_ruta";

    public UserDataSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + SQL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + SQL_NAME + " TEXT UNIQUE" + ")";

        String CREATE_PHOTO_TABLE = "CREATE TABLE " + TABLE_PHOTO + "("
                + SQL_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SQL_NAME + " TEXT UNIQUE," + SQL_PHOTO_FECHA + " TEXT," + SQL_PHOTO_COMENT + " TEXT," + SQL_PHOTO_RUTA + " TEXT" + ")";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_PHOTO_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUserTableUsuarios(String usu_nombre) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQL_NAME, usu_nombre); // Nombre de usuario


        // Inserting Row in users table and storing return id of that row
        long id = db.insert(TABLE_USER, null, values);
        //db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void updateUserTableUsuarios(String usu_nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQL_NAME, usu_nombre); // Nombre de usuario
        db.update(TABLE_USER, values, SQL_NAME + " = ?", new String[]{usu_nombre});
        db.close();
    }

    public void addUserTableFotos(String usu_nombre, String foto_fecha, String foto_coment, String foto_ruta) {
        // NEED TO LOOP THROUGH THE ARRAYLIST AND INSERT INTO THE DATABASE
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQL_NAME, usu_nombre); // Nombre de usuario
        values.put(SQL_PHOTO_FECHA, foto_fecha); // Fecha de la foto
        values.put(SQL_PHOTO_COMENT, foto_coment); // Comentario de la foto
        values.put(SQL_PHOTO_RUTA, foto_ruta); // Ruta de la foto

        // Inserting Row in users table and storing return id of that row
        long id = db.insert(TABLE_PHOTO, null, values);
        //db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    // Obtener nombe de usuario desde la base de datos SQLite
    public HashMap<String, String> getUserName() {
        HashMap<String, String> user_sqlite = new HashMap<String, String>();

        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_sqlite.put("usu_nombre", cursor.getString(1));
        }
        cursor.close();
        db.close();

        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user_sqlite.toString());

        return user_sqlite;
    }

    public int getPhotoCount() {
        int count = 0;
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_PHOTO + " WHERE usu_nombre  = '" + SQL_NAME + "'";
        //String selectQuery = "SELECT * FROM " + TABLE_PHOTO;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    /**
     * Eliminar datos de la base de datos al cerrar sesión
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_PHOTO, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


}