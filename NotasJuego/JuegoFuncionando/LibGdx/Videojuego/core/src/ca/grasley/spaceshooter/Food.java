package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_0;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;
import static com.badlogic.gdx.Input.Keys.NUM_5;
import static com.badlogic.gdx.Input.Keys.NUM_6;
import static com.badlogic.gdx.Input.Keys.NUM_7;
import static com.badlogic.gdx.Input.Keys.NUM_8;
import static com.badlogic.gdx.Input.Keys.NUM_9;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Food {
    private int width, height;
    private int x;

    private TextureRegion[] food;
    private Texture image;

    public Food(int x, int width, int height){
        this.x = x;
        this.width = width;
        this.height = height;

        image = new Texture(Gdx.files.internal("Food/food.png"));
        TextureRegion [][] tmp = TextureRegion.split(image, image.getWidth()/4, image.getHeight()/2);
        food = new TextureRegion[8];
        int j = 0, i = 0;
        for(int w = 0; w< food.length; w++){
            food[w] = tmp [j][i];
            i++;
            if(i >= 4){
                j++;
                i = 0;
            }
        }
    }
    public void render(final SpriteBatch batch){

        if(GameHandler.foodPicked == false){
            foodStack(batch);
        }else{
            foodCollected(batch);
        }
    }

    private void foodStack(final SpriteBatch batch){
        for(int i=0; i< food.length; i++){
            if(GameHandler.animalPositions[i] != GameHandler.foodPositions[i]){
                batch.draw(food[i], x, GameHandler.foodPositions[i], width, height);
            }
        }
    }
    private void foodCollected(final SpriteBatch batch){
        for(int i = food.length-1; i>GameHandler.counter; i--) {
            batch.draw(food[i], x, GameHandler.foodPositions[i], width, height);
        }
    }
}
