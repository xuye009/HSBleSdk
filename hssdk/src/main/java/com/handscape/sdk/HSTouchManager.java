package com.handscape.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.handscape.sdk.inf.ICommondManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;
import com.handscape.sdk.touch.HSTouchCommand;
import com.handscape.sdk.touch.HSTouchDispatch;

/**
 * 触摸管理器
 */
class HSTouchManager {


    private IHSTouchCmdReceive ihsTouchCmdReceive;
    private  HSTouchDispatch hsTouchDispatch;



    public HSTouchManager(IHSTouchCmdReceive receive){
        this.ihsTouchCmdReceive=receive;
        hsTouchDispatch=new HSTouchDispatch(receive);
    }

    /**
     * 处理蓝牙数据
     * @param gatt
     * @param characteristic
     */
    public void onCharacteristReceivei(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){

    }

    public HSTouchDispatch getHsTouchDispatch() {
        return hsTouchDispatch;
    }

    /**
     * 设置触摸事件接收器
     * @param receive
     */
    public void setReceive(IHSTouchCmdReceive receive){
        this.ihsTouchCmdReceive=receive;
        hsTouchDispatch.setReceive(receive);
    }

    public void setTouchServer(ICommondManager commondManager) {
        if(hsTouchDispatch!=null){
            hsTouchDispatch.setTouchServer(commondManager);
        }
    }

    /**
     * 添加触摸指令
     * @param command
     */
    public void addCmd(HSTouchCommand command){
        if(hsTouchDispatch!=null){
            hsTouchDispatch.addCmd(command);
        }
    }




}
