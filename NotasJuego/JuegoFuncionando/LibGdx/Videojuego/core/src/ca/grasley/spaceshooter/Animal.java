package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    private int width = 7, height = 10;
    private float x, y;
    private float speed;
    private float[] positions = new float[GameHandler.numTouchPins];
    private Texture animal_texture;
    private float currentPos;

    int past = 0, current = 0;
    boolean sequential = false;
    public Animal (int x, int y, int width, int height, int max_lim, int min_lim, float speed) {
        float positionSet = 0;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        GameHandler.foodSaved = max_lim;
        animal_texture = new Texture("Animals/cutiehamster.png");
        //Each position has a step of 7.5 units when we have a length of 8 positions
        for(int i = 0; i < positions.length; i++) {
            positionSet += ((float) (max_lim - min_lim) / positions.length);
            positions[i] = positionSet;
            GameHandler.foodPositions[i] = positions[i];
            System.out.println(positions[i]);
        }
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, width, height);
        touchPins();
        isSequential();
        if(sequential){
            climb();
        }
    }

    private void touchPins(){
        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(Gdx.input.isKeyPressed(GameHandler.key[i])) {
                GameHandler.touchPins[i] = true;
                for(int j = 0; j < GameHandler.numTouchPins; j++) {
                    if(j != i)
                        GameHandler.touchPins[j] = false;
                }
            }
        }
    }

    private void climb(){
        currentPos = y;
        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(GameHandler.touchPins[i]) {
                if(currentPos > positions[i]+GameHandler.animHysteresis)
                {
                    y -= Gdx.graphics.getDeltaTime()*speed;
                    if(y < positions[i] + GameHandler.animHysteresis){

                    }
                }
                else if(currentPos < positions[i]-GameHandler.animHysteresis){
                    y += Gdx.graphics.getDeltaTime()*speed;
                    if(y >= positions[i] - GameHandler.animHysteresis){
                        GameHandler.foodPicked = true;
                        GameHandler.animalPositions[i] = y;
                        GameHandler.counter ++;
                    }
                }
            }
        }
    }

    private void isSequential(){
        for(int i = 0; i<GameHandler.numTouchPins; i++){
            if(Gdx.input.isKeyPressed(GameHandler.key[i])) {
                current = (int) positions[i];
                if(current > past){
                    sequential = true;
                    System.out.println("mayor");
                } else if (current < past) {
                    sequential = false;
                    System.out.println("menor");
                }
            }
        }
        past = current;
    }
}