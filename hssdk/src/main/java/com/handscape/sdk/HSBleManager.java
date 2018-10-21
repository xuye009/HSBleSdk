package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.handscape.sdk.ble.HSBluetoothGattCmd;
import com.handscape.sdk.ble.HSLeScan;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.inf.IHSCommonCallback;
import com.handscape.sdk.inf.IHSBleScanCallBack;

/**
 * 蓝牙连接管理器
 */
class HSBleManager {

    private Context mContext;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mAdapter;

    private BluetoothGatt clientBluetoothGatt;


    private Handler mScheduledExecutorHandler = null;

    /**
     * 判断是否在扫描中
     */
    private boolean isScanning = false;

    public boolean isScanning() {
        return isScanning;
    }

    public BluetoothAdapter getBleAdapter() {
        return mAdapter;
    }


    //蓝牙回调类
    private HSLeScan hsScanCallback = null;

    public HSBleManager(Context context) {
        mScheduledExecutorHandler = new Handler();
        this.mContext = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bluetoothManager.getAdapter();
        hsScanCallback = new HSLeScan();
        initadapter();
    }

    /**
     * 开启扫描
     *
     * @param iBleScanCallBack：扫描需要的回调接口
     */
    public boolean startScanning(final IHSBleScanCallBack iBleScanCallBack, final long time) {
        if (isScanning) {
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_ISSCANNING);
            }
            return false;
        }
        if (hsScanCallback != null) {
            hsScanCallback.setiBleScanCallBack(iBleScanCallBack);
        } else {
            return false;
        }
        if (!initadapter()) {
            if (iBleScanCallBack != null) {
                try {
                    iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_INITADAPTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        //如果可以扫描
        if (mAdapter != null && (mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE || mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                if (!mAdapter.startLeScan(hsScanCallback)) {
                    if (iBleScanCallBack != null) {
                        try {
                            iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_STARTLESCAN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                } else {
                    isScanning = true;
                    schedule(new Runnable() {
                        @Override
                        public void run() {
                            stopScanning();
                            if (iBleScanCallBack != null) {
                                iBleScanCallBack.scanfinish();
                            }
                        }
                    }, time);
                    return true;
                }
            } else {
                isScanning = true;
                mAdapter.getBluetoothLeScanner().startScan(hsScanCallback.getMyScanCallback());
                schedule(new Runnable() {
                    @Override
                    public void run() {
                        stopScanning();
                        if (iBleScanCallBack != null) {
                            iBleScanCallBack.scanfinish();
                        }
                    }
                }, time);
                return true;
            }
        } else {
            if (iBleScanCallBack != null) {
                try {
                    iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_ISSCANNING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    /**
     * * 扫描并且自动连接
     *
     * @param scanningTimeout：扫描时长
     * @param connectingTimeout：连接超时
     * @param supportName：符合要求的设备名称
     * @param supportName：支持的名称
     * @param commonCallback：连接回调
     * @param hsBluetoothGattCmd：获取数据回调
     * @return true：开始扫描；false：启动扫描失败
     */
    public boolean startScanningWithAutoConnecting(final long scanningTimeout, final long connectingTimeout, final String[] supportName, final IHSCommonCallback commonCallback, final HSBluetoothGattCmd hsBluetoothGattCmd) {
        boolean flag = false;
        BluetoothDevice device = null;
        //首先获取系统已经连接的设备
        List<BluetoothDevice> systemConnectingDeviceList = HSUtils.getSystemConnectingDevice();
        for (int i = 0; i < systemConnectingDeviceList.size(); i++) {
            if (HSUtils.isSupportDevice(systemConnectingDeviceList.get(i), supportName)) {
                device = systemConnectingDeviceList.get(i);
                flag = true;
                break;
            }
        }
        if (flag && device != null) {
            connect(device, connectingTimeout, commonCallback, hsBluetoothGattCmd);
            return true;
        } else {
            ihsBleScanCallBack.setConnectingTimeOut(connectingTimeout);
            ihsBleScanCallBack.setSupportName(supportName);
            ihsBleScanCallBack.setCommonCallback(commonCallback);
            ihsBleScanCallBack.setHsBluetoothGattCmd(hsBluetoothGattCmd);
            return startScanning(ihsBleScanCallBack, scanningTimeout);
        }
    }

    /**
     * 停止扫描
     */
    public void stopScanning() {
        if (mAdapter != null && mAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.stopLeScan(hsScanCallback);
                isScanning = false;
            } else {
                mAdapter.getBluetoothLeScanner().stopScan(hsScanCallback.getMyScanCallback());
                isScanning = false;
            }
        }
    }

    /**
     * 开始连接
     *
     * @param device：需要连接的设备
     * @param connectCallback：连接回调
     */
    public void connect(final BluetoothDevice device, final long time, final IHSCommonCallback connectCallback, final HSBluetoothGattCmd bluetoothGattCallback) {
        if (device != null && initadapter()) {
            Log.v("xuye", "connect");
            clientBluetoothGatt = device.connectGatt(mContext, false, bluetoothGattCallback);
            schedule(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothGattCallback != null && !bluetoothGattCallback.isConnect()) {
                        disconnect();
                        if (connectCallback != null) {
                            connectCallback.failed();
                        }
                    }
                }
            }, time);
        } else {
            if (connectCallback != null) {
                connectCallback.failed();
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (clientBluetoothGatt != null) {
            clientBluetoothGatt.disconnect();
        }
    }

    /**
     * 向指定的特征值中写指定的数据
     *
     * @param serviceUUid：特征值所在的serviceUUid
     * @param charUUid：特征值的uuid
     * @param value：写入的值
     * @return
     */
    public boolean writeCharacteristic(final UUID serviceUUid, final UUID charUUid, byte[] value) {
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUid);
        if (characteristic == null) {
            return false;
        }
        characteristic.setValue(value);
        return clientBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 向指定的特征值描述符中中写指定的数据
     *
     * @param serviceUUid
     * @param charUUid
     * @param descriptoUUid
     * @param value
     * @return
     */
    public boolean writeDescriptor(final UUID serviceUUid, final UUID charUUid, final UUID descriptoUUid, byte[] value) {
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUid);
        if (characteristic == null) {
            return false;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptoUUid);
        if (descriptor == null) {
            return false;
        }
        descriptor.setValue(value);
        return clientBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * 读取指定的特征值
     *
     * @param serviceUUid
     * @param characteristiUUid
     * @return
     */
    public boolean readCharacteristic(UUID serviceUUid, UUID characteristiUUid) {
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristiUUid);
        if (characteristic == null) {
            return false;
        }
        return clientBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 获取当前连接的设备
     *
     * @return
     */
    public BluetoothDevice getConnectedDevice() {
        if (clientBluetoothGatt != null) {
            return clientBluetoothGatt.getDevice();
        }
        return null;
    }

    /**
     * 获取已经绑定的设备
     *
     * @return
     */
    public Set<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        return deviceSet;
    }

    //初始化蓝牙设备
    private boolean initadapter() {
        if (mAdapter == null) {
            mAdapter = bluetoothManager.getAdapter();
        }
        if (mAdapter == null) {
            return false;
        }
        if (!mAdapter.isEnabled()) {
            mAdapter.enable();
        }
        if (!mAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    /**
     * 关闭扫描
     * 释放蓝牙连接
     */
    public void realease() {
        try {
            stopScanning();
            if (clientBluetoothGatt != null) {
                clientBluetoothGatt.disconnect();
                clientBluetoothGatt.close();
                clientBluetoothGatt = null;
            }
            HSUtils.refreshBleAppFromSystem(mContext);
            HSUtils.releaseAllScanClient();
            hsScanCallback = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 延迟执行任务
     *
     * @param runnable：执行的任务
     * @param time：延迟的时间/毫秒
     */
    private void schedule(Runnable runnable, long time) {
        if (mScheduledExecutorHandler != null) {
            mScheduledExecutorHandler.postDelayed(runnable, time);
        }
    }

    private AutoConnectingCallBack ihsBleScanCallBack = new AutoConnectingCallBack();

    class AutoConnectingCallBack implements IHSBleScanCallBack {

        private long timeout;

        public void setConnectingTimeOut(long timeout) {
            this.timeout = timeout;
        }

        private String[] supportName;

        public void setSupportName(String[] deviceName) {
            this.supportName = deviceName;
        }

        private IHSCommonCallback commonCallback;

        public void setCommonCallback(IHSCommonCallback commonCallback) {
            this.commonCallback = commonCallback;
        }

        private HSBluetoothGattCmd hsBluetoothGattCmd;

        public void setHsBluetoothGattCmd(HSBluetoothGattCmd hsBluetoothGattCmd) {
            this.hsBluetoothGattCmd = hsBluetoothGattCmd;
        }

        private boolean flag = false;

        @Override
        public void scanfailed(int code) {
            flag = false;
            if (commonCallback != null) {
                commonCallback.failed();
            }
        }

        @Override
        public void scanfinish() {
            if (!flag) {
                if (commonCallback != null) {
                    commonCallback.failed();
                }
            } else {
                if (commonCallback != null) {
                    commonCallback.success();
                }
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device, int rssi) {
            if (HSUtils.isSupportDevice(device, supportName)) {
                flag = true;
                stopScanning();
                connect(device, timeout, commonCallback, hsBluetoothGattCmd);
            }
        }

        @Override
        public void onBatchScanResults(List<BluetoothDevice> deviceList) {

        }
    }

}
