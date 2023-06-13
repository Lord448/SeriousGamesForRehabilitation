package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Wizard {
    public int x, y;
    private boolean spelling = false;
    public Animation spellingAnimation;
    public Animation idleAnimation;
    private float spellingTime, time;
    private TextureRegion [] spellingMovement;
    private TextureRegion [] idleMovement;
    private Texture image;
    private TextureRegion currentSpelling_Frame;
    private TextureRegion currentIdle_Frame;

    public Wizard(int x, int y){
        this.x = x;
        this.y = y;

        /*Cargar la img*/
        image = new Texture(Gdx.files.internal("Wizard/wizard.png"));
        TextureRegion [][] tmp = TextureRegion.split(image,
                image.getWidth()/3, image.getHeight()/4);

        /*Wizard Spelling*/
        spellingMovement(tmp);

        /*Wizard Idle*/
        idleMovement(tmp);

        spellingTime = 0f;
        time = 0f;
    }
    public void render(final SpriteBatch batch) {

        if(Gdx.input.isKeyJustPressed(NUM_1) || Gdx.input.isKeyJustPressed(NUM_2)|| Gdx.input.isKeyJustPressed(NUM_3)|| Gdx.input.isKeyJustPressed(NUM_4)){
            spelling = true;
        }
        if(spelling == true) {
            spellingTime += Gdx.graphics.getDeltaTime();
            currentSpelling_Frame = (TextureRegion) spellingAnimation.getKeyFrame(spellingTime, true);
            batch.draw(currentSpelling_Frame, x, y, 20, 30);
            if (spellingTime >= 0.78f) {
                spelling = false;
                spellingTime = 0;
            }
        }
        if(spelling == false){
            time += Gdx.graphics.getDeltaTime();
            currentIdle_Frame = (TextureRegion) idleAnimation.getKeyFrame(time, true);
            batch.draw(currentIdle_Frame, x, y, 20, 30);
        }
    }
    private void spellingMovement(TextureRegion [][] temporal){
        spellingMovement = new TextureRegion[4];
        int w = 0;
        for(int i = 0 ; i<3 ; i++){
            spellingMovement[w] = temporal[2][i];
            w++;
            if(i == 2){
                spellingMovement[w] = temporal[3][0];
                w++;
            }
        }
        spellingAnimation=new Animation<>(1/10f, spellingMovement);
    }
    private void idleMovement(TextureRegion [][] temporal){
        idleMovement = new TextureRegion[6];
        int w = 0;
        for(int i = 0; i<2 ; i++){
            for(int j = 0; j<3 ; j++){
                idleMovement[w] = temporal[i][j];
                w++;
            }
        }
        idleAnimation = new Animation<>(1/9f, idleMovement);
    }
}
