package com.example.labble_1;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

@SuppressLint("MissingPermission")
public class MyGattCallback extends BluetoothGattCallback {
    private static final String TAG = "GattCallback";
    private BluetoothGattCharacteristic mCharacteristic;
    private byte[] mDataBuffer;
    private UUID serviceUUID;

    public MyGattCallback(BluetoothGattCharacteristic characteristic, byte[] dataBuffer) {
        mCharacteristic = characteristic;
        mDataBuffer = dataBuffer;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.");
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "Disconnected from GATT server.");
            // Perform additional actions after disconnection
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "Services discovered successfully.");
            for(BluetoothGattService service : gatt.getServices()) {
                Log.i(TAG, "For service: " + service.getUuid().toString());
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(mCharacteristic.getUuid());
                if (characteristic != null) {
                    Log.i(TAG, "Trying characteristic: " + characteristic.getUuid().toString());
                    mCharacteristic = characteristic;
                    mCharacteristic.setValue(mDataBuffer);
                    gatt.writeCharacteristic(mCharacteristic);
                }
                else {
                    Log.i(TAG, "Failed to get characteristic.");
                }
            }
        }
        else {
            Log.i(TAG, "Failed to discover services: " + status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "Characteristic read successfully.");
            byte[] value = characteristic.getValue();
            // Process the characteristic value
        } else {
            Log.i(TAG, "Failed to read characteristic: " + status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "Characteristic written successfully.");
            // Perform additional actions after characteristic write
        } else {
            Log.i(TAG, "Failed to write characteristic: " + status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        Log.i(TAG, "Characteristic changed.");
        byte[] value = characteristic.getValue();
        // Process the incoming characteristic value
    }

    public void sendData(byte[] mDataBuffer, BluetoothGatt mGatt) {
        if(mCharacteristic != null) {
            mCharacteristic.setValue(mDataBuffer);
            boolean success = mGatt.writeCharacteristic(mCharacteristic);
            if(success)
                Log.i(TAG, "Data send successfully");
            else
                Log.i(TAG, "Failed to send data");
        }
        else
            Log.i(TAG, "Characteristic is null");
    }
}
