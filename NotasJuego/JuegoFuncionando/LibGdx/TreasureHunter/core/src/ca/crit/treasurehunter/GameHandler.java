package ca.crit.treasurehunter;


public class GameHandler {
    public static int WORLD_WIDTH = 128;
    public static int WORLD_HEIGHT = 72;
    public static double hysteresis = 0.5;

    public static float playedTime = 0;         // Elapsed game time
    public static int RoundTrips = 0;          // Number of times the computer circle went forward-back
    /*TIMON*/
    public static boolean reached = false;      // Bandera para saber si se alcanzó la posición deseada en el tiempo de sostenimiento estipulado

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
