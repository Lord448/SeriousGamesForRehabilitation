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
    private Rocket rocket;
    private Gasoline gasoline;
    private AirPosition airPosition;

    GameScreen(){
        background = new Background();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(background.getWORLD_WIDTH(), background.getWORLD_HEIGHT(), camera);
        rocket = new Rocket(background.getWORLD_WIDTH()/2, 2, 65);
        /*VARIABLES AL GUSTO*/
        gasoline = new Gasoline(93, 1, 3); //tiempo de aire sostenido en segundos
        airPosition = new AirPosition(93, 27, 2); //distancia en cm a llegar soplando de la regla
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
    batch.begin();
        background.renderDynamicBackground(deltaTime, batch);
        background.renderStaticBackground(batch);
        rocket.render(deltaTime,batch);
        gasoline.render(deltaTime, batch);
        airPosition.render(deltaTime, batch);
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
