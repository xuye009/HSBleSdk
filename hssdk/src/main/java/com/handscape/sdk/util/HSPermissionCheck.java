package com.handscape.sdk.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class HSPermissionCheck {


    /**
     * 位置模拟
     */
    public static final int REQUEST_LOCATION=100;

    public static final int REQUEST_BLETOOTH=101;


    private Activity appCompatActivity;

    private BluetoothAdapter adapter;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static HSPermissionCheck instance = null;

    public static HSPermissionCheck getInstance() {
        instance=new HSPermissionCheck();
        return instance;
    }

    private HSPermissionCheck() {

    }
    public  void onCreate(Activity appCompatActivity, BluetoothAdapter adapter) {
        this.appCompatActivity = appCompatActivity;
        this.adapter = adapter;
        checkPermission();
    }


    public boolean checkPermission() {
        if (appCompatActivity == null) {
            return false;
        }
        //申请动态权限
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(appCompatActivity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = true;
                break;
            }
        }
        if (flag) {
            ActivityCompat.requestPermissions(appCompatActivity,
                    permissions, 1);
            return false;
        } else {
            //如果在8.0以上版本，打开位置服务才可以扫描到设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!HSUtils.hasLocationEnablePermission(appCompatActivity)) {
                    //如果没有打开位置开关
                    HSUtils.requestLocation(appCompatActivity, REQUEST_LOCATION);
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            checkPermission();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                if (!adapter.isEnabled()) {
                    try {
                        HSUtils.checkBleDevice(appCompatActivity, adapter, REQUEST_BLETOOTH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(appCompatActivity, "位置服务未打开，可能搜索不到设备", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_BLETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(appCompatActivity, "蓝牙未打开", Toast.LENGTH_LONG).show();
            }
        }
    }


}
