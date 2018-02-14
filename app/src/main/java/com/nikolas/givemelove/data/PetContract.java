package com.nikolas.givemelove.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nikolas on 24/2/2017.
 */

public final class PetContract {

    public final String LOG_TAG = PetContract.class.getSimpleName();

    /*** Constant Values we need to create the content URI ***/
    // Content Authority is the name for entire content provider, is the package name of the App witch is unique on the device
    public static final String CONTENT_AUTHORITY = "com.nikolas.givemelove";
    // The URI the apps use to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible path to append the URI
    public static final String PATH_PET_TABLE = "pets";

    // To prevent someone accidentally instantiate this class we give an empty constructor
    private PetContract(){}

    /**
     *  Inner class that defines constant values for the pets database.
     *  Each entry in the table represents a single pet.
     */
    public static final class PetEntry implements BaseColumns{

        // Glue all URI parts together...
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PET_TABLE);

        // The MIME type of the {@link CONTENT_URI} for a list of pets
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PET_TABLE;
        // The MIME type of the {@link CONTENT_URI} for a single pet
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PET_TABLE;

        // Name of database table
        public static final String TABLE_NAME = "pets";

        // Unique id number for pets.Type INTEGER.
        // Only use in the database.
        public static final String _ID = BaseColumns._ID;

        // Cat or Dog?
        public static final String COLUMN_PET_CAT_OR_DOG = "cat_or_dog";

        // Name of the pet. Type TEXT.
        public static final String COLUMN_PET_NAME = "name";

        // Breed of the pet. Type TEXT.
        public static final String COLUMN_PET_BREED = "breed";

        // Gender of the pet. Type INTEGER.
        // Possible values: {@link #GENDER_UNKNOWN) , {@link #GENDER_MALE} , {@link #GENDER_FEMALE}
        public static final String COLUMN_PET_GENDER = "gender";

        // Weight of the pet. Type INTEGER > 0.
        public static final String COLUMN_PET_WEIGHT = "weight";
        // Values of pet gender.
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        // Values Cat or Dog
        public static final int PET_UNKNOWN = 0;
        public static final int PET_CAT = 1;
        public static final int PET_DOG = 2;

        /****    METHODS TO CHECK IF THE VALUES THAT INSERTED AT THE DATABASE ARE VALID    ****/
        // Check if catOrDog value is valid
        public static boolean catOrDogIsValid(int catOrDog){
            if (catOrDog == PET_UNKNOWN || catOrDog == PET_CAT || catOrDog == PET_DOG) {
                return true;
            }
            return false;
        }
        // Check is pet's name is valid
        public static boolean nameIsValid(String name) {
            if (name != null && !name.isEmpty()) {
                return true;
            }
            return false;
        }
        // Check if breed is valid
        public static boolean breedIsValid(String breed) {
            if (breed != null && !breed.isEmpty()) {
                return true;
            }
            return false;
        }
        // Check if gender value is valid
        public static boolean genderIsValid(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
        // Check if weight is valid
        public static boolean weightIsValid(int weight) {
            if (weight >= 0) {
                return true;
            }
            return false;
        }
    }
}
