package ca.crit.videogames;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AndroidLauncher extends AndroidApplication {

	public static final UUID txChUUID = UUID.fromString("5561bdb8-0d80-11ee-be56-0242ac120002");
	public static final UUID rxChUUID = UUID.fromString("583e0dfc-0d80-11ee-be56-0242ac120002");

	private static final String TAG = "AndroidLauncher";
	private static final String deviceName = "Globo";
	private static final String rxConfirm = "GLOK";
	private static final int minLim = 35, maxLim = 80;

	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private String deviceMacAddress;
	private RojoBLE rojoTX, rojoRX;
	private String strValue;
	private boolean start_transmit = false;
	private int maxValue = 390;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!RojoBLE.checkBLESupport(this, bluetoothAdapter)) {
			Toast.makeText(getApplicationContext(), "Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
			finish();
		}
		deviceMacAddress = RojoBLE.searchForMacAddress(this, bluetoothAdapter, deviceName);
		if(deviceMacAddress == null) {
			Log.e(TAG, "ESP32 not paired");
			Toast.makeText(getApplicationContext(), "ESP32 not paired", Toast.LENGTH_LONG).show();
		}
		else {
			rojoTX = new RojoBLE(this, txChUUID, RojoBLE.ROJO_TYPE_WRITE, deviceMacAddress);
			rojoRX = new RojoBLE(this, rxChUUID, RojoBLE.ROJO_TYPE_NOTIFY, deviceMacAddress);
			rojoRX.setOnCharacteristicNotificationListener(this::onCharacteristicNotificationListener);
			rojoTX.sendData("Max:" + maxLim);
			rojoTX.sendData("Min:" + minLim);

			start_transmit = true; //@todo for debug only
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new RespiratoryGame(), config);
	}

	public void onCharacteristicNotificationListener(byte[] value) {
		strValue = new String(value, StandardCharsets.UTF_8);
		Log.i(TAG, "Received: " + strValue);
		if(start_transmit) {
			int numValue = Integer.parseInt(strValue.toLowerCase().trim());
			if(numValue < maxValue) {
				GameHandler.offset = (0.105*numValue)+22;
				Log.i(TAG, "Offset Val " + GameHandler.offset);
			}
			if(numValue >= minLim && numValue <= maxLim) {
				GameHandler.btFilled = true;
				Log.i(TAG, "Entre");
			}
			else
				GameHandler.btFilled = false;
		}
		else {
			if(strValue.toLowerCase().trim().equals(rxConfirm.toLowerCase().trim())){
				start_transmit = true;
				Log.i(TAG, "Se recibio");
			}
		}
	}
}
