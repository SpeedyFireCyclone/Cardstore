package com.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EditCardActivity extends AppCompatActivity {

    int identifier;
    DBHelper dbHelper;
    Button confirmButton;
    Button deleteButton;
    TextView titleEdit;
    TextView extraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        identifier = getIntent().getIntExtra("ID", 0);
        dbHelper = new DBHelper(this);
        confirmButton = (Button) findViewById(R.id.confirmButtonCreateCard);
        deleteButton = (Button) findViewById(R.id.deleteButtonCreateCard);
        titleEdit = (TextView) findViewById(R.id.titleEditCreateCard);
        extraInfo = (TextView) findViewById(R.id.extraInfoCreateCard);

        titleEdit.setText(getIntent().getStringExtra("title"));
        extraInfo.setText(getIntent().getStringExtra("raw"));

        confirmButton.setText("OK");
        confirmButton.setEnabled(true);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCard();
                finishedCard();
            }
        });

        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setEnabled(true);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.delete(identifier);
                finishedCard();

            }
        });

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
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String scanResultRaw = scanResult.toString();
        String splitData[] = scanResultRaw.split("\\r\\n|\\n|\\r");
        if (splitData[0].substring(8).equals("null")) {

            //Display error as a toast
            Toast.makeText(getApplicationContext(), "Barcode not found.", Toast.LENGTH_SHORT).show();
            //Disable confirm button
            confirmButton.setText("Scan to continue");
            confirmButton.setEnabled(false);

        } else {
            extraInfo.setText(scanResultRaw);
        }
    }

    public void updateCard() {
        String scanResultRaw = extraInfo.getText().toString();
        String splitData[] = scanResultRaw.split("\\r\\n|\\n|\\r");
        dbHelper.update(identifier, titleEdit.getText().toString(), splitData[0].substring(8), splitData[1].substring(10), scanResultRaw);
    }

    public void finishedCard() {

        Intent createdCard = new Intent(this, MainActivity.class);
        startActivity(createdCard);
        finish();
    }

}

