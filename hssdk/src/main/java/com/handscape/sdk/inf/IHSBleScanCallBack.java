package com.handscape.sdk.inf;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface IHSBleScanCallBack {
    void scanfailed(int code);

    void scanfinish();
    void onScanResult(BluetoothDevice device, int rssi);

    void onBatchScanResults(List<BluetoothDevice> deviceList);

    int ERROR_INITADAPTER=0;
    int ERROR_STARTLESCAN=1;
    int ERROR_ISSCANNING=2;

}
