package com.handscape.blesdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.inf.IHSCommonCallback;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.util.HSPermissionCheck;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.util.HandScapeUUID;

public class MainActivity extends FragmentActivity implements View.OnClickListener, IHSBleScanCallBack {


    private Button scanbuttonn, disscanbutton, scanbuttonautton, getconnect;
    private RecyclerView recyclelist;
    private Adapter mAdapter;

    private HSManager hsManager;


    private List<BluetoothDevice> devices = new ArrayList<>();

    private TextView tv;

    private HSPermissionCheck permissionCheck;


    UUID[] seiviceUUIDs1=new UUID[]{ HandScapeUUID.s_HOU_SERVICE};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hsManager = MyApp.getMyapp().getHsManager();
        init();
        permissionCheck= HSPermissionCheck.getInstance();

        permissionCheck.onCreate(this,hsManager.getBleAdapter());
    }

    private void init() {
        getconnect = findViewById(R.id.getconnect);
        scanbuttonautton = findViewById(R.id.scanbuttonautton);
        scanbuttonn = findViewById(R.id.scanbutton);
        disscanbutton = findViewById(R.id.disscanbutton);
        recyclelist = findViewById(R.id.recyclelist);
        mAdapter = new Adapter(this, devices);
        recyclelist.setAdapter(mAdapter);
        recyclelist.setLayoutManager(new LinearLayoutManager(this));
        tv = findViewById(R.id.text);
        scanbuttonn.setOnClickListener(this);
        disscanbutton.setOnClickListener(this);
        scanbuttonautton.setOnClickListener(this);
        getconnect.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        permissionCheck.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        permissionCheck.onActivityResult(requestCode, resultCode, data);
    }

    StringBuilder builder = new StringBuilder();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getconnect:
                List<BluetoothDevice> deviceList = HSUtils.getSystemConnectingDevice();
                tv.setText("成功onScanResult");
                devices.clear();
                devices.addAll(deviceList);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.scanbutton:
                hsManager.startScanning(10 * 1000, this);
                devices.clear();
                break;
            case R.id.disscanbutton:
                hsManager.stopScanning(this);
                break;
            case R.id.scanbuttonautton:
                builder = new StringBuilder();
                recyclelist.setVisibility(View.GONE);
                hsManager.startScanningWithAutoConnecting(10 * 1000, 20 * 1000,
                        new String[]{"HS", "HSPro"}, new IHSCommonCallback() {
                            @Override
                            public void failed() {
                                appendText("failed");
                            }

                            @Override
                            public void success() {
                                appendText("success");

                            }
                        }, new IHSConnectRecevive() {
                            @Override
                            public void onDeviceVerifySuccess(BluetoothGatt gatt, int status) {
                                appendText("onDeviceVerifySuccess");

                            }

                            @Override
                            public void onDeviceConnected(BluetoothGatt gatt, int status) {
                                appendText("onDeviceConnected");

                            }

                            @Override
                            public void onDeviceDisConnected(BluetoothGatt gatt, int status) {
                                appendText("onDeviceDisConnected");

                            }

                            @Override
                            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                                appendText("onCharacteristicChanged");
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

                break;
        }

    }

    public void connect(BluetoothDevice device) {
        ConnectActivity.startActivity(this, device);
    }

    @Override
    public void scanfailed(int code) {
        tv.setText("scanfailed失败" + code);
    }

    @Override
    public void scanfinish() {

    }

    @Override
    public void onScanResult(final BluetoothDevice device, int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("成功onScanResult");
                if (!devices.contains(device)) {
                    devices.add(device);
                }
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onBatchScanResults(List<BluetoothDevice> deviceList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("成功onBatchScanResults");
            }
        });
//        devices.addAll(deviceList);
//        mAdapter.notifyDataSetChanged();
    }

    private void appendText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.append(text + "\n");
                tv.setText(builder.toString());
            }
        });
    }
}
