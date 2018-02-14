package com.nikolas.givemelove.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nikolas.givemelove.data.PetContract.PetEntry;

/**
 * Created by Nikolas on 24/2/2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public final String LOG_TAG = PetDbHelper.class.getSimpleName();

    // DataBase details
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pets.db";

    // DataBase commands
    public static final String CREATE_DATABASE =
            "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                    + PetEntry._ID + " INTEGER PRIMARY KEY, "
                    + PetEntry.COLUMN_PET_CAT_OR_DOG + " INTEGER, "
                    + PetEntry.COLUMN_PET_NAME + " TEXT, "
                    + PetEntry.COLUMN_PET_BREED + " TEXT, "
                    + PetEntry.COLUMN_PET_GENDER + " INTEGER, "
                    + PetEntry.COLUMN_PET_WEIGHT + " INTEGER)";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
