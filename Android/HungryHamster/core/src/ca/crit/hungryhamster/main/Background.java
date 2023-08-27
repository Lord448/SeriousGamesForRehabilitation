package ca.crit.hungryhamster.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ca.crit.hungryhamster.GameHandler;

public class Background {
    /*GRAPHICS*/
    private final Texture[] dynamicBackgrounds;
    private final Texture[] staticBackgrounds;
    /*TIMING*/
    private final float[] backgroundOffsets = {0,0};
    private final float scrollingSpeed;

    public Background(){
        /*BACKGROUNDS*/
        dynamicBackgrounds = new Texture[2];
        dynamicBackgrounds [0] = new Texture("Background/layer_sky.png");
        dynamicBackgrounds [1] = new Texture("Background/layer_clouds.png");

        staticBackgrounds = new Texture[6];
        staticBackgrounds[0] = new Texture("Background/layer0_grasp.png");
        staticBackgrounds[1] = new Texture("Background/layer1_grasp.png");
        staticBackgrounds[2] = new Texture("Background/layer2_grasp.png");
        staticBackgrounds[3] = new Texture("Background/layer3_tree0.png");
        staticBackgrounds[4] = new Texture("Background/layer4_tree1.png");
        staticBackgrounds[5] = new Texture("Background/layer5_grasp.png");

        scrollingSpeed =(float)(GameHandler.WORLD_WIDTH/4);
    }

    public void renderDynamicBackground(float deltaTime, final SpriteBatch batch) {

        backgroundOffsets [1]+= deltaTime * scrollingSpeed / 16;
        if( backgroundOffsets[1] > GameHandler.WORLD_WIDTH){
            backgroundOffsets [1]= 0;
        }
        batch.draw(dynamicBackgrounds[0], 0, 0, GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT);
        batch.draw(dynamicBackgrounds[1], -backgroundOffsets [1], GameHandler.WORLD_HEIGHT/2, GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT/2);
        batch.draw(dynamicBackgrounds[1], -backgroundOffsets [1] + GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT/2 , GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT/2);
    }
    public void renderStaticBackground(final SpriteBatch batch){
        batch.draw(staticBackgrounds[0],0, 0, GameHandler.WORLD_WIDTH*2, GameHandler.WORLD_HEIGHT);
        batch.draw(staticBackgrounds[1],0, 0, GameHandler.WORLD_WIDTH*2, GameHandler.WORLD_HEIGHT);
        batch.draw(staticBackgrounds[2],0, 0, GameHandler.WORLD_WIDTH*2, GameHandler.WORLD_HEIGHT);
        batch.draw(staticBackgrounds[3],-43, 0, GameHandler.WORLD_WIDTH*2, GameHandler.WORLD_HEIGHT);
        batch.draw(staticBackgrounds[5],-30, 0, GameHandler.WORLD_WIDTH*2, GameHandler.WORLD_HEIGHT);
    }
}
