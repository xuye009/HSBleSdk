package com.handscape.sdk.util;

import android.graphics.PointF;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import com.handscape.sdk.bean.BaseKeyBean;
import com.handscape.sdk.inf.ICommondManager;

public class HSMotionBuilder {
    private String TAG = HSMotionBuilder.class.getSimpleName();
    private final int TOUCH_DEVICE_ID = 0xFFFF;

    private int screenRotation = 0, screenWidth = 0, screenheight = 0, keyCode = 0;

    private ICommondManager iCommondManager = null;

    public HSMotionBuilder(int rotation, int screenWidth, int ScreenHeight, ICommondManager iCommondManager) {
        this.screenRotation = rotation;
        this.screenWidth = screenWidth;
        this.screenheight = ScreenHeight;
        this.iCommondManager = iCommondManager;
    }

    public MotionEvent build(PacketData data) {
        int pointerCount = data.getIntPacketContent(3);
        if (pointerCount <= 0) {
            Log.e(this.TAG, "build: pointerCount error P = " + pointerCount);
            return null;
        }
        long nowTime = SystemClock.uptimeMillis();
        int action = getMotionAction(data, pointerCount);
        Log.v(TAG, "action=" + action);
        PointerProperties[] properties = getPointerProperties(data, pointerCount);
        PointerCoords[] coords = getPointerCoords(data, pointerCount);

        return MotionEvent.obtain(nowTime, nowTime, action,
                pointerCount, properties, coords,
                0, 0, 0.0f,
                0.0f, TOUCH_DEVICE_ID, 0,
//                InputDeviceCompat.SOURCE_TOUCHSCREEN,
                InputDevice.SOURCE_TOUCHSCREEN, 0);
    }

    private PointerProperties[] getPointerProperties(PacketData data, int count) {
        PointerProperties[] properties = new PointerProperties[count];
        for (int i = 0; i < count; i++) {
            properties[i] = new PointerProperties();
            properties[i].clear();
            properties[i].id = data.getIntPacketContent(5 + (i * 3));
            properties[i].toolType = MotionEvent.TOOL_TYPE_FINGER;
        }
        return properties;
    }

    private PointerCoords[] getPointerCoords(PacketData data, int count) {
        //从机器壳子数据获取多指触摸坐标的数据
        PointerCoords[] coords = new PointerCoords[count];
        //根据壳子现在的手指数目进行循环
        for (int i = 0; i < count; i++) {
            //获取手机壳传递来的坐标数据X，Y
            int iX = data.getIntPacketContent(5 + (i * 3) + 1);
            int iY = data.getIntPacketContent(5 + (i * 3) + 2);
            coords[i] = new PointerCoords();
            coords[i].clear();
            //屏幕旋转角度-0的时候，手机壳的坐标刚好和屏幕一一对应
            float touchX = iX;
            float touchY = iY;
            //根据屏幕方向对坐标进行转化
            if (screenWidth != 0 && screenheight != 0) {
                if (screenRotation == 90) {
                    touchX = iY;
                    touchY = screenWidth - iX;
                }
                if (screenRotation == 270) {
                    touchX = screenheight - iY;
                    touchY = iX;
                }
            }
            if (iCommondManager != null) {
                //获取到对应位置的键值
                keyCode = TouchMapKeyUtils.makeKeycode(touchX, touchY);



                //如果有按键映射，则处理按键映射
                if (iCommondManager.getKeyMap() != null && iCommondManager.getKeyMap().get(keyCode) != null) {
                    BaseKeyBean bean = iCommondManager.getKeyMap().get(keyCode);
                    if (bean != null) {
                        PointF mapPoint=bean.map(0,keyCode,iCommondManager.isInConfigMode(),false);
                        if(mapPoint!=null){
                            touchX = mapPoint.x;
                            touchY = mapPoint.y;
                        }
//                        //单键或者当前在配置模式
//                        if (bean.getKeyType() == BaseKeyBean.Type_SingleKey || iCommondManager.isInConfigMode()) {
//                            PointF mapPoint=bean.map(0,keyCode,iCommondManager.isInConfigMode(),false);
//                            if(mapPoint!=null){
//                                touchX = mapPoint.x;
//                                touchY = mapPoint.y;
//                            }
////                            PointF point = bean.getPoint();
////                            if (point != null) {
////                                touchX = point.x;
////                                touchY = point.y;
////                            }
//                        } else if (bean.getKeyType() == BaseKeyBean.Type_Joystick||bean.getKeyType()==BaseKeyBean.Type_DragKey) {
//                            //摇杆或者拖放类型，以设置的摇杆中心为原点，对所设置的摇杆区域进行转化
//                            PointF mapPoint=bean.map(0,keyCode,iCommondManager.isInConfigMode(),false);
//
//                            PointF point = bean.getPoint();
//                            if (point != null) {
//                                float centX = point.x;
//                                float centY = point.y;
//                                PointF centrePoint = TouchMapKeyUtils.getCentrePoint(keyCode);
//                                float spaceX = centrePoint.x - centX;
//                                float spaceY = centrePoint.y - centY;
//                                touchX = touchX - spaceX;
//                                touchY = touchY - spaceY;
//                            }
//                        }
                    }
                } else if (iCommondManager.getUnDefineMap() != null && iCommondManager.getUnDefineMap().size() == 1) {
                    //如果有未定义的按键映射
                    BaseKeyBean bean = iCommondManager.getUnDefineMap().get(0);
                    PointF mapPoint=bean.map(0,keyCode,iCommondManager.isInConfigMode(),true);
                    if(mapPoint!=null){
                        touchX = mapPoint.x;
                        touchY = mapPoint.y;
                    }
//                    PointF point = bean.getPoint();
//                    touchX = point.x;
//                    touchY = point.y;
                    iCommondManager.addKeyMap(keyCode, bean);
                    iCommondManager.clearUndefineKeyMap();
                }
            }

            //设置处理完成后的坐标点
            coords[i].x = touchX;
            coords[i].y = touchY;
            coords[i].setAxisValue(MotionEvent.AXIS_X, touchX);
            coords[i].setAxisValue(MotionEvent.AXIS_Y, touchY);
            //设置触控区域的大小
            coords[i].setAxisValue(MotionEvent.AXIS_TOOL_MAJOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOOL_MINOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOUCH_MAJOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOUCH_MINOR, 200);
            coords[i].pressure = 0.68f;
            coords[i].size = 0.6f;
        }
        return coords;
    }

    private int getMotionAction(PacketData data, int pointerCount) {
        int mainAction = data.getIntPacketContent(2);
        int mainPtrId = data.getIntPacketContent(4);
        if (pointerCount <= 1)
            return mainAction;

        int ptrIndex = 0;
        for (int i = 0; i < pointerCount; i++) {
            if (mainPtrId == data.getIntPacketContent(5 + (i * 3))) {
                ptrIndex = i;
                break;
            }
        }
        if (mainAction == 0)
            return ptrIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT | MotionEvent.ACTION_POINTER_DOWN;
        if (mainAction == 2)
            return ptrIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT | MotionEvent.ACTION_POINTER_UP;

        return mainAction;
    }

    public  int convertIntType(int action) {
        if (action == 0) return MotionEvent.ACTION_DOWN;
        if (action == 2) return MotionEvent.ACTION_UP;
        if (action == 1) return MotionEvent.ACTION_MOVE;
        return MotionEvent.ACTION_CANCEL;
    }
}
