package ca.crit.hungryhamster;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		GameHandler.environment = GameHandler.DESKTOP_ENV;
		GameHandler.musicVolume = 0.0f;
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("HungryHamster");
		new Lwjgl3Application(new Main_hungryHamster(), config);
	}
}
