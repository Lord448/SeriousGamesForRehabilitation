package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    /*CONSTANTES RÁPIDAS*/
    private static final int MAX_LIM = 70, MIN_LIM = 20;
    private static final int ANIMAL_WITDH = 9, ANIMAL_HEIGHT = 10;

    private float x, y;
    private float speed;
    private float[] positions = new float[GameHandler.numTouchPins];
    private Texture animal_texture;
    private float currentPos;
    public Animal (int x, int y, float speed) {
        float positionSet = 0;
        this.x = x;
        this.y = y;
        this.speed = speed;
        animal_texture = new Texture("Animals/cutiehamster.png");
        //Each position has a step of 7.5 units when we have a length of 8 positions
        for(int i = 0; i < positions.length; i++) {
            positionSet += ((float) (MAX_LIM - MIN_LIM) / positions.length);
            positions[i] = positionSet;
            System.out.println(positions[i]);
        }
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, ANIMAL_WITDH, ANIMAL_HEIGHT);
        currentPos = y;

        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(Gdx.input.isKeyPressed(GameHandler.key[i])) {
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