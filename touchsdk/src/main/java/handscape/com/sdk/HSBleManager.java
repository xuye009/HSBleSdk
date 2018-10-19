package handscape.com.sdk;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import handscape.com.sdk.inf.IBleConnectCallback;
import handscape.com.sdk.inf.IBleScanCallBack;

/**
 * 蓝牙连接管理器
 */
class HSBleManager {

    private Context mContext;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mAdapter;

    public HSBleManager(Context context) {
        this.mContext = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bluetoothManager.getAdapter();
    }


    /**
     * 开启扫描
     *
     * @param iBleScanCallBack：扫描需要的回调接口
     */
    public void startScanning(final IBleScanCallBack iBleScanCallBack) {
        if (!initadapter()) {
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfailed(0);
            }
        }
        if (hsScanCallback != null) {
            stopScanning(hsScanCallback);
        }
        if (hsScanCallback == null) {
            hsScanCallback = new HSScanCallback(iBleScanCallBack);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (!mAdapter.startLeScan(hsScanCallback)) {
                if (iBleScanCallBack != null) {
                    iBleScanCallBack.scanfailed(1);
                }
            }
        } else {
            mAdapter.getBluetoothLeScanner().startScan(hsScanCallback);
        }
    }

    /**
     * 开始连接
     *
     * @param device：需要连接的设备
     * @param connectCallback：连接回调
     */
    public void connect(final BluetoothDevice device, final IBleConnectCallback connectCallback) {

    }

    /**
     * 停止扫描
     */
    public void stopScanning(HSScanCallback callback) {
        if (mAdapter != null && mAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.stopLeScan(callback);
            } else {
                mAdapter.getBluetoothLeScanner().startScan(callback);
            }
        }
    }

    /**
     * 断开连接
     *
     * @param device：设备
     * @param connectCallbac：连接回调
     */
    public void disconnect(final BluetoothDevice device, final IBleConnectCallback connectCallbac) {

    }


    private HSScanCallback hsScanCallback = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class HSScanCallback extends ScanCallback implements BluetoothAdapter.LeScanCallback {

        private IBleScanCallBack iBleScanCallBack;

        public HSScanCallback(IBleScanCallBack iBleScanCallBack) {
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
                iBleScanCallBack.scanfailed(1);
            }
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (iBleScanCallBack != null) {
                iBleScanCallBack.onScanResult(device, rssi);
            }
        }
    }

    ;


    private boolean initadapter() {
        if (mAdapter == null) {
            mAdapter = bluetoothManager.getAdapter();
        }
        if (mAdapter == null) {
            return false;
        }
        if (!mAdapter.enable()) {
            mAdapter.enable();
        }
        if (!mAdapter.enable()) {

            return false;
        }
        return true;
    }

    /**
     * 清空资源
     */
    private void realease() {

    }


}
