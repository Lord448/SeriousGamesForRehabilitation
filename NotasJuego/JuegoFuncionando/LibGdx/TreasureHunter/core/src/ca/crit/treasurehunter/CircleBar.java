package ca.crit.treasurehunter;

import static ca.crit.treasurehunter.GameHandler.RoundTrips;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CircleBar {
    float WIDTH, HEIGHT;
    float x,y;
    float speed_computer, speed_user;       // How fast the circles move forward
    float angle_computer, angle_user;
    float lastAngle, maxDistance;           // Last angle saves the last position of circle_computer when it exceed the maxDistance between both circles
    float rangeHigh, rangeLow;              // If user circle is between the range high and low, parallax go on
    float beginningAngle, endAngle;        // An init and fin angle for a range of the user movement trough the shoulder wheel
    boolean goForward, goBack, stop;              // Flags to make go forward or go back the computer circle in a certain range
    TextureRegion user, computer;
    Texture userTexture, computerTexture, circleTexture;
    public Sprite user_sprite, computer_sprite;

    public CircleBar(float x, float y, float WIDTH, float HEIGHT, float speed_computer, float speed_user, float beginningAngle, float endAngle){
        this.x = x;
        this.y = y;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.speed_computer = speed_computer;
        this.speed_user = speed_user;
        this.beginningAngle = beginningAngle;
        this.endAngle = endAngle;

        userTexture = new Texture("Objects/circle_user.png");
        computerTexture = new Texture("Objects/circle_computer.png");
        circleTexture = new Texture("Objects/circle.png");

        user = new TextureRegion(userTexture);
        computer = new TextureRegion(computerTexture);

        user_sprite = new Sprite(user);
        computer_sprite = new Sprite(computer);

        /* MODIFY THE SIZE AND POSITION OF THE SPRITES*/
        user_sprite.setSize(WIDTH, HEIGHT);
        computer_sprite.setSize(WIDTH, HEIGHT);
        user_sprite.setX(x);
        user_sprite.setY(y);
        computer_sprite.setX(x);
        computer_sprite.setY(y);

        /* SET THE RADIUS SPIN CIRCLES*/
        user_sprite.setOrigin((user_sprite.getWidth()/2), (user_sprite.getHeight()/2));
        computer_sprite.setOrigin((computer_sprite.getWidth()/2), (computer_sprite.getHeight()/2));

        angle_computer = beginningAngle +1;
        angle_user = beginningAngle + 1;
        goForward = true;

        maxDistance = 40;   // In degrees

    }
    public void render(float deltaTime, final SpriteBatch batch){
        batch.draw(circleTexture, x, y, WIDTH, HEIGHT);
        computer_sprite.draw(batch);
        user_sprite.draw(batch);
        computer_sprite.setRotation(angle_computer);
        user_sprite.setRotation(angle_user);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) angle_user += deltaTime * speed_user;     // How user circle go forward
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) angle_user -= deltaTime * speed_user;     // How user circle go back

        /*IF USER CIRCLE IS NEAR COMPUTER CIRCLE, PARALLAX HAPPENS*/
        rangeHigh = angle_computer + 20;    // Maximum angle to make parallax
        rangeLow = angle_computer - 20;     // Minimum angle to make parallax
        if(angle_user < rangeHigh && angle_user > rangeLow) {
            GameHandler.reached = true;
        }else {
            GameHandler.reached = false;
        }

        /*RANGE OF CIRCLES MOVEMENT TROUGH THE WHEEL*/
        lastAngle = angle_computer;
        if(goForward){
            angle_computer += deltaTime * speed_computer;
            if(angle_computer >= endAngle){
                goBack = true;
                goForward = false;
            }
            if(Math.abs(angle_computer) - Math.abs(angle_user) > maxDistance) stop = true; else stop = false;
        }
        if(goBack){
            angle_computer -= deltaTime * speed_computer;
            if(angle_computer < beginningAngle){
                goForward = true;
                goBack = false;
                RoundTrips ++;
            }
            if( Math.abs(angle_user) - Math.abs(angle_computer) > maxDistance) stop = true; else stop = false;
        }
        if(stop) {
            goBack = false;
            goForward = false;
            angle_computer = lastAngle;
            if(Math.abs(angle_computer) - Math.abs(angle_user) < Math.abs(maxDistance)){
                if( angle_computer > angle_user) goForward = true;
                if( angle_computer < angle_user) goBack = true;
            }
        }
    }
}
/*
lastAngle = angle_computer;
        if(angle_computer > angle_user + maxDistance){
            angle_computer = lastAngle;
        }else {
            angle_computer += speed_computer;
        }
 */
