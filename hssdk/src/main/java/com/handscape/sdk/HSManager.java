package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.handscape.sdk.ble.HSBluetoothGattCmd;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.inf.ICommondManager;
import com.handscape.sdk.inf.IHSCommonCallback;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

/**
 * 触摸SDK总入口
 */
public class HSManager {

    private static Context mContext;

    private int screenWidth, screenHeight;

    private boolean isScanning = false;

    public static Context getContext() {
        return mContext;
    }

    private Handler mScheduledExecutorHandler = null;


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
        mScheduledExecutorHandler = new Handler();
        this.screenWidth = width;
        this.screenHeight = height;
        hsBleManager = new HSBleManager(mContext);
        hsTouchManager = new HSTouchManager(receive);
        hsBluetoothGattCmd = new HSBluetoothGattCmd(screenWidth, screenHeight, hsTouchManager.getHsTouchDispatch());
    }


    /**
     * 开始赛秒
     *
     * @param time：时间限制
     * @param iBleScanCallBack
     */
    public void startScanning(final long time, final IHSBleScanCallBack iBleScanCallBack) {
        if (isScanning) {
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_ISSCANNING);
            }
            return;
        }
        if (hsBleManager != null && hsBleManager.startScanning(iBleScanCallBack)) {
            isScanning = true;
            schedule(new Runnable() {
                @Override
                public void run() {
                    if (hsBleManager != null) {
                        stopScanning(iBleScanCallBack);
                    }
                }
            }, time);
        }
    }

    /**
     * 停止扫描
     */
    public void stopScanning(final IHSBleScanCallBack iBleScanCallBack) {
        isScanning = false;
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
     * @param device
     * @param connectCallback
     * @param bluetoothGattCallback
     */
    public void connect(final BluetoothDevice device, long time, final IHSCommonCallback connectCallback, final IHSConnectRecevive bluetoothGattCallback) {
        hsBluetoothGattCmd.setIhsConnectRecevive(bluetoothGattCallback);
        if (hsBleManager != null) {
            schedule(new Runnable() {
                @Override
                public void run() {
                    if (hsBleManager != null && !hsBluetoothGattCmd.isConnect()) {
                        hsBleManager.disconnect(device);
                        if (connectCallback != null) {
                            connectCallback.failed();
                        }
                    }
                }
            }, time);
            hsBleManager.connect(device, connectCallback, hsBluetoothGattCmd);
        }
    }

    /**
     * 断开连接
     *
     * @param device
     */
    public void disconnect(final BluetoothDevice device) {
        if (hsBleManager != null) {
            hsBleManager.disconnect(device);
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
     * 获取在系统中已经连接的蓝牙设备
     *
     * @return
     */
    public List<BluetoothDevice> getSystemConnectingDevice() {
        if (hsBleManager != null) {
            return hsBleManager.getSystemConnectingDevice();
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


}
