package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.event.KeyListener;

public class Animal {
    private float x, y;
    private float speed;
    private float pos1=20, pos2=40, pos3=60, pos4 = 80;
    private float posactual;
    private static final double histeresis = 0.15;
    private boolean n1=false, n2=false, n3=false, n4=false;
    private Texture animal_texture;

    public Animal (int x, int y, float speed){
        this.x = x;
        this.y = y;
        this.speed = speed;
        animal_texture = new Texture("Animals/cutiehamster.png");
    }
    public void render(final SpriteBatch batch){
        batch.draw(animal_texture, x, y, 5, 15);
        posactual = y;
        if(Gdx.input.isKeyPressed(NUM_1)){
            n2 = false;
            n3= false;
            n4 = false;
            n1 = true;
        }
        if(Gdx.input.isKeyPressed(NUM_2)) {
            n1= false;
            n3=false;
            n4=false;
            n2 = true;
        }
        if(Gdx.input.isKeyPressed(NUM_3)) {
            n1= false;
            n2=false;
            n4=false;
            n3 = true;
        }
        if(Gdx.input.isKeyPressed(NUM_4)) {
            n1= false;
            n3=false;
            n2=false;
            n4 = true;
        }
        if(n1 == true){
            if (posactual > pos1+histeresis){
                y -= Gdx.graphics.getDeltaTime()*speed;
            }
            if (posactual < pos1-histeresis){
                y += Gdx.graphics.getDeltaTime()*speed;
            }
        }
        if(n2 == true){
            if (posactual > pos2+histeresis){
                y -= Gdx.graphics.getDeltaTime()*speed;
            }
            if (posactual < pos2-histeresis){
                y += Gdx.graphics.getDeltaTime()*speed;
            }
        }
        if(n3 == true){
            if (posactual > pos3+histeresis){
                y -= Gdx.graphics.getDeltaTime()*speed;
            }
            if (posactual < pos3-histeresis){
                y += Gdx.graphics.getDeltaTime()*speed;
            }
        }
        if(n4 == true){
            if (posactual > pos4+histeresis){
                y -= Gdx.graphics.getDeltaTime()*speed;
            }
            if (posactual < pos4-histeresis){
                y += Gdx.graphics.getDeltaTime()*speed;
            }
        }


        /*
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            y += Gdx.graphics.getDeltaTime()*speed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            y -= Gdx.graphics.getDeltaTime()*speed;
        }*/
    }
}
