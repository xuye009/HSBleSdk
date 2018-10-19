package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public BluetoothAdapter getBleAdapter() {
        return mAdapter;
    }


    //蓝牙回调类
    private HSLeScan hsScanCallback = null;

    public HSBleManager(Context context) {
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
    public boolean startScanning(final IHSBleScanCallBack iBleScanCallBack) {
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
                    return true;
                }
            } else {
                mAdapter.getBluetoothLeScanner().startScan(hsScanCallback);
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
     * 停止扫描
     */
    public void stopScanning() {
        if (mAdapter != null && mAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.stopLeScan(hsScanCallback);
            } else {
                mAdapter.getBluetoothLeScanner().stopScan(hsScanCallback);
            }
        }
    }

    /**
     * 开始连接
     *
     * @param device：需要连接的设备
     * @param connectCallback：连接回调
     */
    public void connect(final BluetoothDevice device, final IHSCommonCallback connectCallback, final HSBluetoothGattCmd bluetoothGattCallback) {
        if (device != null && initadapter()) {
            clientBluetoothGatt = device.connectGatt(mContext, false, bluetoothGattCallback);
        } else {
            if (connectCallback != null) {
                connectCallback.failed();
            }
        }
    }

    /**
     * 断开连接
     *
     * @param device：设备
     */
    public void disconnect(final BluetoothDevice device) {
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
     * 获取在系统中已经连接的蓝牙设备
     *
     * @return
     */
    public List<BluetoothDevice> getSystemConnectingDevice() {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        deviceList.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
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


}
