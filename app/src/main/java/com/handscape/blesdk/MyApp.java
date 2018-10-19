package com.handscape.blesdk;

import android.app.Application;
import android.util.Log;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

public class MyApp extends Application {

    private HSManager hsManager;

    public HSManager getHsManager() {
        return hsManager;
    }

    private static MyApp myapp=null;

    public static MyApp getMyapp() {
        return myapp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        hsManager = HSManager.getinstance(this, new IHSTouchCmdReceive() {
            @Override
            public void onCmdReceive(String command) {
                Log.v("xuye",command);
            }
        });
        myapp=this;
    }
}
