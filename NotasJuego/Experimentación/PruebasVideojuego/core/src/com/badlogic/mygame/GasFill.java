package com.badlogic.mygame;

import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;
import static com.badlogic.gdx.Input.Keys.UP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GasFill {
    private int x, y;

    /*CONSTANTES RÁPIDAS*/
    private static final int barWidth = 150, barHeigth = 100; //tamaño del objeto barra
    private static final int gasWidth = 150, gasHeigth = 180; //tamaño del objeto estacion de gas
    private static final float TiempoAnimacionCompleta = 16; //en segundos
    private boolean filled = false;
    public Animation fillingAnimation;
    public Animation emptyingAnimation;
    private float Time;
    private TextureRegion [] fillMovement;
    private TextureRegion [] emptyMovement;
    private Texture imageBar, gasStation;
    private TextureRegion currentFrame;
    private TextureRegion currentEmpty_Frame;

    public GasFill(int x, int y){
        this.x = x;
        this.y = y;

        gasStation = new Texture(Gdx.files.internal("gas_station.png"));
        imageBar = new Texture(Gdx.files.internal("fillBar.png"));
        TextureRegion [][] tmp = TextureRegion.split(imageBar, imageBar.getWidth()/4, imageBar.getHeight()/4);

        fillMovement(tmp);
        emptyMovement(tmp);

        Time = 0f;
    }
    public void render(final SpriteBatch batchBar, final SpriteBatch batchGas){
        Time += Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyJustPressed(UP)){
            filled = !filled;
        }
        if(filled) {
            currentFrame = (TextureRegion) fillingAnimation.getKeyFrame(Time, true);
        }
        else {
            currentFrame = (TextureRegion) emptyingAnimation.getKeyFrame(Time, true);
        }
        batchBar.draw(currentFrame, x, y, barWidth, barHeigth);
        batchGas.draw(gasStation, x, y+80, gasWidth, gasHeigth);
    }
    private void fillMovement(TextureRegion[][] temporal) {
        fillMovement = new TextureRegion[16];
        int w = 0;
        for(int ejeY = 0; ejeY<4 ;ejeY++){
            for(int ejeX = 0; ejeX<4; ejeX++){
                fillMovement [w] = temporal [ejeY][ejeX];
                w++;
            }
        }
        fillingAnimation=new Animation<>(TiempoAnimacionCompleta/16, fillMovement);
    }
    private void emptyMovement(TextureRegion[][] temporal) {
        emptyMovement = new TextureRegion[16];
        int w = 0;
        for(int ejeY = 3; ejeY>=0 ;ejeY--){
            for(int ejeX = 3; ejeX>=0; ejeX--){
                emptyMovement [w] = temporal [ejeY][ejeX];
                w++;
            }
        }
        emptyingAnimation=new Animation<>(TiempoAnimacionCompleta/16, emptyMovement);
    }
}
