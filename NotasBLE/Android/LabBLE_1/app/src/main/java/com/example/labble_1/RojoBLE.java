package com.example.labble_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.Objects;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.S)
//@SuppressLint("MissingPermission")
public class RojoBLE {
    public static final int ROJO_TYPE_WRITE = 1;
    public static final int ROJO_TYPE_NOTIFY = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_ADMIN_BT = 2;
    private static final String TAG = "RojoBLE";
    private final int typeCharacteristic;
    private final Context context;
    private final BluetoothGattCharacteristic mCharacteristic;
    private SetNotificationsListener setNotificationsListener;
    private BluetoothGatt mGatt;
    private BluetoothDevice mDevice;
    private BluetoothGattDescriptor mDescriptor;
    private MyGattCallback GattCallback;
    private String mDeviceMacAddress;
    private String strDataSend;
    private String strDataReceived;
    private byte[] mDataBuffer;
    private byte[] mDataReceived;

    private static final String[] btPermissions = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN
    };

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

    public RojoBLE(Context context, BluetoothGattCharacteristic characteristic, int typeCharacteristic, String deviceMacAddress) {
        mCharacteristic = characteristic;
        this.typeCharacteristic = typeCharacteristic;
        this.context = context;
        if (context == null) {
            Log.e(TAG, "Context is null");
            Log.e(TAG, "Class not constructed");
            return;
        }
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceMacAddress);
        if (this.typeCharacteristic == ROJO_TYPE_WRITE)
            GattCallback = new MyGattCallback(mCharacteristic, "TRY".getBytes());
        else if (this.typeCharacteristic == ROJO_TYPE_NOTIFY) {
            GattCallback = new MyGattCallback(mCharacteristic, null);
            GattCallback.setOnCharacteristicChangedListener(this::onCharacteristicNotificationListener);
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, btPermissions, REQUEST_ENABLE_BT);
            ActivityCompat.requestPermissions((Activity) context, btPermissions, REQUEST_ENABLE_ADMIN_BT);
        }
        mGatt = mDevice.connectGatt(this.context, false, GattCallback);
    }

    public RojoBLE(Context context, BluetoothGattCharacteristic characteristic, int typeCharacteristic, BluetoothAdapter adapter, String deviceName) {
        mCharacteristic = characteristic;
        this.typeCharacteristic = typeCharacteristic;
        this.context = context;
        if (context == null) {
            Log.e(TAG, "Context is null");
            Log.e(TAG, "Class not constructed");
            return;
        }
        if (mDeviceMacAddress == null) {
            mDeviceMacAddress = searchForMacAddress(this.context, adapter, deviceName);
        }
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDeviceMacAddress);
        if (this.typeCharacteristic == ROJO_TYPE_WRITE)
            GattCallback = new MyGattCallback(mCharacteristic, "TRY".getBytes());
        else if (this.typeCharacteristic == ROJO_TYPE_NOTIFY) {
            GattCallback = new MyGattCallback(mCharacteristic, null);
            GattCallback.setOnCharacteristicChangedListener(this::onCharacteristicNotificationListener);
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, btPermissions, REQUEST_ENABLE_BT);
            ActivityCompat.requestPermissions((Activity) context, btPermissions, REQUEST_ENABLE_ADMIN_BT);
        }
        mGatt = mDevice.connectGatt(this.context, false, GattCallback);
    }

    public MyGattCallback getGattCallback() {
        return GattCallback;
    }

    public static String searchForMacAddress(Context context, BluetoothAdapter bluetoothAdapter, String deviceName) {
        if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_STORAGE, 1);
        }
        else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_LOCATION, 1);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String temporalDeviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (Objects.equals(temporalDeviceName, deviceName)) {
                    Log.i(TAG, "Mac address founded");
                    return deviceHardwareAddress;
                }
            }
            Log.i(TAG, "Mac address not founded");
            return null;
        }
        Log.i(TAG, "Mac address not founded");
        return null;
    }

    public boolean sendData(byte[] dataBuffer) {
        if(GattCallback.sendData(dataBuffer, mGatt)) {
            return true;
        }
        else {
            return false;
        }
    }

    private void onCharacteristicNotificationListener(byte[] value) {
        if(setNotificationsListener != null) {
            setNotificationsListener.onCharacteristicNotificationListener(value);
        }
    }

    public interface SetNotificationsListener {
        void onCharacteristicNotificationListener(byte[] value);
    }

    public void setOnCharacteristicNotificationListener(SetNotificationsListener listener) {
        setNotificationsListener = listener;
    }
}
