package com.handscape.sdk.touch;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.view.MotionEvent;

import java.util.UUID;

import com.handscape.sdk.util.HSConsts;
import com.handscape.sdk.util.HandScapeUUID;

/**
 * 解析蓝牙数据
 */
public class HSCharacteristHandle {


    public static float s_MAPWIDTH_PIXELS, s_MAPHEIGHT_PIXELS;
    private static final int s_HOU_MAX_FINGER = 10;
    private final boolean isHouDataValid[];

    private int rotation = HSConsts.ROTATION_0;
    private int prevNonZeroComponent1;
    private double prevAngle;

    private HSTouchEventProcess eventProcess;


    public HSCharacteristHandle(int mapwidth, int mapheight, HSTouchEventProcess eventProcess) {
        s_MAPWIDTH_PIXELS = mapwidth;
        s_MAPHEIGHT_PIXELS = mapheight;
        this.eventProcess = eventProcess;
        isHouDataValid = new boolean[s_HOU_MAX_FINGER];
    }


    public void pause(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID uuid = characteristic.getUuid();
        byte[] value = characteristic.getValue();
        BluetoothDevice device = gatt.getDevice();
        if (HandScapeUUID.s_TOUCH_DATA.equals(uuid)) {
            if (value != null && value.length >= 10) {
                int touchID = ((value[1] << 8) & 0x0000ff00) | (value[0] & 0x000000ff);
                int x = ((value[2] << 8) & 0x0000ff00) | (value[3] & 0x000000ff);
                int y = ((value[4] << 8) & 0x0000ff00) | (value[5] & 0x000000ff);
                int state = value[8] & 0xff;
                byte vector = value[9];
                onGattCharacteristicProcessing(touchID, x, y, state, vector);
            }
        } else if (HandScapeUUID.s_TOUCH_DATA_MULTIPLE.equals(uuid)) {
            int touchCount = 0;
            while (value.length >= 6 * (touchCount + 1)) {
                int touchID = value[6 * touchCount] & 0x0000000f;
                int x = ((value[6 * touchCount + 1] << 4) & 0x00000ff0) | ((value[6 * touchCount + 3] >> 4) & 0x0000000f);
                int y = ((value[6 * touchCount + 2] << 4) & 0x00000ff0) | (value[6 * touchCount + 3] & 0x0000000f);
                int state = (value[6 * touchCount] >> 4) & 0x0000000f;
                byte vector = value[6 * touchCount + 5];

                onGattCharacteristicProcessing(touchID + 1, x, y, state, vector);

                touchCount++;
            }
        }

        if (HandScapeUUID.s_HOU_TOUCH_DATA_MULTIPLE.equals(uuid)) {
            int touchCount = 0;
            while (value.length >= 1 + 3 * (touchCount + 1)) {
                int touchID = touchCount;
                touchCount++;
                int x = ((((value[1 + 3 * touchID] >> 4) & 0x0000000f) << 8) & 0x00000f00) |
                        (value[1 + 3 * touchID + 1] & 0x000000ff);
                int y = (((value[1 + 3 * touchID] & 0x0000000f) << 8) & 0x00000f00) |
                        (value[1 + 3 * touchID + 2] & 0x000000ff);
                int state = -1;

                if (isHouDataValid[touchID]) {
                    if ((value[1 + 3 * touchID] & 0x00000080) == 0) {  // CANCEL
                        state = 2;
                        isHouDataValid[touchID] = false;
                    } else {  // MOVE
                        state = 1;
                    }
                } else {
                    if ((value[1 + 3 * touchID] & 0x00000080) == 0) {  // INVALID
                        continue;
                    } else {  // DOWN
                        state = 0;
                        isHouDataValid[touchID] = true;
                    }
                }
                onGattCharacteristicProcessing(touchID + 1, x, y, state, (byte) 0);
            }
        }
    }


