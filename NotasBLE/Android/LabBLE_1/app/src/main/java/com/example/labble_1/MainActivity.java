package com.example.labble_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_ADMIN_BT = 2;
    private static final int REQUEST_SCAN_BT = 3;
    private static final String esp32NameBLE = "LabBLE_1";
    private static final UUID BLEsvUUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Button btnON_OFF;
    private TextView txtStatus;
    private boolean esp32IsConnected = false;
    private boolean esp32IsPaired = false;
    private String deviceMacAddress;

    //Experimental
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;

    private static final String[] btPermissions = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN};

    private static final String[] PERMISSIONS_STORAGE = {
            //Manifest.permission.READ_EXTERNAL_STORAGE,
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
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
        } else {
            Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        if(deviceMacAddress != null) {
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceMacAddress);
            BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                    rxChUUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            byte[] dataBuffer = "ON".getBytes();

            MyGattCallback gattCallback = new MyGattCallback(characteristic, dataBuffer);

            mGatt = mDevice.connectGatt(this, false, gattCallback);
        }
        else {
            Log.d("Gatt Connection", "Device MAC Address given is null");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnON_OFF:

            break;
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
        else {
            //scanForDevices();
        }
    }

    public void onChangePressed(View view) {

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