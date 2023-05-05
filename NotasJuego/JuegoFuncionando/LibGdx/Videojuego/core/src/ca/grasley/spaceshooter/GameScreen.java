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
    private Camera camera;
    private Viewport viewport;

    /*GRAPHICS*/
    private SpriteBatch batch;
    private Texture[] backgrounds;
    private Texture playerShipTexture, starTexture;

    /*TIMING*/
    private float[] backgroundOffsets = {0,0,0,0};
    private float backgroundMaxScrollingSpeed;

    /*WORLD PARAMETERS*/
    private final int WORLD_WIDHT = 72;
    private final int WORLD_HEIGHT = 128;

    GameScreen(){

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDHT, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();

        backgrounds = new Texture[4];
        backgrounds [0] = new Texture("layer1.png");
        backgrounds [1] = new Texture("layer2.png");
        backgrounds [2] = new Texture("layer3.png");
        backgrounds [3] = new Texture("layer4.png");

        backgroundMaxScrollingSpeed = (float)(WORLD_HEIGHT)/4;

        batch = new SpriteBatch();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        /*SCROLLING BACKGROUND*/
        renderBackground(deltaTime);

        batch.end();
    }

    private void renderBackground(float deltaTime) {
        backgroundOffsets [0] += deltaTime * backgroundMaxScrollingSpeed / 16 ;
        backgroundOffsets [1] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets [2] += deltaTime * backgroundMaxScrollingSpeed / 4 ;
        backgroundOffsets [3] += deltaTime * backgroundMaxScrollingSpeed / 2;

        for(int layer = 0 ; layer < backgroundOffsets.length ; layer++){
            if(backgroundOffsets[layer] > WORLD_HEIGHT){
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], WORLD_WIDHT, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer] + WORLD_HEIGHT, WORLD_WIDHT, WORLD_HEIGHT);
        }
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
