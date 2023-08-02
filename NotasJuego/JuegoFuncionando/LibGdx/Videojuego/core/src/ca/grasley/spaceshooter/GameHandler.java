package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.Q;
import static com.badlogic.gdx.Input.Keys.W;
import static com.badlogic.gdx.Input.Keys.E;
import static com.badlogic.gdx.Input.Keys.R;
import static com.badlogic.gdx.Input.Keys.T;
import static com.badlogic.gdx.Input.Keys.Y;
import static com.badlogic.gdx.Input.Keys.U;
import static com.badlogic.gdx.Input.Keys.I;
import static com.badlogic.gdx.Input.Keys.O;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.A;

public class GameHandler {
    public static int WORLD_WIDTH = 72;
    public static int WORLD_HEIGHT = 128;

    public static float musicVolume = 0.03f;
    public static float effectsVolume = musicVolume / 5;

    public static final double animHysteresis = 0.30;
    public static boolean wizardSpell  = false;

    public static final int numTouchPins = 9;
    public static float[] foodPositions = new float[numTouchPins];
    public static float[] animalPositions = new float[numTouchPins];
    public static int foodSaved;
    public static int  counter = -1;
    public static boolean foodPicked = false;
    public static boolean[] touchPins = new boolean[numTouchPins];
    public static final int[] key = {
            Q, W, E, R, T, Y, U, I, O,
            P, A
    };
    public static final String[] strReceptions = {
            "T0",
            "T2",
            "T3",
            "T4",
            "T5",
            "T6",
            "T7",
            "T8",
            "T9"
    };

}
