package com.handscape.blesdk;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.util.HSPermissionCheck;

public class MainActivity extends FragmentActivity implements View.OnClickListener, IHSBleScanCallBack {


    private Button scanbuttonn, disscanbutton;
    private RecyclerView recyclelist;
    private Adapter mAdapter;

    private HSManager hsManager;


    private List<BluetoothDevice> devices = new ArrayList<>();

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hsManager=MyApp.getMyapp().getHsManager();
        init();
        HSPermissionCheck.getInstance().onCreate(this,hsManager.getBleAdapter());
    }

    private void init() {
        scanbuttonn = findViewById(R.id.scanbutton);
        disscanbutton = findViewById(R.id.disscanbutton);
        recyclelist = findViewById(R.id.recyclelist);
        mAdapter = new Adapter(this, devices);
        recyclelist.setAdapter(mAdapter);
        recyclelist.setLayoutManager(new LinearLayoutManager(this));
        tv = findViewById(R.id.text);
        scanbuttonn.setOnClickListener(this);
        disscanbutton.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        HSPermissionCheck.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        HSPermissionCheck.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanbutton:
                hsManager.startScanning(10 * 1000, this);
                devices.clear();
                break;
            case R.id.disscanbutton:
                hsManager.stopScanning();
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
    public void onScanResult(BluetoothDevice device, int rssi) {
        tv.setText("成功onScanResult");
        if (!devices.contains(device)) {
            devices.add(device);

        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBatchScanResults(List<BluetoothDevice> deviceList) {
        tv.setText("成功onBatchScanResults");
//        devices.addAll(deviceList);
//        mAdapter.notifyDataSetChanged();
    }
}
