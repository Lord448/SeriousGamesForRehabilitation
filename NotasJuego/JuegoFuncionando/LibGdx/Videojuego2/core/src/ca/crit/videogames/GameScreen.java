package ca.crit.videogames;

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
    private Camera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Background background;
    private Gasoline gasoline;
    private Rocket rocket;

    GameScreen(){
        background = new Background();
        gasoline = new Gasoline(93, 1);
        rocket = new Rocket(background.getWORLD_WIDTH()/2, 2, 70);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(background.getWORLD_WIDTH(), background.getWORLD_HEIGHT(), camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
    batch.begin();
        background.renderDynamicBackground(deltaTime, batch);
        background.renderStaticBackground(batch);
        gasoline.render(deltaTime,batch);
        rocket.render(deltaTime,batch);
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
