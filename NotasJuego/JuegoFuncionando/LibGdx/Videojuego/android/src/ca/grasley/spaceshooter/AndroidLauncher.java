package ca.grasley.spaceshooter;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.UUID;

import ca.grasley.spaceshooter.JuegoCRIT_Game;

public class AndroidLauncher extends AndroidApplication {

	public static final UUID txChUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
	public static final UUID rxChUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new JuegoCRIT_Game(), config);
	}
}
