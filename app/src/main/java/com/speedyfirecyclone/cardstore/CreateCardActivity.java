package com.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CreateCardActivity extends AppCompatActivity {

    TextView titleEdit;
    TextView extraInfo;
    String titleEditContents;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_create_card);

        titleEdit = (TextView) findViewById(R.id.titleEditCreateCard);
        extraInfo = (TextView) findViewById(R.id.extraInfoCreateCard);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToScan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    public void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
        //Dirty workaround for weird bug where title gets duplicated after returning from scan.
        titleEditContents = titleEdit.getText().toString();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Part 2 of the workaround
        titleEdit.setText(titleEditContents);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        final String scanResultRaw = scanResult.toString();
        final String splitData[] = scanResultRaw.split("\\r\\n|\\n|\\r");
        if (splitData[0].substring(8).equals("null")) {
            //Display error as a toast
            Toast.makeText(getApplicationContext(), "Barcode not found.", Toast.LENGTH_SHORT).show();
        } else {
            Button confirmButton = (Button) findViewById(R.id.confirmButtonCreateCard);
            extraInfo.setText(scanResultRaw);
            confirmButton.setText("OK");
            confirmButton.setEnabled(true);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    titleEditContents = titleEdit.getText().toString();
                    if (titleEditContents.equals("")) {
                        Toast.makeText(getApplicationContext(), "Set a title before saving.", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference myRef = database.getReference("users/" + userID);
                        Cardstructure card = new Cardstructure(titleEditContents, scanResultRaw);
                        myRef.push().setValue(card);
                        createdCard(view);
                    }

                }
            });
        }
    }

    public void createdCard(View view) {
        Intent createdCard = new Intent(this, MainActivity.class);
        startActivity(createdCard);
        finish();
    }

}