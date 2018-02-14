package com.nikolas.givemelove;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nikolas.givemelove.data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int PET_ID_LOADER = 0;

    /**
     *  mNameEditText  - EditTextField for Pet Name
     *  mBreedEditText  - EditTextField for Pet Breed
     *  mWeightEditText  - EditTextField for Pet Weight
     *  mGenderSpinner  - Spinner for Pet Gender
     *  mCatOrDogSpinner - Spinner to choose either cat or dog
     */
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private Spinner mCatOrDogSpinner;

    // Cat or Dog - Possible Values: 0 - Unknown, 1 - Cat, 2 - Dog
    private int mCatOrDog;
    // Pet Gender - Possible Values: 0 - Unknown, 1 - Male, 2 - Female
    private int mGender;

    // Content URI for the existing pet (null if it's a new pet)
    private Uri mCurrentPetUri;

    // Check if user has changed one part of the form
    private boolean mPetHasChanged = false;

    // Set up an OnTouchListener to be notified if there are changes on the fields
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get the intent was used to launch the activity.
        Intent intent = getIntent();
        // Then the Uri passed from this intent so we perform the required action.
        mCurrentPetUri = intent.getData();

        if (mCurrentPetUri == null) {
            setTitle(R.string.editor_activity_title_add);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        else {
            setTitle(R.string.editor_activity_title_edit);
            // Initialize the loader
            /* !!!!! An Error that I had made... !!!!!
             * I had initialized the loader at the end of the method.
             * When add a new pet there is no data to load.
             */
            getLoaderManager().initLoader(PET_ID_LOADER, null, this);
        }

        mNameEditText = (EditText)findViewById(R.id.edit_name);
        mBreedEditText = (EditText)findViewById(R.id.edit_breed);
        mWeightEditText = (EditText)findViewById(R.id.edit_weight);
        mGenderSpinner = (Spinner)findViewById(R.id.spinner_gender);
        mCatOrDogSpinner = (Spinner)findViewById(R.id.spinner_cat_or_dog);

        mCatOrDogSpinner.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);

        setupCatOrDogSpinner();
        setupGenderSpinner();
    }

    // SetUp the dropdown Spinner that allows the user to select cat or dog
    private void setupCatOrDogSpinner() {
        // Create the Adapter for the Spinner
        // We will use a String Array and the default layout
        ArrayAdapter catOrDogSpinnerArrayAdapter = ArrayAdapter.createFromResource
                (this, R.array.cat_or_dog_selection, android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCatOrDogSpinner.setAdapter(catOrDogSpinnerArrayAdapter);

        // Set the integer mGender to the constant values
        mCatOrDogSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)) {
                    if(selection.equals(getString(R.string.cat))){
                        mCatOrDog = PetEntry.PET_CAT; //Cat
                    }
                    else if(selection.equals(getString(R.string.dog))){
                        mCatOrDog = PetEntry.PET_DOG; //Dog
                    }
                    else {
                        mCatOrDog = PetEntry.PET_UNKNOWN; //Unknown
                    }
                }
            }
            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCatOrDog = 0; // Unknown
            }
        });
    }

    // SetUp the dropdown Spinner that allows the user to select the gender
    private void setupGenderSpinner() {
        // Create the Adapter for the Spinner
        // We will use a String Array and the default layout
        ArrayAdapter genderSpinnerArrayAdapter = ArrayAdapter.createFromResource
                (this, R.array.array_gender_options, android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerArrayAdapter);

        // Set the integer mGender to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)) {
                    if(selection.equals(getString(R.string.gender_male))){
                        mGender = PetEntry.GENDER_MALE; //Male
                    }
                    else if(selection.equals(getString(R.string.gender_female))){
                        mGender = PetEntry.GENDER_FEMALE; //Female
                    }
                    else {
                        mGender = PetEntry.GENDER_UNKNOWN; //Unknown
                    }
                }
            }
            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If there is a new pet hide the delete button
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu options from res/menu/menu_editor.xml file.
        // This adds menu items to the appBar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Add new pet to the database
                savePet();
                // Exit the activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Create a click listener to handle that user want to continue
                DialogInterface.OnClickListener deleteDialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the delete button
                        // Delete the selected Pet
                        deletePet();
                        // Exit the activity
                        finish();
                    }
                };
                // Show warning dialog
                showDeleteWarningDialog(deleteDialogListener);
                return true;
            // Respond to a click on the "Home" button
            case android.R.id.home:
                // Check if any change has been made and if not continue...
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
                // Otherwise warn the user
                // Create an onClickListener to handle user response
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked discard button
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show warning dialog that there is unsaved data
                showUnsavedWaringDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePet() {
        // The uri of the pet
        Uri uri = mCurrentPetUri;
        // Check if is in edit mode
        if (mCurrentPetUri != null) {
            // The selection string for WHERE clause
            String selection = PetEntry._ID;
            // Selection Args
            String[] selectionArgs = {String.valueOf(ContentUris.parseId(uri))};
            // Delete the selected pet calling the {@link PetProvider#delete} method and get the number of rows deleted
            int rowDeleted = getContentResolver().delete(uri, selection, selectionArgs);
            // Display toast message depending on whether or not the delete was successful
            if (rowDeleted == 0) {
                // Delete failed
                Toast.makeText(this, R.string.delete_pet_failed, Toast.LENGTH_LONG).show();
            }
            else {
                // Otherwise delete was successful
                Toast.makeText(this, R.string.delete_pet_succeed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void savePet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        int catOrDogInt = mCatOrDog;
        String namePetString = mNameEditText.getText().toString().trim();
        String breedPetString = mBreedEditText.getText().toString().trim();
        String weightPetString = mWeightEditText.getText().toString().trim();
        int genderInt = mGender;
        int weightInt;
        /*  If the weightPetString is empty it throws an exception...
         *  So set the weight to "0" as default...
         *  and then surround the action with try-catch block.
         */
        if (TextUtils.isEmpty(weightPetString)) {
            weightInt = 0;
        }
        else {
            weightInt = Integer.parseInt(weightPetString);
        }
        // Create a ContentValues Object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_PET_CAT_OR_DOG, catOrDogInt);
        contentValues.put(PetEntry.COLUMN_PET_NAME, namePetString);
        contentValues.put(PetEntry.COLUMN_PET_BREED, breedPetString);
        contentValues.put(PetEntry.COLUMN_PET_GENDER, genderInt);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT, weightInt);


        try {
            if (mCurrentPetUri == null){
                // Insert a new pet by calling {@link PetProvider#insert()} method
                Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, contentValues);

                // Display toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, R.string.saved_failed, Toast.LENGTH_LONG).show();
                }
                else {
                    // Otherwise the insertion was successful.
                    Toast.makeText(this, R.string.saved_success, Toast.LENGTH_LONG).show();
                }
            }
            else {
                // Update an existing pet by using {@link PetProvider#update()} method
                String selection = PetEntry._ID + "=?";
                String[] selectionArgs = {String.valueOf(ContentUris.parseId(mCurrentPetUri))};
                int rowsUpdated = getContentResolver().update(mCurrentPetUri, contentValues, selection, selectionArgs);

                // Display toast message depending on whether the update was successful
                if (rowsUpdated == 0) {
                    // If there are no rows updated then update failed
                    Toast.makeText(this, R.string.update_failed, Toast.LENGTH_LONG).show();
                }
                else {
                    // Otherwise update was successful
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_LONG).show();
                }
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        // The Editor uses all the attributes.
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_CAT_OR_DOG,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        // Return the Loader
        return new CursorLoader(this,   // CONTEXT:         The activity context
                mCurrentPetUri,         // URI:             The URI to load data from
                projection,             // PROJECTION:      Specify witch columns will be returned
                null,                   // SELECTION:       Filter witch rows will be returned (WHERE clause)
                null,                   // SELECTION ARGS:  The values for the selection
                null);                  // SHORT ORDER:     The order the entries will be shown
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Get the data from the Cursor and update the values shown on the Editor screen.
        // Moving to th first row of cursor.
        if(cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int catOrDogColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_CAT_OR_DOG);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            int catOrDogValue = cursor.getInt(catOrDogColumnIndex);
            String nameValue = cursor.getString(nameColumnIndex);
            String breedValue = cursor.getString(breedColumnIndex);
            int genderValue = cursor.getInt(genderColumnIndex);
            int weightValue = cursor.getInt(weightColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(nameValue);
            mBreedEditText.setText(breedValue);
            mWeightEditText.setText(String.valueOf(weightValue));

            /* CatOrDog and Gender are dropdown spinners...
             * So map the constant value from the database into one of the dropdown options.
             * Then call setSelection() so that option is displayed on screen as the current selection.
             */
            // For CatOrDog
            switch (catOrDogValue){
                case PetEntry.PET_CAT:
                    mCatOrDogSpinner.setSelection(1);
                    break;
                case PetEntry.PET_DOG:
                    mCatOrDogSpinner.setSelection(2);
                    break;
                default:
                    mCatOrDogSpinner.setSelection(0);
                    break;
            }
            // For Gender
            switch (genderValue){
                case PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear all fields
        mCatOrDogSpinner.setSelection(0);
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText("");
    }

    // Create warning dialogs
    /**This code makes a AlertDialog using the AlertDialogBuilder.
     * The method accepts a OnClickListener for the discard button.
     * We do this because the behavior for clicking back or up is a little bit different.
     */
    // For unsaved pet
    public void showUnsavedWaringDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create alert dialog builder and set the message
        // Set click listeners for positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_unsaved_dialog);
        builder.setPositiveButton(R.string.warning_unsaved_positive, discardButtonClickListener);
        builder.setNegativeButton(R.string.warning_unsaved_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User click "keep editing" button, so dismiss the dialog and continue editing the pet
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        // Create and show the warning dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // For delete a pet
    private void showDeleteWarningDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        // Create alert dialog builder and set the message
        // Set click listeners for positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_delete_pet_dialog);
        builder.setPositiveButton(R.string.warning_delete_pet_positive, deleteButtonClickListener);
        builder.setNegativeButton(R.string.warning_delete_pet_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User click "keep editing" button, so dismiss the dialog and continue editing the pet
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        // Create and show the warning dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        // If the user hadn't done any change continue...
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise set up the warning dialog
        // Create a click listener to handle that user want to continue
        DialogInterface.OnClickListener discardButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked discard button, close current activity
                finish();
            }
        };
        // Show the dialog
        showUnsavedWaringDialog(discardButtonListener);
    }
}