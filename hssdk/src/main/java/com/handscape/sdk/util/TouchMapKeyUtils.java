package com.handscape.sdk.util;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.R;
import com.handscape.sdk.bean.BaseKeyBean;

public class TouchMapKeyUtils {

    private static HashMap<Integer, LinkedList<Rect>> hsProKeys = new HashMap<>();
    private static WindowManager manager = (WindowManager) HSManager.getContext().getSystemService(Context.WINDOW_SERVICE);

    static int height = 0;
    static int width = 0;

    static {
        int[] screenSize = getScreenSize();
//        String screen = BaseApplication.getInstance().getScreenSize();
//        String[] size = screen.split("_");
        if (screenSize != null && screenSize.length == 2) {
            width = screenSize[0];
            height = screenSize[1];
        }
        //添加旋转
        LinkedList<Rect> rects0 = new LinkedList<>();
        rects0.add(new Rect(0, 0, width >> 1, height >> 1));  //左一
        rects0.add(new Rect(0, height >> 1, width >> 1, height));  //左二
        rects0.add(new Rect(width >> 1, 0, width, height >> 1));  //右一
        rects0.add(new Rect(width >> 1, height >> 1, width, height));  //右二
        hsProKeys.put(0, rects0);
        //添加90度的
        LinkedList<Rect> rects90 = new LinkedList<>();
        rects90.add(new Rect(0, 0, height >> 1, width >> 1));  //左一
        rects90.add(new Rect(0, width >> 1, height >> 1, width));  //左二
        rects90.add(new Rect(height >> 1, 0, height, width >> 1));  //右一
        rects90.add(new Rect(height >> 1, width >> 1, height, width));  //右二
        hsProKeys.put(90, rects90);
        hsProKeys.put(270, rects90);

    }

    public static int[] getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getRealMetrics(metrics);
        return new int[]{Math.min(metrics.widthPixels, metrics.heightPixels), Math.max(metrics.widthPixels, metrics.heightPixels)};
    }

    public static int getScreenRotation() {
        if (manager == null) {
            return 0;
        }
        int rotation = manager.getDefaultDisplay().getRotation();
        if (Surface.ROTATION_0 == rotation) {
            return 0;
        }
        if (Surface.ROTATION_90 == rotation) {
            return 90;
        }
        if (Surface.ROTATION_180 == rotation) {
            return 180;
        }

        if (Surface.ROTATION_270 == rotation) {
            return 270;
        }
        return 0;
    }

    public static final int KEY_HS_L1 = 240;//左1
    public static final int KEY_HS_L2 = 241;//左2
    public static final int KEY_HS_R1 = 242;//右1
    public static final int KEY_HS_R2 = 243;//右2

    public static int makeKeycode(int x, int y) {
        int code = KEY_HS_L1;
        int screenRotation = getScreenRotation();
        LinkedList<Rect> rects = hsProKeys.get(screenRotation);
        if (rects != null) {
            for (int i = 0; i < rects.size(); i++) {
                if (rects.get(i).contains(x, y)) {
                    code = i + KEY_HS_L1;
                }
            }
        }
        return code;
    }


    public static int makeKeycode(float x, float y) {
        int code = 0;
        int screenRotation = getScreenRotation();
        LinkedList<Rect> rects = hsProKeys.get(screenRotation);
        if (rects != null) {
            for (int i = 0; i < rects.size(); i++) {
                if (rects.get(i).contains((int) x, (int) y)) {
                    code = i + KEY_HS_L1;
                }
            }
        }
        return code;
    }

    public static PointF getCentrePoint(int keycode) {
        PointF point = new PointF();
        int screenRotation = getScreenRotation();
        LinkedList<Rect> rects = hsProKeys.get(screenRotation);
        if (rects != null) {
            if (keycode == KEY_HS_L1) {
                point.x = rects.get(0).left + rects.get(0).right;
                point.y = rects.get(0).top + rects.get(0).bottom;
            }
            if (keycode == KEY_HS_L2) {
                point.x = rects.get(1).left + rects.get(1).right;
                point.y = rects.get(1).top + rects.get(1).bottom;
            }
            if (keycode == KEY_HS_R1) {
                point.x = rects.get(2).left + rects.get(2).right;
                point.y = rects.get(2).top + rects.get(2).bottom;
            }
            if (keycode == KEY_HS_R2) {
                point.x = rects.get(3).left + rects.get(3).right;
                point.y = rects.get(3).top + rects.get(3).bottom;
            }
        }
        point.x = point.x / 2;
        point.y = point.y / 2;
        return point;
    }

    public static String getKeyCodeText(int keycode) {
        if (KEY_HS_L1 == keycode) {
            return HSManager.getContext().getResources().getString(R.string.left_1);
        }
        if (KEY_HS_L2 == keycode) {
            return HSManager.getContext().getResources().getString(R.string.left_2);

        }
        if (KEY_HS_R1 == keycode) {
            return HSManager.getContext().getResources().getString(R.string.right_1);
        }
        if (KEY_HS_R2 == keycode) {
            return HSManager.getContext().getResources().getString(R.string.right_2);
        }
        return "";
    }

    //根据坐标点看看现在是否
    public static int getKeyCode(Map<Integer, BaseKeyBean> map, int x, int y) {
        int keycode = 0;
        if (map != null) {
            for (Integer key : map.keySet()) {
                PointF point = map.get(key).getPoint();
                if (x == point.x && y == point.y) {
                    keycode = key;
                }
            }
        }
        return keycode;
    }

}
