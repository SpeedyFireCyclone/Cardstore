package com.speedyfirecyclone.cardstore;

import com.firebase.client.Firebase;

public class Cardstore extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
