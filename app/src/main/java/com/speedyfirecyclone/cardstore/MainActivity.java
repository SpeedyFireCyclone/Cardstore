package com.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity {

    ListView listViewMain;
    protected FirebaseAnalytics mFirebaseAnalytics;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        listViewMain = (ListView) findViewById(R.id.listViewMain);
        dbHelper = new DBHelper(this);
        populateList();

        listViewMain.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                displayDetails(view);
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToCreate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCard();
            }

        });
    }

    @Override
    protected void onPause(){
        dbHelper.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        populateList();
        super.onResume();
    }

    public void createCard() {
        Intent createCard = new Intent(this, CreateCardActivity.class);
        startActivity(createCard);
    }

    public void populateList() {
        Cursor cardData = dbHelper.getAll();
        CardlistCursorAdapter cardlistAdapter = new CardlistCursorAdapter(this, cardData, 1);
        listViewMain.setAdapter(cardlistAdapter);
    }

    public void displayDetails(View view) {
        Intent details = new Intent(this, DisplayDetailsActivity.class);

        TextView hiddenID = (TextView) view.findViewById(R.id.identifierCardlistAdapter);
        int identifier = Integer.parseInt(hiddenID.getText().toString());
        details.putExtra("ID", identifier);

        startActivity(details);
    }

}