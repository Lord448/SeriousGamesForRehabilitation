package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Food {
    private int width, height;
    private int x, y;
    private float foodHeight = 0;

    private TextureRegion[] food;
    private Texture image;
    private float variable;
    public Food(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
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
        variable = GameHandler.foodPositions[3];

    }

    public void render(final SpriteBatch batch){
        for(int i=0; i<food.length; i++){
            batch.draw(food[i], x, GameHandler.foodPositions[i], width, height);
        }
        /*batch.draw(food[3], x,variable , width, height);
        if(GameHandler.foodCaught){
            variable = 0;
        }
        System.out.println(GameHandler.foodCaught);*/
    }
}
