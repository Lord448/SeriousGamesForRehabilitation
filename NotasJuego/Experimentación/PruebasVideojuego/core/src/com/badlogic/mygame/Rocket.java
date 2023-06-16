package com.badlogic.mygame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Rocket {
    private static final int rocketWitdh = 100, rocketHeigth = 150;
    private boolean launched;
    private int x, y;
    private Texture rocketOff, rocketOn, plataform;

    public Rocket(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void draws(final SpriteBatch batchRocket, final SpriteBatch batchPlataform){
        rocketOff = new Texture(Gdx.files.internal("cohete_off.png"));
        rocketOn = new Texture(Gdx.files.internal("cohete_on.png"));
        plataform = new Texture(Gdx.files.internal("plataforma.png"));

        batchRocket.draw(rocketOff, x, y, rocketWitdh, rocketHeigth);
        batchPlataform.draw(plataform, x-60, y + 300, rocketWitdh+150, rocketHeigth-60);
    }
    private void inputProcess(){
        launched = Gdx.input.isKeyJustPressed(Input.Keys.UP);

        //float y =
    }
}
