package handscape.com.sdk.inf;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import java.util.List;

public interface IBleScanCallBack {
    void scanfailed(int code);

    void onScanResult(BluetoothDevice device, int rssi);

    void onBatchScanResults(List<BluetoothDevice> deviceList);
}
