package com.freadapp.fread.helpers;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistence extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
