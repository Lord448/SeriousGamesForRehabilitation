package ca.ship.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static ca.ship.game.GameHandler.collided;
import static ca.ship.game.GameHandler.counter;
import static ca.ship.game.GameHandler.onomatopoeiaAppear;

import java.util.PrimitiveIterator;

public class TextScreen {
    private final BitmapFont counterFont;   //Creado con fuente
    private final Texture collisionTexture;         //Creado con Imagen
    private final Texture treasureTexture;         //Creado con Imagen
    private float timer;
    //private boolean onomatopoeiaAppear;

    TextScreen(){
        /*POINTS COUNTER*/
        treasureTexture = new Texture("Character/Treasure/treasure.png");
        counterFont = new BitmapFont(Gdx.files.internal("Fonts/Counter/counter.fnt"), Gdx.files.internal("Fonts/Counter/counter.png"), false);
        counterFont.getData().setScale(1f, 1f);
        /*ONOMATOPEYA COLISION*/
        collisionTexture = new Texture("Fonts/Onomatopoeia/onomat_bien.png");

        timer = 0;
        onomatopoeiaAppear = false;
    }

    public void render(final SpriteBatch batch){
        batch.draw(treasureTexture, 250, 410, 45, 45);
        counterFont.draw(batch, "x"+(int)GameHandler.counter, 300, 450);

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
