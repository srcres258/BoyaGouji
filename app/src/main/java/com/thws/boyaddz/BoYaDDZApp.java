package com.thws.boyaddz;

import android.app.Application;

public class BoYaDDZApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SoundManager.init(getApplicationContext());
    }
}
