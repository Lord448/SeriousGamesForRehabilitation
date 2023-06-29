package ca.crit.videogames;

import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gasoline {
    private int x, y;
    /*CONSTANTES RÁPIDAS*/
    private static final int BAR_WIDTH = 15, BAR_HEIGHT= 6; //tamaño del objeto barra
    private static final int GAS_WIDTH = BAR_WIDTH, GAS_HEIGHT = 20; //tamaño del objeto estacion de gas
    /*VARIABLES*/
    private float holdingAirTime; //en segundos
    private float Time, pastTime;
    /*BARRA Y GASOLINA*/
    private TextureRegion[] fillMovement;
    private Texture imageBar, gasStation;

    public Gasoline(int x, int y, float holdingAirTime){
        this.x = x;
        this.y = y;
        this.holdingAirTime = holdingAirTime;

        gasStation = new Texture(Gdx.files.internal("Gasoline/gas_station.png"));
        imageBar = new Texture(Gdx.files.internal("Gasoline/fillBar.png"));
        TextureRegion [][] tmp = TextureRegion.split(imageBar, imageBar.getWidth()/4, imageBar.getHeight()/4);

        fillMovement(tmp);
        GameHandler.iterator = 0;
        Time = 0f;
        pastTime = 0;
    }
    public void render(float deltaTime, final SpriteBatch batch){
        batch.draw(gasStation, x, y, GAS_WIDTH, GAS_HEIGHT);
        Time += deltaTime;
        if(!GameHandler.stageReached) {
            GameHandler.filled = GameHandler.offset <= GameHandler.distance + 2 && GameHandler.offset >= GameHandler.distance - 2;
            if (Time >= (pastTime + holdingAirTime / 16)) {
                pastTime = Time;
                if (GameHandler.filled || GameHandler.btFilled) {
                    GameHandler.iterator++;
                } else {
                    GameHandler.iterator = 0;
                }
            }
            if (GameHandler.iterator < 0) {
                GameHandler.iterator = 15;
            }
            if (GameHandler.iterator == 15) {
                GameHandler.iterator = 0;
            }
            batch.draw(fillMovement[GameHandler.iterator], x, y + (GAS_HEIGHT), BAR_WIDTH, BAR_HEIGHT);
        }
    }

    private void fillMovement(TextureRegion[][] temporal) {
        fillMovement = new TextureRegion[16];
        int w = 0;
        for(int ejeY = 0; ejeY<4 ;ejeY++){
            for(int ejeX = 0; ejeX<4; ejeX++){
                fillMovement [w] = temporal [ejeY][ejeX];
                w++;
            }
        }
    }
}
