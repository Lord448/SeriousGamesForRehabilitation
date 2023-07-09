//todo make appear the fruit when is picked
package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FoodStack{

    private Food[] fruits = new Food[GameHandler.numberOfFruits];
    private int[] XPos, Ypos = new int[GameHandler.numberOfFruits];
    private int fruitPicked = 0;
    private int fruitsInBase;
    private int numberOfBases;

    public FoodStack(int x, int y, int width, int height) {
        /*todo
        if(GameHandler.numberOfFruits < 3) {
            numberOfBases = 1;
            fruitsInBase = GameHandler.numberOfFruits;
        }
        else {
            int totalFruits = 0;
            fruitsInBase = 3;
            numberOfBases = fruitsInBase;

            for(int i = 0; i < fruitsInBase; i++) {
                totalFruits += i;
            }

        }

        int i = 0;
        for (Food fruit : fruits) {
            i++;
            fruit = new Food(x, y, width, height, i, false);
            if(i == 7)
                i = 0;
        }
         */
        //Provisional
        fruits[0] = new Food(x, y, width, height, Food.BANANA, false);
        fruits[1] = new Food(x+width-1, y, width, height, Food.APPLE, false);
        fruits[2] = new Food(x+(2*width)-3, y, width, height, Food.GRAPES, false);
        fruits[3] = new Food(x+(3*width)-5, y, width, height, Food.GREEN_GRAPES, false);
        fruits[4] = new Food(x+(width/2), y+height, width, height, Food.PINEAPPLE, false);
        fruits[5] = new Food(x+(width)+1, y+height, width, height, Food.KIWI, false);
        fruits[6] = new Food(x+(2*width), y+height, width, height, Food.CHEERY, false);
        fruits[7] = new Food(x+width, y+(2*height)-2, width, height, Food.STRAWBERRY, false);
    }

    public void render(final SpriteBatch batch) {
        for(Food fruit : fruits) {
            fruit.render(batch);
        }
        if(GameHandler.foodPicked) {
            if(fruitPicked < GameHandler.numberOfFruits) {
                fruits[fruitPicked].disappear();
                GameHandler.foodPicked = false;
            }
            fruitPicked++;
            GameHandler.currentFruit = fruitPicked;
        }
    }

    /*todo
    private int searchNumberOfBases(int number, int totalFruits) {
        final int initialBase = 3;
        if (number <= totalFruits) {
            return 0;
        }
        else {
            //Calculates the total fruits in determinate bases
            for(int i = 0; i < ; i++) {

            }
        }

    }

     */
}
