package com.handscape.sdk.touch;

public class HSClock {    //class tn
    protected static HSClock instance;

    public static HSClock get() {
        if (instance == null) {
            instance = new HSClock();
        }
        return instance;
    }

    public long now() {
        return System.currentTimeMillis();
    }
}
