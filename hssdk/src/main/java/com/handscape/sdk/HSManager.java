package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.DisplayMetrics;

import java.util.Set;
import java.util.UUID;

import com.handscape.sdk.ble.HSBluetoothGattCmd;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.inf.ICommondManager;
import com.handscape.sdk.inf.IHSCommonCallback;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

/**
 * 蓝牙触摸SDK入口
 */
public class HSManager {

    private static Context mContext;

    private int screenWidth, screenHeight;

    public static Context getContext() {
        return mContext;
    }

    private HSBleManager hsBleManager;

    private HSTouchManager hsTouchManager;

    private HSBluetoothGattCmd hsBluetoothGattCmd;

    private static HSManager instance = null;


    public static HSManager getinstance(Context context, IHSTouchCmdReceive receive) {
        mContext = context;
        if (instance == null) {
            DisplayMetrics displayMetrics = HSUtils.getScreenSize(context);
            instance = new HSManager(displayMetrics.widthPixels, displayMetrics.heightPixels, receive);
        }
        return instance;
    }

    private HSManager(int width, int height, IHSTouchCmdReceive receive) {
        this.screenWidth = width;
        this.screenHeight = height;
        hsBleManager = new HSBleManager(mContext);
        hsTouchManager = new HSTouchManager(receive);
        hsBluetoothGattCmd = new HSBluetoothGattCmd(screenWidth, screenHeight, hsTouchManager.getHsTouchDispatch());
    }


    /**
     * 开始扫描
     *
     * @param time：时间限制
     * @param iBleScanCallBack
     */
    public boolean startScanning(final long time, final IHSBleScanCallBack iBleScanCallBack,final UUID[] serviceUUId) {
        if (hsBleManager != null && hsBleManager.startScanning(iBleScanCallBack, time)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 扫描并且自动连接
     *
     * @param scanningTimeout   扫描时长
     * @param connectingTimeout 连接超时
     * @param supportName       符合要求的设备名称
     * @param commonCallback    连接回调
     * @param receive           获取数据回调
     * @return
     */
    public boolean startScanningWithAutoConnecting(final long scanningTimeout,
                                                   final long connectingTimeout,
                                                   final String[] supportName,
                                                   final IHSCommonCallback commonCallback,
                                                   final IHSConnectRecevive receive) {
        hsBluetoothGattCmd.setIhsConnectRecevive(receive);
        if (hsBleManager != null &&
                hsBleManager.startScanningWithAutoConnecting(
                        scanningTimeout, connectingTimeout,
                        false, supportName,
                        commonCallback, hsBluetoothGattCmd)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断系统中连接的设备是否有符合要求的
     *
     * @param scanningTimeout   扫描时长
     * @param connectingTimeout 连接超时
     * @param supportName       符合要求的设备名称
     * @param commonCallback    连接回调
     * @param receive           获取数据回调
     * @return
     */
    public boolean checkSystemConnect(final long scanningTimeout,
                                      final long connectingTimeout,
                                      final String[] supportName,
                                      final IHSCommonCallback commonCallback,
                                      final IHSConnectRecevive receive) {
        hsBluetoothGattCmd.setIhsConnectRecevive(receive);
        if (hsBleManager != null &&
                hsBleManager.startScanningWithAutoConnecting(
                        scanningTimeout, connectingTimeout,
                        true,supportName,
                        commonCallback,hsBluetoothGattCmd)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 停止扫描
     */
    public void stopScanning(final IHSBleScanCallBack iBleScanCallBack) {
        if (hsBleManager != null) {
            hsBleManager.stopScanning();
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfinish();
            }
        }
    }


    /**
     * 连接后自动将指令转化到OnReceive中
     *
     * @param device：要连接的设备
     * @param time：时间
     * @param connectCallback：连接状态接口
     * @param bluetoothGattCallback：连接成功后，接收数据的接口
     */
    public void connect(final BluetoothDevice device, long time, final IHSCommonCallback connectCallback, final IHSConnectRecevive bluetoothGattCallback) {
        hsBluetoothGattCmd.setIhsConnectRecevive(bluetoothGattCallback);
        if (hsBleManager != null) {
            hsBleManager.connect(device, time, connectCallback, hsBluetoothGattCmd);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (hsBleManager != null) {
            hsBleManager.disconnect();
        }
    }


    /**
     * 设置触摸指令接收器
     *
     * @param receive
     */
    public void setReceive(IHSTouchCmdReceive receive) {
        if (hsTouchManager != null) {
            hsTouchManager.setReceive(receive);
        }
    }

    public void setTouchServer(ICommondManager commondManager) {
        if (hsTouchManager != null) {
            hsTouchManager.setTouchServer(commondManager);
        }
    }

    /**
     * 获取已经连接的设备
     *
     * @return
     */
    public BluetoothDevice getConnectedDevice() {
        if (hsBleManager != null) {
            return hsBleManager.getConnectedDevice();
        }
        return null;
    }

    /**
     * 获取已经绑定的设备
     *
     * @return
     */
    public Set<BluetoothDevice> getBondedDevices() {
        if (hsBleManager != null) {
            return hsBleManager.getBondedDevices();
        }
        return null;
    }

    /**
     * 获取本机蓝牙适配器
     *
     * @return
     */
    public BluetoothAdapter getBleAdapter() {
        if (hsBleManager != null) {
            return hsBleManager.getBleAdapter();
        }
        return null;
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
        if (hsBleManager != null) {
            return hsBleManager.writeCharacteristic(serviceUUid, charUUid, value);
        }
        return false;
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
        if (hsBleManager != null) {
            return hsBleManager.writeDescriptor(serviceUUid, charUUid, descriptoUUid, value);
        }
        return false;
    }

    /**
     * 读取指定的特征值
     *
     * @param serviceUUid
     * @param characteristiUUid
     * @return
     */
    public boolean readCharacteristic(UUID serviceUUid, UUID characteristiUUid) {

        if (hsBleManager != null) {
            return hsBleManager.readCharacteristic(serviceUUid, characteristiUUid);
        }
        return false;
    }


    /**
     * 释放所有资源，在蓝牙扫描不到设备的时候调用
     */
    public void realease() {
        if (hsBleManager != null) {
            hsBleManager.realease();
        }
    }


}
