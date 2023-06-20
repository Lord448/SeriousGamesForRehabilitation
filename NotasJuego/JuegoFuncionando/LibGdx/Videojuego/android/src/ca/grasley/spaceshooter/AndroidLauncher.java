package ca.grasley.spaceshooter;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import ca.grasley.spaceshooter.JuegoCRIT_Game;

public class AndroidLauncher extends AndroidApplication {

	public static final UUID txChUUID = UUID.fromString("058804de-0a45-11ee-be56-0242ac120002");
	public static final UUID rxChUUID = UUID.fromString("006e861c-0a45-11ee-be56-0242ac120002");

	private static final String TAG = "AndroidLauncher";
	private static final String deviceName = "Ladder";
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private String deviceMacAddress;
	private RojoBLE rojoTX, rojoRX;
	private String strValue;

	private static final String strReceptions[] = new String[8];

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for(int i = 0; i < strReceptions.length; i++) {
			strReceptions[i] = "T"+i;
			Log.i(TAG, strReceptions[i]);
		}

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
			rojoTX = new RojoBLE(this, rxChUUID, RojoBLE.ROJO_TYPE_WRITE, deviceMacAddress);
			rojoRX = new RojoBLE(this, txChUUID, RojoBLE.ROJO_TYPE_NOTIFY, deviceMacAddress);
			rojoRX.setOnCharacteristicNotificationListener(this::onCharacteristicNotificationListener);
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new JuegoCRIT_Game(), config);
	}

	public void onCharacteristicNotificationListener(byte[] value) {
		strValue = new String(value, StandardCharsets.UTF_8);
		Log.i(TAG, "Received: " + strValue);
		for(int i = 0; i < strReceptions.length; i++) {
			if(strValue.toLowerCase().trim().equals(strReceptions[i].toLowerCase().trim())) {
				GameHandler.touchPins[i] = true;
				for(int j = 0; j < GameHandler.numTouchPins; j++) {
					if(j != i)
						GameHandler.touchPins[j] = false;
				}
				return;
			}
		}
		Log.i(TAG, "Data not handled");
	}
}
