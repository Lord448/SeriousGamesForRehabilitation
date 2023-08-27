package ca.crit.hungryhamster;

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
    //Global Constants
    public static final int DESKTOP_ENV = 0;
    public static final int MOBILE_ENV = 1;
    public static int environment;
    public static final int numHouseSteps = 9;
    public static final int countsToHouse = 8;
    public static final double animHysteresis = 0.30;
    public static int WORLD_WIDTH = 72;
    public static int WORLD_HEIGHT = 128;
    public static float musicVolume;
    public static float effectsVolume = musicVolume / 5;
    public static boolean wizardSpell  = false;
    public static float[] foodPositions = new float[numHouseSteps];
    public static float[] animalPositions = new float[numHouseSteps];
    public static int foodSaved;
    public static int animalCounter = -1;
    public static int countsToWin = 8;
    public static boolean foodPicked = false;
    public static boolean[] touchPins = new boolean[numHouseSteps];
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
