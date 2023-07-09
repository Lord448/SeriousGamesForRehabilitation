package ca.grasley.spaceshooter;

import static com.badlogic.gdx.Input.Keys.NUM_0;
import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;
import static com.badlogic.gdx.Input.Keys.NUM_5;
import static com.badlogic.gdx.Input.Keys.NUM_6;
import static com.badlogic.gdx.Input.Keys.NUM_7;
import static com.badlogic.gdx.Input.Keys.NUM_8;
import static com.badlogic.gdx.Input.Keys.NUM_9;

public class GameHandler {
    public static final int numTouchPins = 9;
    public static final double animHysteresis = 0.30;
    public static int WORLD_WIDTH = 72;
    public static int WORLD_HEIGHT = 128;
    public static float [] foodPositions = new float[numTouchPins];
    public static float [] animalPositions = new float[numTouchPins];
    public static boolean foodCaught = false;

    public static boolean[] touchPins = new boolean[numTouchPins];
    public static boolean wizardSpell  = false;
    public static final int[] key = {
            NUM_0,
            NUM_2,
            NUM_3,
            NUM_4,
            NUM_5,
            NUM_6,
            NUM_7,
            NUM_8,
            NUM_9
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
