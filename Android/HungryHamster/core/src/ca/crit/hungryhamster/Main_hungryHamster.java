package ca.crit.hungryhamster;


import com.badlogic.gdx.Game;

import ca.crit.hungryhamster.main.GameScreen;
import ca.crit.hungryhamster.main.Sounds;
import ca.crit.hungryhamster.menus.MainMenu;

public class Main_hungryHamster extends Game {
	MainMenu mainMenu;
	GameScreen gameScreen;
	Sounds sounds;
	@Override
	public void create () {
		/*PANTALLA DEL JUEGO*/
		gameScreen = new GameScreen();
		//setScreen(gameScreen);

		mainMenu = new MainMenu(gameScreen);
		setScreen(mainMenu);

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
		mainMenu.resize(width, height);
	}
}

