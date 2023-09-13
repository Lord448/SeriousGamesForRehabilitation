package ca.crit.hungryhamster;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Arrays;

import ca.crit.hungryhamster.main.GameScreen;
import ca.crit.hungryhamster.main.Sounds;
import ca.crit.hungryhamster.menu.MainMenu;

public class Main_hungryHamster extends Game {
	private MainMenu mainMenu;
	private GameScreen gameScreen;

	@Override
	public void create () {

		mainMenu = new MainMenu();
		gameScreen = new GameScreen();
		switch (GameHandler.DEBUG_MODE) {
			case GameHandler.DEBUG_GAME:
				setScreen(gameScreen);
			break;
			case GameHandler.DEBUG_MENU:
			case GameHandler.DEBUG_NONE:
				setScreen(mainMenu);
			break;
			case GameHandler.DEBUG_DB:
			break;
		}

		//Game sounds
		Sounds sounds = new Sounds();
		Sounds.create();
	}

	@Override
	public void render () {
		super.render();
		if(GameHandler.startGame) {
			setScreen(gameScreen);
			GameHandler.startGame = false;
		}
	}


	@Override
	public void dispose () {
		Sounds.dispose();
		gameScreen.dispose();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
		mainMenu.resize(width, height);
	}
}

