package ca.crit.videogames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Rocket {
    /*CONSTANTES RÁPIDAS*/
    private static final int ROCKET_WIDTH = 10, ROCKET_HEIGHT = 12;
    private static final int ISLAND_WIDTH = 35, ISLAND_HEIGHT = 9;
    private static final int LANDING_WIDTH = 30, LANDING_HEIGHT = 10;
    /*TEXTURAS*/
    private Texture land;
    private Texture island;
    private Texture[] rocket;
    /*PARÁMETROS*/
    private boolean rocket_takeoff = false;
    private int x, y;
    private float speed;
    private static final int LANDING_POINT = ROCKET_HEIGHT + 38;

    public Rocket(int x, int y, float speed){
        this.x = x;
        this.y = y;
        this.speed = speed;

        /*CARGAR IMÁGENES*/
        island = new Texture(Gdx.files.internal("island.png"));
        land = new Texture(Gdx.files.internal("plataforma.png"));
        rocket = new Texture[2];
        rocket[0] = new Texture(Gdx.files.internal("Rocket/rocket_off.png"));
        rocket[1] = new Texture(Gdx.files.internal("Rocket/rocket_on.png"));

    }
    public void render(float deltaTime, final SpriteBatch batch) {
        batch.draw(island, x-11, ROCKET_HEIGHT + 25, ISLAND_WIDTH, ISLAND_HEIGHT);
        batch.draw(land, x-8, ROCKET_HEIGHT + 30, LANDING_WIDTH, LANDING_HEIGHT);

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            rocket_takeoff = true;
        }
        if(rocket_takeoff){
            if(y < LANDING_POINT){
                y += deltaTime*speed;
            }
            if( y >= LANDING_POINT){
                rocket_takeoff = false;
                batch.draw(rocket[0], x, y, ROCKET_WIDTH, ROCKET_HEIGHT);
            }
            batch.draw(rocket[1], x, y, ROCKET_WIDTH, ROCKET_HEIGHT+4);
        } else {
            rocket_takeoff = false;
            batch.draw(rocket[0], x, y, ROCKET_WIDTH, ROCKET_HEIGHT);
        }
    }
}
