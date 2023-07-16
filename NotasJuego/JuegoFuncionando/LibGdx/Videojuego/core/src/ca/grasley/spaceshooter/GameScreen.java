package ca.grasley.spaceshooter;

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

    private final Food food;

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

        food = new Food(GameHandler.WORLD_WIDTH/2+6, 5, 6);
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
            /*CHARACTERS*/
            wizard.render(batch);
            food.render(batch);
            animal.render(batch);
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

    }
}
