package com.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class DisplayDetailsActivity extends AppCompatActivity {

    DBHelper dbHelper;
    TextView titleView;
    TextView detailsView;
    int identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);
        dbHelper = new DBHelper(this);
        titleView = (TextView) findViewById(R.id.titleTextViewDetails);
        detailsView = (TextView) findViewById(R.id.detailsTextViewDetails);
        identifier = getIntent().getIntExtra("ID", 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMode();
            }
        });

        Cursor listData = dbHelper.get(identifier);
        listData.moveToFirst();
        titleView.setText(listData.getString(listData.getColumnIndex("title")));
        detailsView.setText(listData.getString(listData.getColumnIndex("data")));

        int favourite = listData.getInt(listData.getColumnIndex("favourite"));

        if (favourite == 1) {
            ImageButton favouriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
            favouriteStar.setImageResource(R.drawable.favourite_pressed);
            favouriteStar.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
        listData.close();


    }

    public void editMode() {
        Intent EditCard = new Intent(this, EditCardActivity.class);

        dbHelper.getWritableDatabase();
        Cursor listData = dbHelper.get(identifier);
        listData.moveToFirst();

        String title = listData.getString(listData.getColumnIndex("title"));
        String rawdata = listData.getString(listData.getColumnIndex("raw"));

        EditCard.putExtra("title", title);
        EditCard.putExtra("raw", rawdata);
        EditCard.putExtra("ID", identifier);

        startActivity(EditCard);
        listData.close();
        finish();
    }

    public void displayCode(View view) {
        Cursor listData = dbHelper.get(identifier);
        listData.moveToFirst();
        String barcodeString = listData.getString(listData.getColumnIndex("data"));
        String formatString = listData.getString(listData.getColumnIndex("format"));
        String titleString = listData.getString(listData.getColumnIndex("title"));

        Intent DisplayCodeIntent = new Intent(this, DisplayCodeActivity.class);
        DisplayCodeIntent.putExtra("barcodeString", barcodeString);
        DisplayCodeIntent.putExtra("formatString", formatString);
        DisplayCodeIntent.putExtra("titleString", titleString);
        startActivity(DisplayCodeIntent);
        listData.close();
    }

    public void favouriteToggle(View view) {
        Cursor listData = dbHelper.get(identifier);
        listData.moveToFirst();

        if (listData.getInt(listData.getColumnIndex("favourite")) == 0) {
            dbHelper.favourite(identifier);
            ImageButton favouriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
            favouriteStar.setImageResource(R.drawable.favourite_pressed);
            favouriteStar.setColorFilter(getResources().getColor(R.color.colorAccent));
        } else {
            dbHelper.unfavourite(identifier);
            ImageButton favouriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
            favouriteStar.setImageResource(R.drawable.favourite_border);
            favouriteStar.setColorFilter(getResources().getColor(R.color.colorIcon));
        }
        listData.close();
    }
}

