package ca.crit.treasurehunter;

import static ca.crit.treasurehunter.GameHandler.collided;
import static ca.crit.treasurehunter.GameHandler.counter;
import static ca.crit.treasurehunter.GameHandler.onomatopoeiaAppear;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextScreen {
    private final BitmapFont treasureCounter;   //Creado con fuente
    private final BitmapFont elapsedTime;   //Creado con fuente
    private final BitmapFont lapsCounter;   //Creado con fuente
    private final Texture collisionTexture;         //Creado con Imagen
    private final Texture treasureTexture;         //Creado con Imagen
    private float timer;

    TextScreen(){
        /*POINTS COUNTER*/
        treasureTexture = new Texture("Objects/treasure.png");
        treasureCounter = new BitmapFont(Gdx.files.internal("Fonts/counter.fnt"), Gdx.files.internal("Fonts/counter.png"), false);
        treasureCounter.getData().setScale(1f, 1f);

        /*ELAPSED PLAYED TIME*/
        elapsedTime = new BitmapFont(Gdx.files.internal("Fonts/counter.fnt"), Gdx.files.internal("Fonts/counter.png"), false);
        elapsedTime.getData().setScale(1f, 1f);

        /* LAPS COUNTER*/
        lapsCounter = new BitmapFont(Gdx.files.internal("Fonts/counter.fnt"), Gdx.files.internal("Fonts/counter.png"), false);
        lapsCounter.getData().setScale(1f, 1f);

        /*ONOMATOPOEIA COLLISION*/
        collisionTexture = new Texture("Fonts/onomat_bien.png");

        timer = 0;
        onomatopoeiaAppear = false;
    }

    public void render(final SpriteBatch batch){
        batch.draw(treasureTexture, 20, 410, 45, 45);
        treasureCounter.draw(batch, "x"+(int)GameHandler.counter, 70, 450);
        lapsCounter.draw(batch, "Vueltas: "+(int)GameHandler.RoundTrips, 250, 450);
        elapsedTime.draw(batch, (int)GameHandler.playedTime + " s", 560, 450);

        if(collided){
            counter ++;
        }
        if(onomatopoeiaAppear){
            batch.draw(collisionTexture, 150, GameHandler.treasurePosition, 100, 100);
            timer += Gdx.graphics.getDeltaTime();
            if(timer >=2){
                onomatopoeiaAppear = false;
                timer = 0;
                GameScreen.flag = true;
            }
        }
    }
}
