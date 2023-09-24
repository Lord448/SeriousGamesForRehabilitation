package ca.ship.game;

import com.badlogic.gdx.Gdx;
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
    TextureRegion user, computer;
    Texture userTexture, computerTexture, circleTexture;
    public Sprite user_sprite, computer_sprite;

    public CircleBar(float x, float y, float WIDTH, float HEIGHT, float speed_computer, float speed_user){
        this.x = x;
        this.y = y;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.speed_computer = speed_computer;
        this.speed_user = speed_user;

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

        angle_computer = 0;
        angle_user = 0;

        maxDistance = 90;   // In degrees

    }
    public void render(float deltaTime, final SpriteBatch batch){
        batch.draw(circleTexture, x, y, WIDTH, HEIGHT);

        /*UPDATE THE RANGE LIMIT ALLOWED TO MAKE PARALLAX*/
        rangeHigh = angle_computer + 20;
        rangeLow = angle_computer - 20;

        /* COMPUTER CIRCLE STOPS IF IS TOO FAR AWAY FROM USER CIRCLE*/
        lastAngle = angle_computer;
        if(angle_computer > angle_user + maxDistance){
            angle_computer = lastAngle;
        }else {
            angle_computer += speed_computer;
        }

        if(Gdx.input.isTouched()) angle_user += deltaTime * speed_user;     // How user circle go forward

        computer_sprite.setRotation(angle_computer);
        user_sprite.setRotation(angle_user);

        computer_sprite.draw(batch);
        user_sprite.draw(batch);

        /*IF USER CIRCLE IS NEAR COMPUTER CIRCLE, PARALLAX HAPPENS*/
        if(angle_user < rangeHigh && angle_user > rangeLow) {
            GameHandler.reached = true;
        }else {
            GameHandler.reached = false;
        }
    }
}
