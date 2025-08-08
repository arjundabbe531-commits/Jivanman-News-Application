// DBHelper.java
package com.arjundabbe.jivanman.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jivanman.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "mobileno TEXT UNIQUE," +
                        "email TEXT UNIQUE," +
                        "username TEXT UNIQUE," +
                        "password TEXT," +
                        "role TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean registerUser(String name, String mobileno, String emailid, String username, String password, String role) {
        if (checkUserExists(mobileno, emailid, username)) {
            return false;
        }

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("mobileno", mobileno);
        contentValues.put("email", emailid);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("role", role);

        long result = sqLiteDatabase.insert("users", null, contentValues);
        sqLiteDatabase.close();
        return result != -1;
    }

    private boolean checkUserExists(String mobilno, String emailid, String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM users WHERE mobileno = ? OR email = ? OR username = ?",
                new String[]{mobilno, emailid, username}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        sqLiteDatabase.close();
        return exists;
    }

    public boolean updateUser(String username, String newName, String newEmail, String newMobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("email", newEmail);
        values.put("mobileno", newMobile);

        int result = db.update("users", values, "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
    }

}
