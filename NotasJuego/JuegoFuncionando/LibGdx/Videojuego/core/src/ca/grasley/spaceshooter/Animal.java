package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animal {
    private int x, y;
    private Texture animal_texture;

    public Animal (int x, int y){
        this.x = x;
        this.y = y;

        animal_texture = new Texture("Animals/cutiehamster.png");
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, 5, 15);
        if(Gdx.input.isKeyJustPressed(NUM_1)){
            y = 20;
        }
        if(Gdx.input.isKeyJustPressed(NUM_2)){
            y = 40;
        }
        if(Gdx.input.isKeyJustPressed(NUM_3)){
            y = 60;
        }
        if(Gdx.input.isKeyJustPressed(NUM_4)){
            y = 80;
        }
        /*
        if(Gdx.input.isKeyJustPressed(NUM_1)){
            y += 5;
            if(y > 80){
                y = 5;
            }
        }*/
    }
}
