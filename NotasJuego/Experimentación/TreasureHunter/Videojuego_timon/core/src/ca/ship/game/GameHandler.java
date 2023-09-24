package ca.ship.game;

//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameHandler {
    public static int WORLD_WIDTH = 128;
    public static int WORLD_HEIGHT = 72;
    public static double hysteresis = 0.5;

    /*TIMON*/
    public static boolean reached = false;      // Bandera para saber si se alcanzó la posición deseada en el tiempo de sostenimiento estipulado
    //public static int position_setPoint;        // Número de posición en timón a llegar (sentido anti horario del reloj)
    //public static float chronometer;            // Tiempo transcurrido al mantener timon en cierta posición

    /*COLLISION*/
    public static boolean collided;             // Colisionan barco-cofre
    public static boolean treasureAppeared;     // Apareció un cofre

    /*SHIP*/
    public static boolean shipGoDown;           // Barco debe bajar
    public static boolean shipGoUp;             // Barco debe subir

    /*TREASURE*/
    public static float treasurePosition;
    public static int counter;
    //public static ShapeRenderer shapeShip;
    //public static ShapeRenderer shapeTreasure;

    public static boolean onomatopoeiaAppear;
}
