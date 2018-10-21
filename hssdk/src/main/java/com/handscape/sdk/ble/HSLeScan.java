package com.handscape.sdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.handscape.sdk.inf.IHSBleScanCallBack;

public final class HSLeScan implements BluetoothAdapter.LeScanCallback {

    private IHSBleScanCallBack iBleScanCallBack;

    private MyScanCallback myScanCallback;

    public MyScanCallback getMyScanCallback() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            return myScanCallback;
        }
        return null;
    }

    public HSLeScan() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            myScanCallback=new MyScanCallback();
        }
    }

    public void setiBleScanCallBack(IHSBleScanCallBack iBleScanCallBack) {
        Log.v("xuye", "setiBleScanCallBack");
        this.iBleScanCallBack = iBleScanCallBack;
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.v("xuye", "onLeScan");
        if (iBleScanCallBack != null) {
            iBleScanCallBack.onScanResult(device, rssi);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class MyScanCallback extends ScanCallback {
        public MyScanCallback() {
        }
        public void onScanResult(int callbackType, ScanResult result) {
            Log.v("xuye", "onScanResult");
            if (iBleScanCallBack != null) {
                iBleScanCallBack.onScanResult(result.getDevice(), result.getRssi());
            }
        }

        public void onBatchScanResults(List<ScanResult> results) {
            Log.v("xuye", "onBatchScanResults");
            List<BluetoothDevice> deviceList = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                deviceList.add(results.get(i).getDevice());
            }
            if (iBleScanCallBack != null) {
                iBleScanCallBack.onBatchScanResults(deviceList);
            }
        }

        public void onScanFailed(int errorCode) {
            Log.v("xuye", "onScanFailed");
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfailed(errorCode);
            }
        }
    }

}
