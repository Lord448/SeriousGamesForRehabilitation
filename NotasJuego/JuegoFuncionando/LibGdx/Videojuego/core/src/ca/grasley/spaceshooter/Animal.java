package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    private int width = 7, height = 10;
    private float x, y;
    private float speed;
    private float[] positions = new float[GameHandler.numTouchPins];
    private Texture animal_texture;
    private float currentPos;
    private int currentPin;
    private int nextPin = 0;

    private Sound victorySound;
    public Animal (int x, int y, int width, int height, int max_lim, int min_lim, float speed) {
        float positionSet = 0;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        GameHandler.foodSaved = max_lim;
        animal_texture = new Texture("Animals/cutiehamster.png");
        victorySound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Effects/achievement.ogg"));
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
        climb();
    }

    private void touchPins(){
        for(int i = 0; i < GameHandler.numTouchPins; i++) {
            if(Gdx.input.isKeyJustPressed(GameHandler.key[i])) {
                currentPin = i;
                if(currentPin == nextPin){
                    GameHandler.touchPins[i] = true;
                    nextPin++;
                }
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
                }
                else if(currentPos < positions[i]-GameHandler.animHysteresis){
                    y += Gdx.graphics.getDeltaTime()*speed;
                    if(y >= positions[i] - GameHandler.animHysteresis){
                        GameHandler.foodPicked = true;
                        GameHandler.animalPositions[i] = y;
                        GameHandler.counter ++;
                        if(GameHandler.counter == 8){ //Llegó a la casa
                            winSound();
                            System.out.println("Ganaste");
                        }
                    }
                }
            }
        }
    }

    private void winSound() {
        long id = victorySound.play(GameHandler.musicVolume);
        victorySound.setPitch(id, 1);
        victorySound.setLooping(id, false);
    }

    public void dispose(){
        victorySound.dispose();
    }
}