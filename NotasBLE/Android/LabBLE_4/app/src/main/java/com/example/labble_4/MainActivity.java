package com.example.labble_4;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "MainActivity";
    private static final String deviceName = "LabBLE_4";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String deviceMacAddress;
    private RojoBLE rojoBLE;
    private String strValue;

    private TextView txtTempVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTempVal = (TextView) findViewById(R.id.txtTempVal);

        if(!RojoBLE.checkBLESupport(this, bluetoothAdapter)) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        deviceMacAddress = RojoBLE.searchForMacAddress(this, bluetoothAdapter, deviceName);
        if(deviceMacAddress == null) {
            Log.e(TAG, "ESP 32 not paired");
        }
        else {
            rojoBLE = new RojoBLE(this, txChUUID, RojoBLE.ROJO_TYPE_NOTIFY, deviceMacAddress);
            rojoBLE.setOnCharacteristicNotificationListener(this::onCharacteristicNotificationListener);
        }
    }

    public void onCharacteristicNotificationListener(byte[] value) {
        strValue = new String(value, StandardCharsets.UTF_8);
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                txtTempVal.setText(strValue + " °C");
            }
        });
    }
}