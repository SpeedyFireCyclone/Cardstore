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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class CreateCardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView titleEdit;
    TextView extraInfo;
    String titleEditContents;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database;
    Button confirmButton;
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

        titleEdit = (TextView) findViewById(R.id.titleEditCreateCard);
        extraInfo = (TextView) findViewById(R.id.extraInfoCreateCard);
        confirmButton = (Button) findViewById(R.id.confirmButtonCreateCard);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleEditContents = titleEdit.getText().toString();
                if (titleEditContents.equals("")) {
                    Toast.makeText(getApplicationContext(), "Set a title before saving.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference myRef = database.getReference("users/" + userID);
                    Cardstructure card = new Cardstructure(titleEditContents, extraInfo.getText().toString());
                    myRef.push().setValue(card);
                    createdCard(view);
                }
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
        confirmButton.setEnabled(true);
        confirmButton.setText("OK");
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
            extraInfo.setText(scanResultRaw);
            confirmButton.setText("OK");
            confirmButton.setEnabled(true);
        }
    }

    public void createdCard(View view) {
        Intent createdCard = new Intent(this, MainActivity.class);
        startActivity(createdCard);
        finish();
    }

}