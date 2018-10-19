package com.handscape.sdk.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import com.handscape.sdk.inf.IHSBleScanCallBack;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class HSLeScan extends ScanCallback implements BluetoothAdapter.LeScanCallback {

    private IHSBleScanCallBack iBleScanCallBack;
    public HSLeScan() {
    }

    public void setiBleScanCallBack(IHSBleScanCallBack iBleScanCallBack) {
        this.iBleScanCallBack = iBleScanCallBack;
    }

    public void onScanResult(int callbackType, ScanResult result) {
        if (iBleScanCallBack != null) {
            iBleScanCallBack.onScanResult(result.getDevice(), result.getRssi());
        }
    }

    public void onBatchScanResults(List<ScanResult> results) {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            deviceList.add(results.get(i).getDevice());
        }
        if (iBleScanCallBack != null) {
            iBleScanCallBack.onBatchScanResults(deviceList);
        }
    }

    public void onScanFailed(int errorCode) {
        if (iBleScanCallBack != null) {
            iBleScanCallBack.scanfailed(errorCode);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (iBleScanCallBack != null) {
            iBleScanCallBack.onScanResult(device, rssi);
        }
    }

}
