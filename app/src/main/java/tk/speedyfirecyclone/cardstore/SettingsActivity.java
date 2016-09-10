package tk.speedyfirecyclone.cardstore;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    public static final String ADVANCED_MODE = "Advanced";
    public static final String AUTO_BRIGHT = "Bright";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.preferences);
    }
}
