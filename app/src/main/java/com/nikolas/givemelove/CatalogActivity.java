package com.nikolas.givemelove;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nikolas.givemelove.data.PetContract.PetEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int PET_LOADER = 0;

    private PetCursorAdapter mPetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup floating action button to access Editor Activity
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView witch will be populated with the pet data
        final ListView petListView = (ListView) findViewById(R.id.pet_list);

        // Find and set the empty view on the ListView, so it shows when the pet list is empty
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup the adapter to create a list item for each entry in ht database.
        // There is no data till the load finishes so pass null for the cursor
        mPetCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mPetCursorAdapter);

        // Set a item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create an intent to move to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                // Create the Uri we must pass to the Editor Activity.
                // Using the method ContentUris.withAppendedId we pass the pet's id to the end of the Uri.
                Uri uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                // Set the Uri on the data field of the intent
                intent.setData(uri);
                // Launch {@link EditorActivity}
                startActivity(intent);
            }
        });

        // Initialize the loader.
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    private void insertDummyData() {
        // Create a ContentValues Object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_PET_CAT_OR_DOG, 0);
        contentValues.put(PetEntry.COLUMN_PET_NAME, "No Name");
        contentValues.put(PetEntry.COLUMN_PET_GENDER, 0);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT, 1);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access pet's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, contentValues);
        // Check if the dummy data successfully inserted to the database
        // If the uri is not null, the insertion was successful
        if (newUri != null) {
            // Show a toast message to inform the user that the dummy data was inserted
            Toast.makeText(this, R.string.insert_dummy_data_success, Toast.LENGTH_LONG).show();
        }
        // Else the uri is null and means that the insertion failed
        else {
            // Show a toast message to inform the user that the dummy data was not inserted
            Toast.makeText(this, R.string.insert_dummy_data_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAllEntries() {
        // Create the Uri
        Uri uri = PetEntry.CONTENT_URI;
        int rowsDeleted = getContentResolver().delete(uri, null, null);
        // Display toast message depending on whether or not the delete was successful
        if (rowsDeleted == 0) {
            // Delete failed
            Toast.makeText(this, R.string.delete_all_entries_failed, Toast.LENGTH_LONG).show();
        }
        else {
            // Otherwise delete was successful
            Toast.makeText(this, R.string.delete_all_entries_succeed, Toast.LENGTH_LONG).show();
        }
    }
    // Set the menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from res/menu/menu_catalog.xml
        // This adds the menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    // Set the menuItems actions
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        //  User clicked on menu item in the app bar overflow menu
        switch (menuItem.getItemId()) {
            // Respond to a click on the "Insert Dummy Data" item
            case R.id.insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete All Pets" item
            case R.id.delete_all_entries:
                // Create a click listener to be used by the warning dialog
                DialogInterface.OnClickListener deleteDialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed to delete all pets
                        deleteAllEntries();
                    }
                };
                showDeleteWarningDialog(deleteDialogListener);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_CAT_OR_DOG,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        // Return the Loader
        return new CursorLoader(this,   // CONTEXT:         The activity context
                PetEntry.CONTENT_URI,   // URI:             The URI to load data from
                projection,             // PROJECTION:      Specify witch columns will be returned
                null,                   // SELECTION:       Filter witch rows will be returned (WHERE clause)
                null,                   // SELECTION ARGS:  The values for the selection
                null);                  // SHORT ORDER:     The order the entries will be shown
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update the {@link PetCursorAdapter} with this new cursor containing the pet data
        mPetCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // When the data is deleted
        mPetCursorAdapter.swapCursor(null);
    }

    private void showDeleteWarningDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        // Create alert dialog builder and set the message
        // Set click listeners for positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_delete_all_pets_dialog);
        builder.setPositiveButton(R.string.warning_delete_all_pets_positive, deleteButtonClickListener);
        builder.setNegativeButton(R.string.warning_delete_all_pets_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User click "cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the warning dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}