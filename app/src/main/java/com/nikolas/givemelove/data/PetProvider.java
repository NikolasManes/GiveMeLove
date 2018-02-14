package com.nikolas.givemelove.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nikolas.givemelove.R;
import com.nikolas.givemelove.data.PetContract.PetEntry;

/**
 * Created by Nikolas on 9/3/2017.
 */

public class PetProvider extends ContentProvider {

    private final String LOG_TAG = PetProvider.class.getSimpleName();
    // The code that is returned whether we need to access the whole table or a single row
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    // PetDbHelper object
    private PetDbHelper mPetDbHelper;

    // URI Matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // We add all the URI that should be recognized by the ContentProvider
        // URI matcher code for the whole table
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PET_TABLE, PETS);
        // URI matcher code for a single row
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PET_TABLE + "/#", PET_ID);
    }
    @Override
    public boolean onCreate() {
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Create a database object
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(
                        PetEntry.TABLE_NAME,    // The table to query
                        projection,             // The columns to return
                        null,                   // The columns for the WHERE clause
                        null,                   // The values for the WHERE clause
                        null,                   // Don't group the rows
                        null,                   // Don't filter by row groups
                        null);                  // The sort order
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query... Unknown URI: " + uri);
        }
        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data of that URI changes then the Cursor must be updated.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Cannot insert pet... URI: " + uri);
        }
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PETS:
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0){
                    // Notify all listeners that the URI has changed.
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to delete. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0){
                    // Notify all listeners that the URI has changed.
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Delete not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        // VALUES
        int catOrDog = values.getAsInteger(PetEntry.COLUMN_PET_CAT_OR_DOG);
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        int gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        /****** SANITY CHECK ******/
        // Check if catOrDog value is valid (1, 2, 0)
        if (!PetEntry.catOrDogIsValid(catOrDog)) {
            throw new IllegalArgumentException("catOrDog value not valid");
        }
        // Check if the pet name is not null
        if (!PetEntry.nameIsValid(name)) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        // Check if breed is null and then set breed to "Unknown"
        if (!PetEntry.breedIsValid(breed)) {
            values.remove(PetEntry.COLUMN_PET_BREED);
            // When I try to access the string from R.id.breed_unknown I was getting the Integer value
            // So in order to get the actual value I follow that way
            values.put(PetEntry.COLUMN_PET_BREED, getContext().getString(R.string.breed_unknown));
        }
        // Check if the gender value is valid
        if (!PetEntry.genderIsValid(gender)){
            throw new IllegalArgumentException("Gender value not valid");
        }
        // Check first if the pets weight null, and then if is equal or less than zero
        if (!PetEntry.weightIsValid(weight)) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }

        // Get writable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        // Insert a new pet into the pets database table with the given ContentValues
        long newRowID = database.insert(PetEntry.TABLE_NAME, null, values);
        // If the ID is -1 then the insertion failed. Log an error and return null.
        if (newRowID == -1) {
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the URI has changed.
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowID);
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Update the selected pets in the pets database table with the given ContentValues
        // Get a readable database object
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();
        /****** SANITY CHECK ******/
        // Check witch attributes are in the values
        // And then if are valid

        // 1 - CatOrDog
        if (values.containsKey(PetEntry.COLUMN_PET_CAT_OR_DOG)) {
            int catOrDog = values.getAsInteger(PetEntry.COLUMN_PET_CAT_OR_DOG);
            if (!PetEntry.catOrDogIsValid(catOrDog)) {
                throw new IllegalArgumentException("catOrDog value not valid");
            }
            values.put(PetEntry.COLUMN_PET_CAT_OR_DOG, catOrDog);
        }

        // 2 - Name
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (!PetEntry.nameIsValid(name)) {
                throw new IllegalArgumentException("Pet requires a name");
            }
            values.put(PetEntry.COLUMN_PET_NAME, name);
        }

        // 3 - Breed - no need to check so move to next

        // 4 - Gender
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            int gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (!PetEntry.genderIsValid(gender)){
                throw new IllegalArgumentException("gender value not valid");
            }
            values.put(PetEntry.COLUMN_PET_GENDER, gender);
        }

        // 5 - Weight
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (!PetEntry.weightIsValid(weight)) {
                throw new IllegalArgumentException("Weight must be greater than zero");
            }
            values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
        }

        // Return the number of rows that were affected
        int rowsUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            // Notify all listeners that the URI has changed.
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
