package com.example.lab_28_vasilev_403_apispectr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.lab_28_vasilev_403_apispectr.Model.ChemElement;
import com.example.lab_28_vasilev_403_apispectr.Model.SpecLine;

public class LocalDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_wlen_min = "wlen_min";
    public static final String COLUMN_wlen_max = "wlen_max";

    public static final String CREATE_TABLE = "CREATE TABLE table_spectr (" +
            COLUMN_wlen_min + " REAL, " +
            COLUMN_wlen_max + " REAL)";


    public LocalDB(Context context){
        super(context, "Spectr.db", null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS table_spectr");
        onCreate(db);
    }

    @SuppressLint("Range")
    public static void CursorFromLoad(SpectraView spectraView, Cursor cursor)
    {
        if(cursor != null) {
            spectraView.wlen_min = cursor.getFloat(cursor.getColumnIndex(COLUMN_wlen_min));
            spectraView.wlen_max = cursor.getFloat(cursor.getColumnIndex(COLUMN_wlen_max));

        }
    }

    public static ContentValues getContentValues(SpectraView spectraView){
        ContentValues values = new ContentValues();
        values.put(COLUMN_wlen_min, spectraView.wlen_min);
        values.put(COLUMN_wlen_max, spectraView.wlen_max);
        return values;
    }
}
