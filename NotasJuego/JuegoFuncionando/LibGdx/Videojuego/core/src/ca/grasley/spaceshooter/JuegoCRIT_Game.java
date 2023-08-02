package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class JuegoCRIT_Game extends Game {
	GameScreen gameScreen;
	Sounds sounds;
	@Override
	public void create () {
		/*PANTALLA DEL JUEGO*/
		gameScreen = new GameScreen();
		setScreen(gameScreen);

		/*SONIDOS DEL JUEGO*/
		sounds = new Sounds();
		sounds.create();
	}

	@Override
	public void render () {
		super.render();
	}


	@Override
	public void dispose () {
		sounds.dispose();
		gameScreen.dispose();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}
