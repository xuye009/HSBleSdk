package com.handscape.sdk.bean;

import android.graphics.PointF;

public class BaseKeyBean {

    //单击按键
    public final static int Type_SingleKey = 0x00000010;
    //游戏摇杆
    public final static int Type_Joystick = 0x00000011;
    //拖放按键
    public final static int Type_DragKey = 0x00000012;

    //按键类型
    protected int keyType = Type_SingleKey;
    //大小
    //limit  0-100
    protected int keysize = 0;

    //透明度
    //limit 1-100
    protected int keyalpha = 50;

    //是否显示
    protected boolean keyvisibi = true;

    public final int getKeyalpha() {
        return keyalpha;
    }

    public final int getKeysize() {
        return keysize;
    }

    public final boolean isKeyvisibi() {
        return keyvisibi;
    }

    public final void setKeyalpha(int keyalpha) {
        this.keyalpha = keyalpha;
    }

    public final void setKeysize(int keysize) {
        this.keysize = keysize;
    }

    public final void setKeyvisibi(boolean keyvisibi) {
        this.keyvisibi = keyvisibi;
    }

    public final void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public final int getKeyType() {
        return keyType;
    }

    public PointF getPoint() {
        return null;
    }

    public void setPoint(PointF point) {}

    public PointF map(int index,int keyCode,boolean isinConfigMode,boolean undefineMap){return null;};

}
