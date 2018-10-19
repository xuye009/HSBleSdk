package com.handscape.sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.touch.HSCharacteristHandle;
import com.handscape.sdk.touch.HSTouchCommand;
import com.handscape.sdk.touch.HSTouchDispatch;
import com.handscape.sdk.touch.HSTouchEventProcess;

public  class HSBluetoothGattCmd extends HSBluetoothGatt {


    private HSCharacteristHandle hsCharacteristHandle;

    private HSTouchEventProcessImpl hsTouchEventProcess;

    private HSTouchDispatch dispatch;

    private IHSConnectRecevive ihsConnectRecevive;



    public HSBluetoothGattCmd(int mapwidth, int mapheight, HSTouchDispatch dispatch) {
        this.dispatch=dispatch;
        hsTouchEventProcess = new HSTouchEventProcessImpl();
        hsCharacteristHandle = new HSCharacteristHandle(mapwidth, mapheight, hsTouchEventProcess);
    }

    public void setIhsConnectRecevive(IHSConnectRecevive ihsConnectRecevive) {
        this.ihsConnectRecevive = ihsConnectRecevive;
    }

    @Override
    protected  void onDeviceVerifySuccess(BluetoothGatt gatt, int status){
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onDeviceVerifySuccess(gatt,status);
        }

    }

    @Override
    protected  void onDeviceConnected(BluetoothGatt gatt, int status){
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onDeviceConnected(gatt,status);
        }
    }

    @Override
    protected void onDeviceDisConnected(BluetoothGatt gatt, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onDeviceDisConnected(gatt,status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onCharacteristicChanged(gatt,characteristic);
        }
        if(hsCharacteristHandle!=null){
            hsCharacteristHandle.pause(gatt, characteristic);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onCharacteristicRead(gatt,characteristic,status);
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onCharacteristicWrite(gatt,characteristic,status);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onDescriptorRead(gatt,descriptor,status);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onMtuChanged(gatt,mtu,status);
        }
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onPhyRead(gatt,txPhy,rxPhy,status);
        }
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onPhyUpdate(gatt,txPhy,rxPhy,status);
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onReadRemoteRssi(gatt,rssi,status);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        if(ihsConnectRecevive!=null){
            ihsConnectRecevive.onReliableWriteCompleted(gatt,status);
        }
    }

    class HSTouchEventProcessImpl extends HSTouchEventProcess {

        @Override
        public void handleTouchEvent(int touchAction, int pointerID, float eventX, float eventY, float windowWidth, float windowHeight) {
            HSTouchCommand cmd = HSTouchCommand.newCommand(pointerID, touchAction, (int) eventX, (int) eventY);
            if(dispatch!=null){
                dispatch.addCmd(cmd);
            }
        }

    }
}
