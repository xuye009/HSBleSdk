package com.handscape.sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

import com.handscape.sdk.util.HandScapeUUID;
import com.handscape.sdk.util.HSSingleTaskManager;

/**
 * 基本的处理器
 * 选择接收多指指令
 */
 abstract class HSBluetoothGatt extends BluetoothGattCallback {

    private HSSingleTaskManager singleTaskManager = null;

    /**
     * 是否链接成功
     */
    private boolean isConnect=false;

    public boolean isConnect() {
        return isConnect;
    }

    public HSBluetoothGatt() {
        singleTaskManager = HSSingleTaskManager.getnewInstance();
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (BluetoothGatt.GATT_SUCCESS == status) {
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    isConnect=true;
                    onDeviceConnected(gatt, status);
                    if (gatt != null && (gatt.getServices() == null || gatt.getServices().size() == 0)) {
                        gatt.discoverServices();
                    }else if(gatt!=null&&gatt.getServices().size()>0){
                        handservice(gatt,status);
                    }
                    break;
                case BluetoothGatt.STATE_CONNECTING:
                    isConnect=true;
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    isConnect=false;
                    onDeviceDisConnected(gatt,status);
                    if (gatt != null) {
                        gatt.close();
                    }
                    gatt = null;
                    break;
                case BluetoothGatt.STATE_DISCONNECTING:
                    isConnect=false;
                    onDeviceDisConnected(gatt,status);
                    break;
            }
        }
    }

    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
        handservice(gatt,status);
    }



    private void handservice(final BluetoothGatt gatt, int status){
        if (gatt != null && gatt.getServices() != null && gatt.getServices().size() > 0) {
            List<BluetoothGattService> gattServices = gatt.getServices();
            for (int i = 0; i < gattServices.size(); i++) {
                final BluetoothGattService service = gattServices.get(i);
                if (HandScapeUUID.s_HOU_SERVICE.equals(service.getUuid())) {
                    if (singleTaskManager != null) {
                        try {
                            singleTaskManager.addTask(0, new Runnable() {
                                @Override
                                public void run() {
                                    setCharacteristicNotification(gatt, service, HandScapeUUID.s_HOU_TOUCH_DATA_MULTIPLE, true);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (HandScapeUUID.s_TOUCH_SERVICE.equals(service.getUuid())) {
                    if (singleTaskManager != null) {
                        try {
                            singleTaskManager.addTask(0, new Runnable() {
                                @Override
                                public void run() {
                                    setCharacteristicNotification(gatt, service, HandScapeUUID.s_TOUCH_DATA_MULTIPLE, true);
                                }
                            });
                            singleTaskManager.addTask(0, new Runnable() {
                                @Override
                                public void run() {
                                    setCharacteristicNotification(gatt, service, HandScapeUUID.s_CJZC_SERVICE, true);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            singleTaskManager.runTask();
        }
    }
    /**
     * 接收到数据
     *
     * @param gatt
     * @param characteristic
     */
    @Override
    public abstract void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (singleTaskManager != null) {
            if (singleTaskManager.getTaskSize() == 1
                    && gatt != null
                    && gatt.getService(HandScapeUUID.s_TOUCH_SERVICE) != null
                    && gatt.getService(HandScapeUUID.s_TOUCH_SERVICE).getCharacteristic(HandScapeUUID.s_TOUCH_DATA_MULTIPLE) != null) {
                onDeviceVerifySuccess(gatt, status);
            }
        }
        if (singleTaskManager.hasNext()) {
            singleTaskManager.runTask();
        }

    }



    protected final boolean setCharacteristicNotification(final BluetoothGatt gatt, final BluetoothGattService service, UUID uuid, boolean enable) {
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        boolean success = gatt.setCharacteristicNotification(characteristic, true);
        if (success) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(HandScapeUUID.s_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                byte[] val = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(val);
                boolean flag = gatt.writeDescriptor(descriptor);
                return flag;
            }
        }
        return false;
    }
    @Override
    public abstract void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) ;

    @Override
    public abstract void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    @Override
    public abstract void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    @Override
    public abstract void onMtuChanged(BluetoothGatt gatt, int mtu, int status);

    @Override
    public abstract void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status);

    @Override
    public abstract void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status);

    @Override
    public abstract void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

    @Override
    public abstract void onReliableWriteCompleted(BluetoothGatt gatt, int status);
    /**
     * 设备验证成功
     * 在所有服务初始化完成后才会执行
     *
     * @param gatt
     * @param status
     */
    protected abstract void onDeviceVerifySuccess(BluetoothGatt gatt, int status);

    /**
     * 设备连接成功
     *
     * @param gatt
     * @param status
     */
    protected abstract void onDeviceConnected(BluetoothGatt gatt, int status);

    /**
     * 设备断开连接
     *
     * @param gatt
     * @param status
     */
    protected abstract void onDeviceDisConnected(BluetoothGatt gatt, int status);


}
