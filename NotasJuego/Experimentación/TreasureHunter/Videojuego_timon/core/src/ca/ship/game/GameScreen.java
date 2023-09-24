package ca.ship.game;

import static ca.ship.game.GameHandler.WORLD_HEIGHT;
import static ca.ship.game.GameHandler.WORLD_WIDTH;
import static ca.ship.game.GameHandler.collided;
import static ca.ship.game.GameHandler.counter;
import static ca.ship.game.GameHandler.onomatopoeiaAppear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    /*SCREEN*/
    private final Camera camera;
    private final Viewport viewport;

    /*GRAPHICS*/
    private final SpriteBatch batch;
    private final Background background;

    /*CHARACTER*/
    private final Ship ship;

    /*OBJECTS*/
    private final Treasures treasures;
    private final CircleBar circleBar;

    /*TEXT*/
    private TextScreen textScreen;

    public static boolean flag;

    GameScreen(){
        /*SCREEN*/
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        /*GRAPHICS*/
        batch = new SpriteBatch();
        background = new Background();
        /*CHARACTERS*/
        ship = new Ship(130, 70, 100, 80);
        /*OBJECTS*/
        treasures = new Treasures(580, 35, 35);
        circleBar = new CircleBar(0, 60, 100, 100, 1, 80);
        /*TEXT*/
        textScreen = new TextScreen();

        flag = true;

    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Intersector.overlaps(treasures.rectangle, ship.rectangle) && flag){
            counter ++;
            flag = false;
        }

        if(Intersector.overlaps(treasures.rectangle, ship.rectangle)){
            collided = true;
            onomatopoeiaAppear = true;
        }else {
            collided = false;
        }

        batch.begin();
            background.render(delta, batch);
            treasures.render(delta, batch);
            ship.render(batch);
            circleBar.render(delta,batch);
            textScreen.render(batch);
        batch.end();
        /*SABER POSICIÓN DE CURSOR CON CLICK EN EL MAPA
        if(Gdx.input.isTouched()){
            System.out.println("x: " + Gdx.input.getX());
            System.out.println("y: " + (-1)*(Gdx.input.getY() - 479));
        }*/
    }

    @Override
    public void resize(int width, int height) {

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
