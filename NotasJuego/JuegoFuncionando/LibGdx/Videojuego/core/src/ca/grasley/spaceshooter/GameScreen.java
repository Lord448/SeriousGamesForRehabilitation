package ca.grasley.spaceshooter;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private Texture[] staticbackgrounds;
    private Texture treeHouse;
    /*TIMING*/
    private float[] backgroundOffsets = {0,0};
    private float backgroundMaxScrollingSpeed;

    /*WORLD PARAMETERS*/
    private final int WORLD_WIDHT = 72;
    private final int WORLD_HEIGHT = 128;

    /*CHARACTER*/
    private Wizard wizard;
    private Animal animal;

    GameScreen(){

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDHT, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();

        backgrounds = new Texture[4];
        backgrounds [0] = new Texture("Background/layer_sky.png");
        backgrounds [1] = new Texture("Background/layer_clouds.png");

        staticbackgrounds = new Texture[6];
        staticbackgrounds[0] = new Texture("Background/layer0_grasp.png");
        staticbackgrounds[1] = new Texture("Background/layer1_grasp.png");
        staticbackgrounds[2] = new Texture("Background/layer2_grasp.png");
        staticbackgrounds[3] = new Texture("Background/layer3_tree0.png");
        staticbackgrounds[4] = new Texture("Background/layer4_tree1.png");
        staticbackgrounds[5] = new Texture("Background/layer5_grasp.png");
        treeHouse = new Texture("Background/tree_house.png");

        backgroundMaxScrollingSpeed = (float)(WORLD_HEIGHT)/4;
        /*Characters*/
        wizard = new Wizard(15 , 5);
        animal = new Animal(55, 5, 30);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
            /*SCROLLING BACKGROUND*/
            renderDynamicBackground(deltaTime);
            renderStaticBackground();

            batch.draw(treeHouse, 45, 0, 25, WORLD_HEIGHT+20);
            wizard.render(batch);
            animal.render(batch);
        batch.end();
    }

    private void renderDynamicBackground(float deltaTime) {
        backgroundOffsets [0] += deltaTime * backgroundMaxScrollingSpeed / 64;
        backgroundOffsets [1] += deltaTime * backgroundMaxScrollingSpeed / 32;

        for(int layer = 0 ; layer < backgroundOffsets.length ; layer++){
            if(backgroundOffsets[layer] > WORLD_WIDHT){
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], -backgroundOffsets[layer], 0, WORLD_WIDHT, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], -backgroundOffsets[layer]+ WORLD_WIDHT, 0 , WORLD_WIDHT, WORLD_HEIGHT);
        }
    }
    public void renderStaticBackground(){
        batch.draw(staticbackgrounds[0],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticbackgrounds[1],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticbackgrounds[2],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticbackgrounds[3],-20, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticbackgrounds[4],-7, 0, WORLD_WIDHT, WORLD_HEIGHT);
        batch.draw(staticbackgrounds[5],0, 0, WORLD_WIDHT, WORLD_HEIGHT);
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
