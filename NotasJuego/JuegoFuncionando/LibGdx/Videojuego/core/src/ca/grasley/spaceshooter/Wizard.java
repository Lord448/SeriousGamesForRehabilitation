package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Wizard {

    private int x, y;
    private Animation animation;
    private float time;
    private Texture wizard_texture;
    private TextureRegion actualFrame;

    public Wizard(int x, int y){
        this.x = x;
        this.y = y;
        wizard_texture = new Texture("Wizard/wizard.png");
        TextureRegion [][] temporal = TextureRegion.split(wizard_texture,
                wizard_texture.getWidth()/3, wizard_texture.getHeight()/4);
        idleMovement(temporal);
        time += Gdx.graphics.getDeltaTime();        //tiempo transcurrido desde el ultimo render
        actualFrame = animation.getKeyFrame(time, true);
        time = 0;
    }

    public void render(final SpriteBatch batch){
        batch.draw(actualFrame, x, y);
    }

    private void idleMovement(TextureRegion[][] tmp){
        TextureRegion [] idle_movement;
        idle_movement = new TextureRegion[3];
        int row = 0, column = 0;
        for (int i=0; i<5; i++){
            idle_movement[i] = tmp[row][column];
            column ++;
            if (column == 3){
                column = 0;
                row ++;
            }
        }
        animation = new Animation(1, idle_movement);
    }
    private void spellingMovement(TextureRegion[][] tmp){
        TextureRegion [] spelling_movement;
        spelling_movement = new TextureRegion[3];
        int row = 0, column = 0;
        for (int i = 0; i<5; i++){
            spelling_movement[i] = tmp[row][column];
            column ++;
            if(column == 3){
                column = 0;
                row ++;
            }
        }
    }
}
