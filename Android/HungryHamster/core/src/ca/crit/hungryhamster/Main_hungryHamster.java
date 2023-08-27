package ca.crit.hungryhamster;


import com.badlogic.gdx.Game;

import ca.crit.hungryhamster.main.GameScreen;
import ca.crit.hungryhamster.main.Sounds;
import ca.crit.hungryhamster.menu.MainMenu;

public class Main_hungryHamster extends Game {
	private MainMenu mainMenu;
	private GameScreen gameScreen;

	@Override
	public void create () {
		/*PANTALLA DEL JUEGO*/
		gameScreen = new GameScreen();
		//setScreen(gameScreen);

		mainMenu = new MainMenu();
		setScreen(mainMenu);

		/*SONIDOS DEL JUEGO*/
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

