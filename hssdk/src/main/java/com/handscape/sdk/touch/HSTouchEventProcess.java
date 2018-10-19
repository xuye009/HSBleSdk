package com.handscape.sdk.touch;

/**
 * 触摸事件对外传递处理类
 */
public abstract class HSTouchEventProcess {


    public HSTouchEventProcess() {

    }


    public abstract void handleTouchEvent(int touchAction, int pointerID, float eventX,
                                          float eventY, float windowWidth, float windowHeight);


}
