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
    /*WORLD PARAMETERS*/
    private final int WORLD_WIDHT = 72;
    private final int WORLD_HEIGHT = 128;
    /*SCREEN*/
    private Camera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    /*GRAPHICS*/
    private Texture[] dynamicBackgrounds;
    private Texture[] staticBackgrounds;
    /*TIMING*/
    private float[] backgroundOffsets = {0};
    private float scrollingSpeed;

    public GameScreen(){

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDHT, WORLD_HEIGHT, camera);

        dynamicBackgrounds = new Texture[1];
        dynamicBackgrounds[0] = new Texture(Gdx.files.internal("Background/1.png"));

        staticBackgrounds = new Texture[2];
        staticBackgrounds[0] = new Texture(Gdx.files.internal("Background/2.png"));
        staticBackgrounds[1] = new Texture(Gdx.files.internal("Background/3.png"));

        scrollingSpeed =(float)(WORLD_HEIGHT/4);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
    batch.begin();
        renderDynamicBackground(deltaTime);
        renderStaticBackground();
    batch.end();
    }
    public void renderDynamicBackground(float deltaTime){
        backgroundOffsets [0] += deltaTime * scrollingSpeed / 64;
        backgroundOffsets [1] += deltaTime * scrollingSpeed / 32;

        for(int layer = 0 ; layer < backgroundOffsets.length ; layer++){
            if(backgroundOffsets[layer] > WORLD_WIDHT){
                backgroundOffsets[layer] = 0;
            }
            batch.draw(dynamicBackgrounds[layer], -backgroundOffsets[layer], 0, WORLD_WIDHT, WORLD_HEIGHT);
            batch.draw(dynamicBackgrounds[layer], -backgroundOffsets[layer]+ WORLD_WIDHT, 0 , WORLD_WIDHT, WORLD_HEIGHT);
        }
    }
    public void renderStaticBackground(){
        batch.draw(staticBackgrounds[0],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticBackgrounds[1],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
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
