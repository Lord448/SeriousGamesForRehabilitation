package com.example.labble_3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "MainActivity";
    private static final String deviceName = "LabBLE_3";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String deviceMacAddress;
    private RojoBLE rojoBLE;
    //View objects
    private TextView tvRedRes, tvGreenRes, tvBlueRes;
    private TextView txtConnectionState;
    private SeekBar skRed, skGreen, skBlue;
    private FrameLayout flColorBox;
    //Global variables
    private int[] colorRGB = {0, 0, 0};

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRedRes = (TextView) findViewById(R.id.tvRedRes);
        tvGreenRes = (TextView) findViewById(R.id.tvGreenRes);
        tvBlueRes = (TextView) findViewById(R.id.tvBlueRes);
        txtConnectionState = (TextView) findViewById(R.id.txtConnectionState);
        skRed = (SeekBar) findViewById(R.id.skRed);
        skGreen = (SeekBar) findViewById(R.id.skGreen);
        skBlue = (SeekBar) findViewById(R.id.skBlue);
        flColorBox = (FrameLayout) findViewById(R.id.flColorBox);

        if(!RojoBLE.checkBLESupport(this, bluetoothAdapter)) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        deviceMacAddress = RojoBLE.searchForMacAddress(this, bluetoothAdapter, deviceName);
        if(deviceMacAddress == null) {
            Log.e(TAG, "ESP32 not paired");
            txtConnectionState.setText("ESP32 not paired");
        }
        else
            txtConnectionState.setText("");

        rojoBLE = new RojoBLE(this, rxChUUID, RojoBLE.ROJO_TYPE_WRITE, deviceMacAddress);

        skRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                colorRGB[0] = (int) ((progress*255) / 100);
                tvRedRes.setText(String.valueOf(colorRGB[0]));
                try {
                    boxColorChange(colorRGB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        skGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                colorRGB[1] = (progress*255) / 100;
                tvGreenRes.setText(String.valueOf(colorRGB[1]));
                try {
                    boxColorChange(colorRGB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        skBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                colorRGB[2] = (progress*255) / 100;
                tvBlueRes.setText(String.valueOf(colorRGB[2]));
                try {
                    boxColorChange(colorRGB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void boxColorChange(int[] colorRGB) throws ArrayIndexOutOfBoundsException {
        if(colorRGB.length == 3) {
            flColorBox.setBackgroundColor(Color.rgb(colorRGB[0], colorRGB[1], colorRGB[2]));
            rojoBLE.sendData("R" + colorRGB[0] + " G" + colorRGB[1] + " B" + colorRGB[2] + "\n");
            Log.i(TAG, "R" + colorRGB[0] + " G" + colorRGB[1] + " B" + colorRGB[2] + "\n");
        }

        else
            throw new ArrayIndexOutOfBoundsException();
    }
}