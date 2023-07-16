package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.G;
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
    private int collectedFood_X = 57, collectedFood_Y = 87;
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
        /*DIBUJAR EN LA SUBIDA DEL ÁRBOL*/
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

        /*DIBUJANDO LAS FRUTAS RECOGIDAS EN LAS POSICIONES DESIGNADAS*/
        for(int i = 0; i< GameHandler.counter; i++){
            batch.draw(food[i],
                    collectedFood_X + (width * topFood_X[i]),
                    (int)(collectedFood_Y + (1.4* height * topFood_Y[i])),
                       width, height);
        }
    }
}
