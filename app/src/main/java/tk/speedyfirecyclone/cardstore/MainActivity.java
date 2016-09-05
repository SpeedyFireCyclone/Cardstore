package tk.speedyfirecyclone.cardstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    static Boolean firebasePersistent = false;
    private static int RC_SIGN_IN = 1;
    protected FirebaseAnalytics mFirebaseAnalytics;
    ListView listViewMain;
    String userID;
    FirebaseListAdapter mAdapter;
    FirebaseDatabase database;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!firebasePersistent) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebasePersistent = true;
        }
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("846652782731-gm14evv0f1sj3q7k8ml8j8v8ebi7n5jj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
        try {
            if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                inflater.inflate(R.menu.menu_anon, menu);
            } else {
                inflater.inflate(R.menu.menu_main, menu);
            }
            return true;
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.overflowFOSS:
                Intent FOSS = new Intent(this, LicensesActivity.class);
                startActivity(FOSS);
                return true;
            case R.id.overflowINFO:
                Intent INFO = new Intent(this, AboutActivity.class);
                startActivity(INFO);
                return true;
            case R.id.overflowCONVERT:
                convert();
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

    public void convert() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("AUTH", "linkWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    if (task.getException().getMessage().equals("This credential is already associated with a different user account.")) {
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                        FirebaseAuth.getInstance().signOut();
                                        FirebaseAuth.getInstance().signInWithCredential(credential);
                                        recreate();
                                    } else {
                                        FirebaseCrash.report(task.getException());
                                        Toast.makeText(MainActivity.this, "Something went wrong, a bug report has been filed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            } else {
                // Google Sign In failed, update UI appropriately
                Log.d("AUTH", "isSuccess: " + result.isSuccess());
                Toast.makeText(MainActivity.this, "Google authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}