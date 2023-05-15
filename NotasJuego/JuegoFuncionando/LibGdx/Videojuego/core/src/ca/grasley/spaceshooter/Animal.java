package ca.grasley.spaceshooter;

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
        if(Gdx.input.justTouched()){
            y += 5;
            if(y > 80){
                y = 5;
            }
        }
    }
}
