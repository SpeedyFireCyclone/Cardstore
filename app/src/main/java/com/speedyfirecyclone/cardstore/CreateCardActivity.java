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

public class CreateCardActivity extends AppCompatActivity {

    TextView titleEdit;
    TextView extraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        titleEdit = (TextView) findViewById(R.id.titleEditCreateCard);
        extraInfo = (TextView) findViewById(R.id.extraInfoCreateCard);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToScan);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    scan();
            }
        });
    }

    public void scan()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        final String scanResultRaw = scanResult.toString();
        final String splitData[] = scanResult.toString().split("\\r\\n|\\n|\\r");
        if (splitData[0].substring(8).equals("null"))
        {
            //Display error as a toast
            Toast.makeText(getApplicationContext(), "Barcode not found.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Button confirmButton = (Button) findViewById(R.id.confirmButtonCreateCard);
            extraInfo.setText(scanResult.toString());
            confirmButton.setText("OK");
            confirmButton.setEnabled(true);
            confirmButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    saveCard(splitData, scanResultRaw);
                    createdCard(view);
                }
            });
        }
    }

    public void saveCard(String[] splitData, String scanResultRaw)
    {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.insert(titleEdit.getText().toString(), splitData[0].substring(8), splitData[1].substring(10), scanResultRaw);

    }

    public void createdCard(View view)
    {
        Intent createdCard = new Intent(this, MainActivity.class);
        startActivity(createdCard);
        finish();
    }

}