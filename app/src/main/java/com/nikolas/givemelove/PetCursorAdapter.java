package com.nikolas.givemelove;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nikolas.givemelove.data.PetContract.PetEntry;

/**
 * Created by Nikolas on 26/3/2017.
 */

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View listItemView, Context context, Cursor cursor) {

        // Field of the listItemView
        ImageView catOrDogImg = (ImageView) listItemView.findViewById(R.id.icon_cat_or_dog);
        TextView nameText = (TextView) listItemView.findViewById(R.id.name_text);
        TextView breedText = (TextView) listItemView.findViewById(R.id.breed_text);
        TextView genderText = (TextView) listItemView.findViewById(R.id.gender_text);
        TextView weightText = (TextView) listItemView.findViewById(R.id.weight_text);

        // Get pet properties from the database
        int catOrDog = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_CAT_OR_DOG));
        String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));
        int gender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
        int weight = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT));

        // Fill the listItemView with the values

        nameText.setText(name);
        breedText.setText(breed);
        weightText.setText("" + weight);

        // For the "type" image
        switch (catOrDog) {
            case PetEntry.PET_UNKNOWN:
                catOrDogImg.setImageResource(R.drawable.ic_add_pet);
                break;
            case PetEntry.PET_CAT:
                catOrDogImg.setImageResource(R.drawable.ic_cat);
                break;
            case PetEntry.PET_DOG:
                catOrDogImg.setImageResource(R.drawable.ic_dog);
                break;
        }

        // For gender field
        switch (gender) {
            case PetEntry.GENDER_UNKNOWN:
                genderText.setText(R.string.gender_unknown);
                break;
            case PetEntry.GENDER_FEMALE:
                genderText.setText(R.string.gender_female);
                break;
            case PetEntry.GENDER_MALE:
                genderText.setText(R.string.gender_male);
                break;
        }

    }
}
