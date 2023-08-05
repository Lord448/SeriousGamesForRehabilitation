package ca.crit.hungryhamster;


import com.badlogic.gdx.Game;

public class Main_hungryHamster extends Game {
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

