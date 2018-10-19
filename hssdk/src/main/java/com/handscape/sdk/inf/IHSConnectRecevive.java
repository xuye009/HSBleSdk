package com.handscape.sdk.inf;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public interface IHSConnectRecevive {

    void onDeviceVerifySuccess(BluetoothGatt gatt, int status);

    void onDeviceConnected(BluetoothGatt gatt, int status);

    void onDeviceDisConnected(BluetoothGatt gatt, int status);

    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);


    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);


    void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);


    void onMtuChanged(BluetoothGatt gatt, int mtu, int status);


    void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status);


    void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status);


    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);


    void onReliableWriteCompleted(BluetoothGatt gatt, int status);


}
