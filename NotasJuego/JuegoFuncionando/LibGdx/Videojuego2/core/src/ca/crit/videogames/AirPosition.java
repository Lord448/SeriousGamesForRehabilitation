package ca.crit.videogames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AirPosition {
    private int x, y, distance;
    /*CONSTANTES RÁPIDAS*/
    private static final int RULER_WIDTH = 3, RULER_HEIGHT = 20;
    private static final int ARROW_WIDTH = 10, ARROW_HEIGHT = 5;
    private static final int RULER_LIMIT = 29;
    /*ARROW AND RULER FOR INDICATE POSITION*/
    private Texture ruler, arrow;
    private boolean pushing = false;       //bandera para mover la flecha
    private boolean pushUp = false;
    private int speed = 7;

    public AirPosition(int x, int y, int distance){
        this.x = x;
        this.y = y;
        this.distance = 64 - (distance * 4); //1cm de regla  = 4 en eje Y
                                            // y el 0cm está posicionado en 64 de eje Y
        ruler = new Texture(Gdx.files.internal("AirPosition/ruler.png"));
        arrow = new Texture(Gdx.files.internal("AirPosition/left_arrow.png"));
        GameHandler.offset = y + 37;
    }

    public void render(float deltaTime, final SpriteBatch batch){
        batch.draw(ruler, x, y, RULER_WIDTH*2, RULER_HEIGHT*2);
        batch.draw(arrow, (float) (x+6), (float) GameHandler.offset, ARROW_WIDTH, ARROW_HEIGHT);

        pushing = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        pushUp = Gdx.input.isKeyPressed(Input.Keys.UP);
        GameHandler.distance = distance;

        if(pushing){
            GameHandler.offset -= deltaTime * speed;
            if(GameHandler.offset <= RULER_LIMIT){
                GameHandler.offset = RULER_LIMIT;
            }
        }else if(pushUp) {
            GameHandler.offset += deltaTime * speed;
            if(GameHandler.offset >= y + 37){
                GameHandler.offset = y + 37;
            }
        }
    }
}
