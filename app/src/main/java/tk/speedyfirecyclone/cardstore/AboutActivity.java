package tk.speedyfirecyclone.cardstore;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView aboutText = (TextView) findViewById(R.id.aboutText);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String VN = pInfo.versionName;
            int VC = pInfo.versionCode;
            String ConcatenatedVersion = "Version name: " + VN + "\n\nVersion code: " + VC;
            aboutText.setText(ConcatenatedVersion);
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrash.report(e);
            Log.e("VersionInfo", e.getLocalizedMessage());
            aboutText.setText("Failed to get version information'.\n\nA report has been sent.");
        }

        TextView IDsText = (TextView) findViewById(R.id.IDsText);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String Token = FirebaseInstanceId.getInstance().getToken();
        Log.d("UID", UID);
        Log.d("TokenID", Token);
        String ConcatenatedID = "UID: " + UID + "\n\nToken: " + Token;
        IDsText.setText(ConcatenatedID);
    }
}
