package ca.ship.game;

import static ca.ship.game.GameHandler.reached;
import static ca.ship.game.GameHandler.treasurePosition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


import java.awt.Shape;

public class Ship {
    private float WIDTH, HEIGHT;                   // Ship Dimensions
    private float x, y, initialPosition;           // Ship positions at map
    private Texture shipTexture;
    public Rectangle rectangle;                    // Rectangle shape for the ship hit box

    public Ship(float x, float y, float WIDTH, float HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.x = x;
        this.y = y;

        initialPosition = y;
        shipTexture = new Texture("Character/Ship/ship_option3.png");
        rectangle = new Rectangle();
    }

    public void render(final SpriteBatch batch){
        batch.draw(shipTexture, (float)x, (float)y, 100, 80);

        if (reached) {                                              // If user_circle angle follows the computer_circle angle
            y = treasurePosition;                                   // Ship follows the treasure position to capture it
            rectangle.set(x, y, 100, 80/2);             // The collision ship-treasure will occurred
        }else {
            y = initialPosition;                                    // Ship keeps in its initial position
            rectangle.set(x, y + 200, 100, 80/2);    // Any collision won't occurred no matter the ship position
        }
    }

    /*
    ShapeRenderer shapeRenderer;
    shapeRenderer = new ShapeRenderer();
    shapeRenderer();
    private void shapeRenderer(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, WIDTH*2, HEIGHT*2);
        shapeRenderer.end();
    }

    if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            y += deltaTime*100;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            y -= deltaTime*100;
        }
     */
}