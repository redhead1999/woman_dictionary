package com.redhead.y14.womandictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "databs.db";
    private static String DB_PATH = "file:///android_asset/databs.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = this.myContext.getDatabasePath(DB_NAME).toString();
    }

    public void createDataBase() throws IOException {
        if (!checkDataBase()) {
            getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException unused) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, 1);
        } catch (SQLiteException unused) {
        }
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
        if (sQLiteDatabase != null) {
            return true;
        }
        return false;
    }

    private void copyDataBase() throws IOException {
        InputStream open = this.myContext.getAssets().open(DB_NAME);
        FileOutputStream fileOutputStream = new FileOutputStream(DB_PATH);
        byte[] bArr = new byte[1024];
        while (true) {
            int read = open.read(bArr);
            if (read > 0) {
                fileOutputStream.write(bArr, 0, read);
            } else {
                fileOutputStream.flush();
                fileOutputStream.close();
                open.close();
                return;
            }
        }
    }

    public void openDataBase() throws SQLException {
        this.myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, 1);
    }

    public synchronized void close() {
        if (this.myDataBase != null) {
            this.myDataBase.close();
        }
        super.close();
    }
}
