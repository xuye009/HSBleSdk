package com.handscape.blesdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.handscape.sdk.inf.IHSCommonCallback;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.touch.HSCharacteristHandle;
import com.handscape.sdk.touch.HSTouchEventProcess;
import com.handscape.sdk.util.HSUtils;


public class ConnectActivity extends Activity {

    public static void startActivity(Context context, BluetoothDevice device) {
        Intent intent = new Intent(context, ConnectActivity.class);
        intent.putExtra("device",device);
        context.startActivity(intent);
    }

    TextView text;

    private BluetoothDevice device;

    private HSCharacteristHandle hsCharacteristHandle;

    StringBuilder stringBuilder=new StringBuilder();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        device=getIntent().getParcelableExtra("device");
        text = findViewById(R.id.text);
        DisplayMetrics  displayMetrics=HSUtils.getScreenSize(this);
        hsCharacteristHandle=new HSCharacteristHandle(displayMetrics.widthPixels,displayMetrics.heightPixels, new HSTouchEventProcess() {
            @Override
            public void handleTouchEvent(int touchAction, int pointerID, float eventX, float eventY, float windowWidth, float windowHeight) {
                Log.v("HSTouchEventProcess",
                        "touchAction="+ touchAction+
                                " pointerID="+pointerID+" eventX="+eventX+" eventY="+eventY);
            }
        });

        MyApp.getMyapp().getHsManager().connect(device,20*1000, new IHSCommonCallback() {
            @Override
            public void failed() {
                append("failed");
                showstring();
            }

            @Override
            public void success() {
                append("success");
                showstring();
            }
        }, new IHSConnectRecevive() {
            @Override
            public void onDeviceVerifySuccess(BluetoothGatt gatt, int status) {
                append("onDeviceVerifySuccess");
                showstring();

            }

            @Override
            public void onDeviceConnected(BluetoothGatt gatt, int status) {
                append("onDeviceConnected");
                showstring();

            }

            @Override
            public void onDeviceDisConnected(BluetoothGatt gatt, int status) {
                append("onDeviceDisConnected");
                finish();
                showstring();

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {

            }

            @Override
            public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {

            }

            @Override
            public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {

            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

            }
        });
    }

    private void append(String str){
        stringBuilder.append(str);
        stringBuilder.append("\n");
    }
    private void showstring(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(stringBuilder.toString());
            }
        });

    }


}
