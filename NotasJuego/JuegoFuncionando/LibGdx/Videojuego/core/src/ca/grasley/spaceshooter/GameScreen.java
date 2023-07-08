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

    GameScreen(){
        /*SCREEN*/
        camera = new OrthographicCamera();
        viewport = new StretchViewport(GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH, camera);
        /*GRAPHICS*/
        background = new Background();
        treeHouse = new Texture("Background/tree_house.png");
        batch = new SpriteBatch();
        /*CHARACTERS*/
        wizard = new Wizard(50 , 2);
        animal = new Animal(GameHandler.WORLD_WITDH/2 + 34, 2, 30);
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
            batch.draw(treeHouse, (float)GameHandler.WORLD_WITDH/2 + 18, 0, 40, GameHandler.WORLD_HEIGTH+20);
            /*CHARACTERS*/
            wizard.render(batch);
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
