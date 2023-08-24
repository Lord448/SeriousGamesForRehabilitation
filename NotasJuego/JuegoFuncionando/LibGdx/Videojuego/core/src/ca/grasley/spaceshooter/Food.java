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
    private int x;
    private int collectedFood_X = 57, collectedFood_Y = 87;
    private TextureRegion[] food;
    private Texture image;

    public Food(int x, int width, int height){
        this.x = x;
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
        /*BORRANDO LAS FRUTAS DEL TRONCO DEL ÁRBOL POSICIÓN A POSICIÓN ALCANZADA*/
        for(int i = food.length-1; i>GameHandler.counter; i--) {
            batch.draw(food[i], x, GameHandler.foodPositions[i], width, height);
        }

        /*CREANDO POSICIONES DESIGNADAS EN LA CASA DEL ÁRBOL PARA DIBUJAR AHÍ LAS FRUTAS*/
        int z = 0;
        int[] topFood_Y = new int[8], topFood_X = new int[8];
        for(int axisY = 0 ; axisY < 4 ; axisY ++){
            for(int axisX = 0; axisX < 2 ; axisX ++){
                topFood_X [z] = axisX;
                topFood_Y [z]= axisY;
                z++;
            }
        }

        /*DIBUJANDO LAS FRUTAS RECOGIDAS EN LAS POSICIONES DESIGNADAS POSICIÓN A POSICIÓN ALCANZADA*/
        for(int i = -1; i< GameHandler.counter; i++){
            if(GameHandler.counter == 8){
                break;
            }
            batch.draw(food[i + 1],
                    collectedFood_X + (width * topFood_X[i+1]),
                        (int)(collectedFood_Y + (1.4* height * topFood_Y[i+1])),
                       width, height);
        }
    }

    public void render(final SpriteBatch batch) {
        /*
        if(isCarriable)
            System.out.println("bool appear " + hasToAppear + " fruit " + fruit);
         */
        if(GameHandler.foodCarrying && isCarriable && hasToAppear)
            batch.draw(food[fruit], x, GameHandler.AnimalY, width, height);
        else if(!isCarriable)
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
