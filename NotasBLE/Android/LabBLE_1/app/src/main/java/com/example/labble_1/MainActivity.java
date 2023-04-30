package com.example.labble_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_ADMIN_BT = 2;
    //private static final int REQUEST_SCAN_BT = 3; @TODO ScanBT
    private static final String esp32NameBLE = "LabBLE_1";
    //private static final UUID BLEsvUUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"); @TODO ScanBT
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothGattCharacteristic characteristicTX;
    private static BluetoothGattCharacteristic characteristicRX;
    private static BluetoothGattService service;
    private static MyGattCallback gattCallbackTXChar;
    private static MyGattCallback gattCallbackRXChar;
    private Button btnON_OFF;
    private TextView txtStatus;
    private boolean esp32IsConnected = false;
    private boolean esp32IsPaired = false;
    private String deviceMacAddress;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private BluetoothGatt mGattRX;

    private static final String[] btPermissions = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN};

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //@TODO Scan Bluetooth
        //BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        btnON_OFF = (Button) findViewById(R.id.btnON_OFF);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        if (bluetoothAdapter != null) { //The device support bt
            if (!bluetoothAdapter.isEnabled()) { //The bluetooth is off on the phone
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, btPermissions, REQUEST_ENABLE_BT);
                    ActivityCompat.requestPermissions(this, btPermissions, REQUEST_ENABLE_ADMIN_BT);
                }
            }
            checkEsp32Connection();
        }
        else {
            Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        if(deviceMacAddress != null) {
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceMacAddress);
            characteristicTX = new BluetoothGattCharacteristic(
                    rxChUUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            gattCallbackTXChar = new MyGattCallback(characteristicTX, "TRY".getBytes());
            mGatt = mDevice.connectGatt(this, false, gattCallbackTXChar);

            characteristicRX = new BluetoothGattCharacteristic(
                    txChUUID,
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            gattCallbackRXChar = new MyGattCallback(characteristicRX, null);
            mGattRX = mDevice.connectGatt(this, false, gattCallbackRXChar);
            gattCallbackRXChar.setOnCharacteristicChangedListener(this::onCharacteristicChanged);
        }
        else {
            Log.d("Gatt Connection", "Device MAC Address given is null");
        }
        btnON_OFF.setOnClickListener(this::onClick);
    }

    @SuppressLint("SetTextI18n")
    public void onCharacteristicChanged(byte[] value) {
        String strValue = new String(value, StandardCharsets.UTF_8);
        Log.i(TAG, strValue);
        if(strValue.toLowerCase().trim().equals("ESP32 ON".toLowerCase().trim())) {
            txtStatus.setText("ON");
        }
        else if(strValue.toLowerCase().trim().equals("ESP32 OFF".toLowerCase().trim())) {
            txtStatus.setText("OFF");
        }
        else {
            Log.d(TAG, "Text received not handled");
        }
    }

    @SuppressLint({"MissingPermission", "SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnON_OFF) {
            if (btnON_OFF.getText().toString().equals("ON")) {
                gattCallbackTXChar.sendData("ON".getBytes(), mGatt);
                btnON_OFF.setText("OFF");
            }
            else if (btnON_OFF.getText().toString().equals("OFF")) {
                gattCallbackTXChar.sendData("OFF".getBytes(), mGatt);
                btnON_OFF.setText("ON");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkEsp32Connection() {
        //Checking permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        //Checking if the phone have devices paired
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (Objects.equals(deviceName, esp32NameBLE) || Objects.equals(deviceName,"ESP32")) {
                    esp32IsConnected = true;
                    deviceMacAddress = deviceHardwareAddress;
                }
            }
            if(!esp32IsConnected) {
                txtStatus.setText("ESP32 Not paired");
            }
        }
        //@TODO Scan for devices
        else {
            //scanForDevices();
        }
    }
    /**
    //TODO Function not working yet
    private void scanForDevices() {
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // Handle the discovered BLE device
                BluetoothDevice device = result.getDevice();
                int rssi = result.getRssi();
                byte[] scanRecord = result.getScanRecord().getBytes();
                //AdvertiseData advertiseData = AdvertiseData.parseFromBytes(scanRecord);
            }

            @Override
            public void onScanFailed(int errorCode) {
                // Handle the scan failure
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.checkSelfPermission(getApplicationContext(), btPermissions, REQUEST_SCAN_BT);
            return;
        }
        bluetoothLeScanner.startScan(scanCallback);
    }
     */
}