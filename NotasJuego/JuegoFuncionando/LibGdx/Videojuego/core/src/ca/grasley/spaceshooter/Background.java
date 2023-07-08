package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    /*GRAPHICS*/
    private final Texture[] dynamicBackgrounds;
    private final Texture[] staticBackgrounds;
    /*TIMING*/
    private final float[] backgroundOffsets = {0,0};
    private final float scrollingSpeed;

    Background(){
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

        scrollingSpeed =(float)(GameHandler.WORLD_WITDH/4);
    }

    public void renderDynamicBackground(float deltaTime, final SpriteBatch batch) {
        backgroundOffsets [0] += deltaTime * scrollingSpeed / 64;
        backgroundOffsets [1] += deltaTime * scrollingSpeed / 32;

        for(int layer = 0 ; layer < backgroundOffsets.length ; layer++){
            if(backgroundOffsets[layer] > GameHandler.WORLD_WITDH){
                backgroundOffsets[layer] = 0;
            }
            batch.draw(dynamicBackgrounds[layer], -backgroundOffsets[layer], 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
            batch.draw(dynamicBackgrounds[layer], -backgroundOffsets[layer]+ GameHandler.WORLD_WITDH, 0 , GameHandler.WORLD_HEIGTH, GameHandler.WORLD_HEIGTH);
        }
    }
    public void renderStaticBackground(final SpriteBatch batch){
        batch.draw(staticBackgrounds[0],0, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
        batch.draw(staticBackgrounds[1],0, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
        batch.draw(staticBackgrounds[2],0, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
        batch.draw(staticBackgrounds[3],-28, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
        batch.draw(staticBackgrounds[4],-12, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
        batch.draw(staticBackgrounds[5],0, 0, GameHandler.WORLD_WITDH, GameHandler.WORLD_HEIGTH);
    }

}
