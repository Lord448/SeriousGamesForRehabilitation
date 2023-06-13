package ca.grasley.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class JuegoCRIT_Game extends Game {
	GameScreen gameScreen;
	//SpriteBatch batch;
	/*CHARACTERS*/
	//private Wizard wizard;
	//private Animal animal;
	@Override
	public void create () {
		gameScreen = new GameScreen();
		setScreen(gameScreen);
		//batch = new SpriteBatch();
		//wizard = new Wizard(15, 5);
		//animal = new Animal(55, 5);
	}

	@Override
	public void render () {
		super.render();
	}


	@Override
	public void dispose () {
		gameScreen.dispose();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}
