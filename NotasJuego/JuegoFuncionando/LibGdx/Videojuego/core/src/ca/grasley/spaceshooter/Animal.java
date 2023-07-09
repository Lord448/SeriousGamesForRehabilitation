package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    private int maxLim, minLim;
    private int width = 7, height = 10;
    private float x, y;
    private float speed;
    private float[] positions = new float[GameHandler.numTouchPins];
    private Texture animal_texture;
    private float currentPos;
    public Animal (int x, int y, int width, int height, int maxLim, int minLim, float speed) {
        float positionSet = 0;
        this.maxLim = maxLim;
        this.minLim = minLim;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        animal_texture = new Texture("Animals/cutiehamster.png");
        //Each position has a step of 7.5 units when we have a length of 8 positions
        for(int i = 0; i < positions.length; i++) {
            positionSet += ((float) (maxLim - minLim) / positions.length);
            positions[i] = positionSet;
        }
        GameHandler.touchPins[0] = true;
    }
    public void render(final SpriteBatch batch){
        GameHandler.AnimalY = y + 9;
        batch.draw(animal_texture, x, y, width, height);
        currentPos = y;
        if((int)currentPos == positions[0]) {
            if(!GameHandler.foodCarrying)
                GameHandler.foodPicked = true;
            GameHandler.foodCarrying = true;
        }
        else if((int)currentPos == positions[GameHandler.numTouchPins - 1]) {
            if(GameHandler.foodCarrying)
                GameHandler.foodDelivered = true;
            GameHandler.foodCarrying = false;
        }

        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(Gdx.input.isKeyPressed(GameHandler.key[i])) {
                GameHandler.touchPins[i] = true;
                for(int j = 0; j < GameHandler.numTouchPins; j++) {
                    if(j != i){
                        GameHandler.touchPins[j] = false;
                    }
                }
            }
        }

        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(GameHandler.touchPins[i]) {
                if(currentPos > positions[i]+GameHandler.animHysteresis){
                    y -= Gdx.graphics.getDeltaTime()*speed;
                }
                else if(currentPos < positions[i]-GameHandler.animHysteresis){
                    y += Gdx.graphics.getDeltaTime()*speed;
                }
            }
        }
    }
}