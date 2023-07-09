package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Food {
    public static final int BANANA = 0;
    public static int APPLE = 1;
    public static int GRAPES = 2;
    public static int GREEN_GRAPES = 3;
    public static int PINEAPPLE = 4;
    public static int KIWI = 5;
    public static int CHEERY = 6;
    public static int STRAWBERRY = 7;
    private int width, height;
    private int x, y, lastY;
    private float foodHeight = 0;

    private final boolean isCarriable;

    private boolean hasToAppear;

    private TextureRegion[] food;
    private Texture image;
    private int fruit;
    public Food(int x, int y, int width, int height, int fruit, boolean isCarriable){
        this.fruit = fruit;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isCarriable = isCarriable;
        image = new Texture(Gdx.files.internal("Food/food.png"));
        TextureRegion [][] tmp = TextureRegion.split(image, image.getWidth()/4, image.getHeight()/2);
        food = new TextureRegion[8];
        int j = 0, i = 0;
        for(int w = 0; w < food.length; w++){
            food[w] = tmp [j][i];
            i++;
            if(i >= 4){
                j++;
                i = 0;
            }
        }
    }

    public void render(final SpriteBatch batch) {
        if(GameHandler.foodCarrying && isCarriable && hasToAppear)
            batch.draw(food[fruit], x, GameHandler.AnimalY, width, height);
        else
            batch.draw(food[fruit], x, -10, width, height);
        if(!isCarriable)
            batch.draw(food[fruit], x, y, width, height);
    }
    
    public void disappear() {
        if(isCarriable)
            hasToAppear = false;
        else {
            lastY = y;
            y = -10;
        }
    }

    public void appear() {
        if(isCarriable)
            hasToAppear = true;
        else
            y = lastY;
    }
}
