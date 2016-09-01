package tk.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayDetailsActivity extends AppCompatActivity {

    TextView titleView;
    TextView detailsView;
    String title;
    String data;
    String splitData[];
    String favorite;
    FirebaseDatabase database;
    String cardID;
    ImageButton favoriteStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_display_details);

        titleView = (TextView) findViewById(R.id.titleTextViewDetails);
        detailsView = (TextView) findViewById(R.id.detailsTextViewDetails);
        title = getIntent().getStringExtra("title");
        data = getIntent().getStringExtra("data");
        splitData = data.split("\\r\\n|\\n|\\r");
        favorite = getIntent().getStringExtra("favorite");
        cardID = getIntent().getStringExtra("cardID");

        titleView.setText(title);
        detailsView.setText(splitData[1].substring(10));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMode();
            }
        });

        if (!favorite.equals(title)) {
            favoriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
            favoriteStar.setImageDrawable(getDrawable(R.drawable.favourite_pressed));
            favoriteStar.getDrawable().setTint(getResources().getColor(R.color.colorAccent));
        }

    }

    public void editMode() {
        Intent EditCard = new Intent(this, EditCardActivity.class);

        EditCard.putExtra("title", title);
        EditCard.putExtra("data", data);
        EditCard.putExtra("favorite", favorite);
        EditCard.putExtra("cardID", cardID);

        startActivity(EditCard);
    }


    public void displayCode(View view) {

        String barcodeString = splitData[1].substring(10);
        String formatString = splitData[0].substring(8);
        String titleString = title;

        Intent DisplayCodeIntent = new Intent(this, DisplayCodeActivity.class);
        DisplayCodeIntent.putExtra("barcodeString", barcodeString);
        DisplayCodeIntent.putExtra("formatString", formatString);
        DisplayCodeIntent.putExtra("titleString", titleString);
        startActivity(DisplayCodeIntent);

    }

    public void favouriteToggle(View view) {
        final DatabaseReference ref = database.getReferenceFromUrl(cardID).child("favorite");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favorite = dataSnapshot.getValue().toString();

                if (title.equals(favorite)) {
                    ref.setValue("     !" + title);
                    favoriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
                    favoriteStar.setImageDrawable(getDrawable(R.drawable.favourite_pressed));
                    favoriteStar.getDrawable().setTint(getResources().getColor(R.color.colorAccent));
                } else {
                    ref.setValue(title);
                    favoriteStar = (ImageButton) findViewById(R.id.favouriteStarDetails);
                    favoriteStar.setImageDrawable(getDrawable(R.drawable.favourite_border));
                    favoriteStar.getDrawable().setTint(getResources().getColor(R.color.colorIcon));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}