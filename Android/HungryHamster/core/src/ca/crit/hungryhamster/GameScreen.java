package ca.crit.hungryhamster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    /*SCREEN*/
    private final Camera camera;
    private final Viewport viewport;

    /*GRAPHICS*/
    private final SpriteBatch batch;
    private final Texture treeHouse;
    private final Background background;

    /*CHARACTER*/
    private final Wizard wizard;
    private final Animal animal;

    /*OBJECTS*/
    private final Food food;

    /*TEXT*/
    //private final BitmapFont font;

    private final GameText WinText;

    GameScreen(){
        /*SCREEN*/
        camera = new OrthographicCamera();
        viewport = new StretchViewport(GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT, camera);
        /*GRAPHICS*/
        background = new Background();
        treeHouse = new Texture("Background/tree_house.png");
        batch = new SpriteBatch();
        /*CHARACTERS*/
        wizard = new Wizard(GameHandler.WORLD_WIDTH/2 - 25 , 2, 26, 25, 1/10f);
        animal = new Animal(GameHandler.WORLD_WIDTH/2+5, 0, 7, 10, 107, 20, 30);
        /*OBJECTS*/
        food = new Food(GameHandler.WORLD_WIDTH/2+6, 5, 6);
        /*TEXT*/
        WinText = new GameText("¡Bien \nHecho!", Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        WinText.setX(3);
        WinText.setY(50);
        //font = new BitmapFont(Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        //font.getData().setScale(0.2f, 0.2f);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
        /*BACKGROUND*/
        background.renderDynamicBackground(deltaTime, batch);
        background.renderStaticBackground(batch);
        /*OBJECTS*/
        batch.draw(treeHouse, GameHandler.WORLD_WIDTH/2 - 27, 0, GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT+30);
        /*OBJECTS*/
        food.render(batch);
        /*CHARACTERS*/
        wizard.render(batch);
        animal.render(batch);
        /*TEXT*/
        if(GameHandler.animalCounter == GameHandler.countsToWin)
            WinText.draw(batch);
            //font.draw(batch, "¡Bien \nHecho!", 3, 50);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        wizard.dispose();
        animal.dispose();
        WinText.dispose();
    }
}