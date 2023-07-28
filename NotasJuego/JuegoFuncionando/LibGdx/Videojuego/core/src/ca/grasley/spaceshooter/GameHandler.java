package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.F;
import static com.badlogic.gdx.Input.Keys.G;
import static com.badlogic.gdx.Input.Keys.H;
import static com.badlogic.gdx.Input.Keys.J;
import static com.badlogic.gdx.Input.Keys.K;
import static com.badlogic.gdx.Input.Keys.L;

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
            A, S, D, F, G, H, J, K, L
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