    private void onGattCharacteristicProcessing(int touchID, int x, int y, int state, byte vector) {
        int touchAction = -1;

        // default: ROTATION_90
        float pixelX = (x / 4096f) * s_MAPWIDTH_PIXELS;
        float pixelY = (y / 4096f) * s_MAPHEIGHT_PIXELS;
        pixelX = s_MAPWIDTH_PIXELS - pixelX;
        pixelY = s_MAPHEIGHT_PIXELS - pixelY;
        //默认就是角度0
        float tmp = pixelX;
        pixelX = (1 - pixelY / s_MAPHEIGHT_PIXELS) * s_MAPWIDTH_PIXELS;
        pixelY = tmp / s_MAPWIDTH_PIXELS * s_MAPHEIGHT_PIXELS;
        switch (rotation) {
            case HSConsts.ROTATION_0:

                break;
            case HSConsts.ROTATION_90:
                break;
            case HSConsts.ROTATION_180:
                tmp = pixelX;
                pixelX = pixelY / s_MAPHEIGHT_PIXELS * s_MAPWIDTH_PIXELS;
                pixelY = (1 - tmp / s_MAPWIDTH_PIXELS) * s_MAPHEIGHT_PIXELS;
                break;
            case HSConsts.ROTATION_270:
                pixelX = s_MAPWIDTH_PIXELS - pixelX;
                pixelY = s_MAPHEIGHT_PIXELS - pixelY;
                break;
        }
        if (touchID > 0) {
            //Start = 0, Update = 1, End = 2
            switch (state) {
                case 0:
                    touchAction = MotionEvent.ACTION_DOWN;
                    break;
                case 1:
                    touchAction = MotionEvent.ACTION_UP;
                    break;
                case 2:
                    touchAction = MotionEvent.ACTION_MOVE;
                    break;
            }

            if (touchAction != -1) {
                double angle = computeAngle(vector);
                if (eventProcess != null) {
                    eventProcess.handleTouchEvent(touchAction, touchID, pixelX, pixelY, s_MAPWIDTH_PIXELS, s_MAPHEIGHT_PIXELS);
                }
            }
        }

    }


    private double computeAngle(byte vector) {
        int component1c = (vector >> 4) & 0x0000000f;
        int component2c = vector & 0x0000000f;

        // sign extend
        if ((component1c & 0x00000008) != 0) {
            component1c |= 0xfffffff0;
        }
        if ((component2c & 0x00000008) != 0) {
            component2c |= 0xfffffff0;
        }
        double component1 = component1c;
        double component2 = component2c;

        double angle = Math.atan2(component1, component2) / 2;

        // HSSWD-164: Adjust angle for orientation
        boolean isLandscape = ((rotation == HSConsts.ROTATION_90) || (rotation == HSConsts.ROTATION_270));

        // customize for HandScape based on experimental data,
        // as if the case is attached to the back of the device.
        //
        // component1c:                          component2c:
        //
        //             0                                       +1
        //        -1       +1                               +2     +2
        //     -3             +3                           0          0
        //   -1                 +1
        //                                              -2              -2
        //  0                      0                  -1                  -1
        //
        //   +1                 -1                      -2              -2
        //     +3             -3                           0           0
        //        +1        -1                               +2    +2
        //             0                                        +1
        //
        // Note: adding Math.PI/2 means counter-clock-wise Math.PI/2
        //       substracting Math.PI/2 means clock-wise Math.PI/2
        //
//        if (!HandscapeSDK.getModel().handsOnFront) { // fix for HSSDK-29

        if (component1c == 0) {
            if (component2c == 0) {
                angle = 0;
            } else if (component2c > 0) {
                if (prevNonZeroComponent1 < 0) {
                    if (isLandscape) {
                        angle = angle - Math.PI / 2;
                    }
                } else if (prevNonZeroComponent1 > 0) {
                    if (isLandscape) {
                        angle = angle + Math.PI / 2;
                    }
                }
            } else { // component2c < 0
                angle = angle - Math.PI / 2;
            }
        } else {
            if (component1c > 0) {
                if (isLandscape) {
                    angle = Math.PI / 2 - angle;
                } else {
                    angle = -angle;
                }
            } else { // component1c < 0
                if (isLandscape) {
                    angle = -Math.PI / 2 - angle;
                } else {
                    angle = -angle;
                }
            }
            prevNonZeroComponent1 = component1c;
        }

        // across PI
        while (angle - prevAngle > Math.PI * 0.9) {
            angle -= Math.PI;
        }
        while (prevAngle - angle > Math.PI * 0.9) {
            angle += Math.PI;
        }
        // avoid raw data jumping around
        if (Math.abs(angle - prevAngle) > Math.PI / 4) {
            angle = prevAngle;
        }
        // bookkeep history
        prevAngle = angle;

        return angle;
    }


    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
