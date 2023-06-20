package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_0;
import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;
import static com.badlogic.gdx.Input.Keys.NUM_5;
import static com.badlogic.gdx.Input.Keys.NUM_6;
import static com.badlogic.gdx.Input.Keys.NUM_7;
import static com.badlogic.gdx.Input.Keys.NUM_8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    private final int maxLim = 80, minLim = 20;
    private float x, y;
    private float speed;
    private float positions[] = new float[GameHandler.numTouchPins];
    private Texture animal_texture;
    private float currentPos;
    private int key[] = {
            NUM_0,
            NUM_1,
            NUM_2,
            NUM_3,
            NUM_4,
            NUM_5,
            NUM_6,
            NUM_7,
            NUM_8,
    };
    public Animal (int x, int y, float speed) {
        float positionSet = 0;
        this.x = x;
        this.y = y;
        this.speed = speed;
        animal_texture = new Texture("Animals/cutiehamster.png");
        //Each position has a step of 7.5 units when we have a length of 8 positions
        for(int i = 0; i < positions.length; i++) {
            positionSet += ((float) (maxLim - minLim) / positions.length);
            positions[i] = positionSet;
            System.out.println(positions[i]);
        }
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, 5, 15);
        currentPos = y;

        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(Gdx.input.isKeyPressed(key[i])) {
                GameHandler.touchPins[i] = true;
                for(int j = 0; j < GameHandler.numTouchPins; j++) {
                    if(j != i)
                        GameHandler.touchPins[j] = false;
                }
            }
        }

        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(GameHandler.touchPins[i]) {
                if(currentPos > positions[i]+GameHandler.animHysteresis)
                    y -= Gdx.graphics.getDeltaTime()*speed;
                else if(currentPos < positions[i]-GameHandler.animHysteresis)
                    y += Gdx.graphics.getDeltaTime()*speed;
            }
        }
    }
}