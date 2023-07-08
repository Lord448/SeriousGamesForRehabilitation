package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;

public class JuegoCRIT_Game extends Game {
	GameScreen gameScreen;
	@Override
	public void create () {
		gameScreen = new GameScreen();
		setScreen(gameScreen);
	}

	@Override
	public void render () {
		super.render();
	}


	@Override
	public void dispose () {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}
