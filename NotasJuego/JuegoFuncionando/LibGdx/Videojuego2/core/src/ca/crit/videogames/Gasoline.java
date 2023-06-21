package ca.crit.videogames;

import static com.badlogic.gdx.Input.Keys.UP;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gasoline {
    private int x, y, iterator;
    /*CONSTANTES RÁPIDAS*/
    private static final int BAR_WIDTH = 15, BAR_HEIGHT= 6; //tamaño del objeto barra
    private static final int GAS_WIDTH = BAR_WIDTH, GAS_HEIGHT = 20; //tamaño del objeto estacion de gas
    private static final float TiempoAnimacionCompleta = 3; //en segundos
    /*BARRA Y GASOLINA*/
    private TextureRegion[] fillMovement;
    private Texture imageBar, gasStation;
    private float Time, pastTime;

    public Gasoline(int x, int y){
        this.x = x;
        this.y = y;

        gasStation = new Texture(Gdx.files.internal("gas_station.png"));
        imageBar = new Texture(Gdx.files.internal("fillBar.png"));
        TextureRegion [][] tmp = TextureRegion.split(imageBar, imageBar.getWidth()/4, imageBar.getHeight()/4);

        fillMovement(tmp);
        Time = 0f;
        iterator = 0;
        pastTime = 0;
    }
    public void render(float deltaTime, final SpriteBatch batch){
        batch.draw(gasStation, x, y, GAS_WIDTH, GAS_HEIGHT);
        Time += deltaTime;
        if(!GameHandler.stageReached) {
            if (Gdx.input.isKeyPressed(UP))
                GameHandler.filled = true;
            if (Time >= (pastTime + TiempoAnimacionCompleta / 16)) {
                pastTime = Time;

                if (GameHandler.filled) {
                    iterator++;
                } else {
                    iterator--;
                }
            }
            if (iterator < 0) {
                iterator = 15;
            }
            if (iterator == 15) {
                iterator = 0;
            }
            batch.draw(fillMovement[iterator], x, y + (GAS_HEIGHT), BAR_WIDTH, BAR_HEIGHT);
        }
    }

    public int getIterator() {
        return iterator;
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
