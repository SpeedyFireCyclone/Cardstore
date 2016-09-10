package tk.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EditCardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String cardID;
    Button confirmButton;
    Button deleteButton;
    TextView titleEdit;
    TextView extraInfo;
    FirebaseDatabase database;
    Boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean Advanced = sharedPref.getBoolean(SettingsActivity.ADVANCED_MODE, false);
        if (Advanced) {
            setContentView(R.layout.activity_create_card_advanced);
            Spinner spinner = (Spinner) findViewById(R.id.barcodeFormat);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.barcodeformats_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } else {
            setContentView(R.layout.activity_create_card);
        }

        cardID = getIntent().getStringExtra("cardID");
        confirmButton = (Button) findViewById(R.id.confirmButtonCreateCard);
        deleteButton = (Button) findViewById(R.id.deleteButtonCreateCard);
        titleEdit = (TextView) findViewById(R.id.titleEditCreateCard);
        extraInfo = (TextView) findViewById(R.id.extraInfoCreateCard);

        titleEdit.setText(getIntent().getStringExtra("title"));
        extraInfo.setText(getIntent().getStringExtra("data"));

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
                DatabaseReference myRef = database.getReferenceFromUrl(cardID);
                myRef.removeValue();
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!first) {
            String extraInfoContents = extraInfo.getText().toString();
            //remove the set format string.
            String[] splitContents = extraInfoContents.split("\\r\\n|\\n|\\r");
            extraInfoContents = "";
            for (int i = 1; i < splitContents.length; i++) {
                splitContents[i] += "\n";
                extraInfoContents = extraInfoContents + splitContents[i];
            }
            switch (parent.getItemAtPosition(pos).toString()) {
                case "Aztec":
                    extraInfoContents = "Format: AZTEC\n" + extraInfoContents;
                    break;
                case "Codabar":
                    extraInfoContents = "Format: CODABAR\n" + extraInfoContents;
                    break;
                case "Code 39":
                    extraInfoContents = "Format: CODE_39\n" + extraInfoContents;
                    break;
                case "Code 98":
                    extraInfoContents = "Format: CODE_93\n" + extraInfoContents;
                    break;
                case "Code 128":
                    extraInfoContents = "Format: CODE_128\n" + extraInfoContents;
                    break;
                case "DataMatrix":
                    extraInfoContents = "Format: DATA_MATRIX\n" + extraInfoContents;
                    break;
                case "EAN 8":
                    extraInfoContents = "Format: EAN_8\n" + extraInfoContents;
                    break;
                case "EAN 13":
                    extraInfoContents = "Format: EAN_13\n" + extraInfoContents;
                    break;
                case "ITF":
                    extraInfoContents = "Format: ITF\n" + extraInfoContents;
                    break;
                case "PDF417":
                    extraInfoContents = "Format: PDF_417\n" + extraInfoContents;
                    break;
                case "Plessey (UK)":
                    extraInfoContents = "Format: PLESSEY\n" + extraInfoContents;
                    break;
                case "QR-Code":
                    extraInfoContents = "Format: QR_CODE\n" + extraInfoContents;
                    break;
                case "UPC-A":
                    extraInfoContents = "Format: UPC_A\n" + extraInfoContents;
                    break;
                case "UPC-E":
                    extraInfoContents = "Format: UPC_E\n" + extraInfoContents;
                    break;
            }
            extraInfo.setText(extraInfoContents);
        }
        first = false;
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void setContents(View view) {
        String extraInfoContents = extraInfo.getText().toString();
        EditText contentsEdit = (EditText) findViewById(R.id.barcodeContents);
        if (confirmButton.isEnabled()) {
            extraInfoContents = extraInfoContents.split("\\r\\n|\\n|\\r")[0] + "\n";
        }
        extraInfoContents = extraInfoContents + "Contents: " + contentsEdit.getText() + "\n";
        extraInfo.setText(extraInfoContents);
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
        String titleEditContents = titleEdit.getText().toString();
        String scanResultRaw = extraInfo.getText().toString();
        String favorite = getIntent().getStringExtra("favorite");
        String originalTitle = getIntent().getStringExtra("title");
        if (originalTitle.equals(favorite)) {
            DatabaseReference myRef = database.getReferenceFromUrl(cardID);
            Cardstructure card = new Cardstructure(titleEditContents, scanResultRaw, titleEditContents);
            myRef.setValue(card);
        } else {
            DatabaseReference myRef = database.getReferenceFromUrl(cardID);
            Cardstructure card = new Cardstructure(titleEditContents, scanResultRaw, "     !" + titleEditContents);
            myRef.setValue(card);
        }
    }

    public void finishedCard() {

        Intent createdCard = new Intent(this, MainActivity.class);
        startActivity(createdCard);
        finish();
    }

}

