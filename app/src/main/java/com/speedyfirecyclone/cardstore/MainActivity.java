package com.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    static Boolean firebasePersistent = false;
    protected FirebaseAnalytics mFirebaseAnalytics;
    ListView listViewMain;
    String userID;
    FirebaseListAdapter mAdapter;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!firebasePersistent) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        firebasePersistent = true;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        database = FirebaseDatabase.getInstance();
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_main);
        listViewMain = (ListView) findViewById(R.id.listViewMain);

        final DatabaseReference myRef = database.getReference("users/" + userID);
        final Query query = myRef.orderByChild("favorite");
        mAdapter = new FirebaseListAdapter<Cardstructure>(this, Cardstructure.class, R.layout.cardlist_adapter, query) {
            @Override
            protected void populateView(View view, Cardstructure card, int position) {

                TextView listCardname = (TextView) view.findViewById(R.id.listCardnameCardlistAdapter);
                listCardname.setText(card.getCardTitle());

            }
        };

        listViewMain.setAdapter(mAdapter);
        listViewMain.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                displayDetails(view, position);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.overflowFOSS:
                Intent FOSS = new Intent(this, Licenses.class);
                startActivity(FOSS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createCard() {
        Intent createCard = new Intent(this, CreateCardActivity.class);
        startActivity(createCard);
    }

    public void displayDetails(View view, int position) {
        final Intent details = new Intent(this, DisplayDetailsActivity.class);
        final DatabaseReference cardID = mAdapter.getRef(position);
        cardID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("cardTitle").getValue().toString();
                String data = dataSnapshot.child("cardData").getValue().toString();
                String favorite = dataSnapshot.child("favorite").getValue().toString();
                details.putExtra("title", title);
                details.putExtra("data", data);
                details.putExtra("favorite", favorite);
                details.putExtra("cardID", cardID.toString());
                startActivity(details);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}