package ca.crit.hungryhamster.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import ca.crit.hungryhamster.GameHandler;

public class Animal {

    private int width = 7, height = 10;
    private float x, y;
    private final float speed;
    private final float[] positions = new float[GameHandler.numHouseSteps];
    private final Texture animal_texture;
    private int nextPin = 0;
    private final Sound victorySound;
    public Circle hitbox;
    public Animal (int x, int y, int width, int height, float speed) {
        float positionSet = 0;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        hitbox = new Circle((float)width/4, (float)height/4, (float)height/4+1);
        GameHandler.foodPositions = new float[GameHandler.numHouseSteps];
        GameHandler.animalPositions = new float[GameHandler.numHouseSteps];
        animal_texture = new Texture("Animals/cutiehamster.png");
        victorySound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Effects/achievement.ogg"));
        //Each position has a step of 7.5 units when we have a length of 8 positions
        for(int i = 0, j = 0; i < positions.length; i++) {
            positionSet += ((float) (GameHandler.REGION_MAX_LIM - GameHandler.REGION_MIN_LIM) / positions.length);
            positions[i] = positionSet;
            System.out.println("Pos:" + i + " " + positions[i]);
            if(i%2 == 0) { //If i is even
                GameHandler.foodPositions[j] = positions[i];
                j++;
            }
        }
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, width, height);
        hitbox.setPosition(x, y);
        if(GameHandler.environment == GameHandler.DESKTOP_ENV)
            checkKeyPressed(); //Only for desktop environment
        climb();
    }

    private void checkKeyPressed(){
        int currentPin;
        for(int i = 0; i < GameHandler.numHouseSteps; i++) {
            if(Gdx.input.isKeyJustPressed(GameHandler.key[i])) {
                currentPin = i;
                if(currentPin == nextPin){
                    GameHandler.touchPins[i] = true;
                    nextPin++;
                }
                for(int j = 0; j < GameHandler.numHouseSteps; j++) {
                    if(j != i)
                        GameHandler.touchPins[j] = false;
                }
            }
        }
    }

    private void climb(){
        float currentPos = y;
        //Controls the move of the animal
        for(int i = 0; i < GameHandler.numHouseSteps; i++) {
            if(GameHandler.touchPins[i]) { //Searching if we need to move the animal
                if(currentPos > positions[i]+GameHandler.animHysteresis) { //Moving down
                    y -= Gdx.graphics.getDeltaTime()*speed;
                }
                else if(currentPos < positions[i]-GameHandler.animHysteresis){ //Moving up
                    y += Gdx.graphics.getDeltaTime()*speed;
                    //Checking if we have reached the position
                    if(y >= positions[GameHandler.animalCounter+1] - GameHandler.animHysteresis) {
                        GameHandler.animalPositions[i] = y;
                        GameHandler.animalCounter++;
                        System.out.println(GameHandler.animalCounter + " | " + (GameHandler.countsToWin));
                        if(GameHandler.animalCounter == GameHandler.countsToWin) { //Finish the session
                            winSound();
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
