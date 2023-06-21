package ca.crit.videogames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    /*WORLD PARAMETERS*/
    private int WORLD_WIDTH = 128;
    private int WORLD_HEIGHT = 72;
    /*GRAPHICS*/
    private Texture dynamicBackground;
    private Texture[] staticBackgrounds;
    /*TIMING*/
    private float backgroundOffsets;
    private float scrollingSpeed;

    Background(){
        /*BACKGROUNDS*/
        dynamicBackground = new Texture(Gdx.files.internal("Background/1.png"));
        staticBackgrounds = new Texture[3];
        staticBackgrounds[0] = new Texture(Gdx.files.internal("Background/2.png"));
        staticBackgrounds[1] = new Texture(Gdx.files.internal("Background/3.png"));
        staticBackgrounds[2] = new Texture(Gdx.files.internal("Background/4.png"));

        scrollingSpeed =(float)(WORLD_HEIGHT/4);
    }

    public int getWORLD_WIDTH() {
        return WORLD_WIDTH;
    }

    public int getWORLD_HEIGHT() {
        return WORLD_HEIGHT;
    }

    public void renderDynamicBackground(float deltaTime, final SpriteBatch batch){
        backgroundOffsets += deltaTime * scrollingSpeed / 16;
        if( backgroundOffsets > WORLD_WIDTH){
            backgroundOffsets = 0;
        }
        batch.draw(dynamicBackground, -backgroundOffsets, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(dynamicBackground, -backgroundOffsets + WORLD_WIDTH, 0 , WORLD_WIDTH, WORLD_HEIGHT);
    }
    public void renderStaticBackground(final SpriteBatch batch){
        batch.draw(staticBackgrounds[0],0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(staticBackgrounds[1],0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(staticBackgrounds[2],0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

}
