package com.handscape.blesdk;

import android.app.Application;
import android.util.Log;
import android.view.MotionEvent;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.bean.BaseKeyBean;
import com.handscape.sdk.inf.ICommondManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

import java.util.HashMap;

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
        hsManager.setTouchServer(new ICommondManager() {
            @Override
            public void onCommondReceive(MotionEvent event) {

            }

            @Override
            public HashMap<Integer, BaseKeyBean> getUnDefineMap() {
                return null;
            }

            @Override
            public void addUndefineMap(Integer id, BaseKeyBean point) {

            }

            @Override
            public void removeUndefineKeyMap(Integer id) {

            }

            @Override
            public void clearUndefineKeyMap() {

            }

            @Override
            public HashMap<Integer, BaseKeyBean> getKeyMap() {
                return null;
            }

            @Override
            public void addKeyMap(Integer id, BaseKeyBean point) {

            }

            @Override
            public void removeKeyMap(Integer id) {

            }

            @Override
            public void clearKeyMap() {

            }

            @Override
            public boolean isInConfigMode() {
                return false;
            }
        });
        myapp=this;
    }
}
