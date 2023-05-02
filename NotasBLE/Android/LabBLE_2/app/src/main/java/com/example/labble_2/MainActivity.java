package com.example.labble_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "MainActivity";
    private static final String deviceName = "LabBLE_2";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String deviceMacAddress;
    private RojoBLE rojoTX;
    private RojoBLE rojoRX;
    private Button btnON_OFF;
    private TextView txtStatus;
    private TextView txtSwicth;
    private String strValue;
    private boolean swChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnON_OFF = (Button) findViewById(R.id.btnON_OFF);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtSwicth = (TextView) findViewById(R.id.txtSwitch);

        if(!RojoBLE.checkBLESupport(this, bluetoothAdapter)) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        deviceMacAddress = RojoBLE.searchForMacAddress(this, bluetoothAdapter, deviceName);
        if(deviceMacAddress == null) {
            Log.e(TAG, "ESP 32 not paired");
        }
        else {
            rojoTX = new RojoBLE(this, rxChUUID, RojoBLE.ROJO_TYPE_WRITE, deviceMacAddress);
            rojoRX = new RojoBLE(this, txChUUID, RojoBLE.ROJO_TYPE_NOTIFY, deviceMacAddress);
            rojoRX.setOnCharacteristicNotificationListener(this::onCharacteristicNotificationListener);
        }
        btnON_OFF.setOnClickListener(this::onClick);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnON_OFF) {
            if (btnON_OFF.getText().toString().equals("ON")) {
                rojoTX.sendData("ON");
                btnON_OFF.setText("OFF");
                txtStatus.setText("ON");
            }
            else if (btnON_OFF.getText().toString().equals("OFF")) {
                rojoTX.sendData("OFF");
                btnON_OFF.setText("ON");
                txtStatus.setText("OFF");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void onCharacteristicNotificationListener(byte[] value) {
        strValue = new String(value, StandardCharsets.UTF_8);
        Log.i(TAG, "Received: " + strValue);
        if(strValue.toLowerCase().trim().equals("SW ON".toLowerCase().trim())) {
            swChecked = true;
            txtSwicth.setText("Switch ON");
            Log.i(TAG, "Entered ON");
        }
        else if(strValue.toLowerCase().trim().equals("SW OFF".toLowerCase().trim())) {
            swChecked = false;
            txtSwicth.setText("Switch OFF");
            Log.i(TAG, "Entered OFF");
        }
        else {
            Log.i(TAG, "Not Handled");
        }
    }
}