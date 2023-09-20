package ca.crit.hungryhamster;

import static com.badlogic.gdx.Input.Keys.B;
import static com.badlogic.gdx.Input.Keys.C;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.F;
import static com.badlogic.gdx.Input.Keys.G;
import static com.badlogic.gdx.Input.Keys.H;
import static com.badlogic.gdx.Input.Keys.J;
import static com.badlogic.gdx.Input.Keys.K;
import static com.badlogic.gdx.Input.Keys.L;
import static com.badlogic.gdx.Input.Keys.M;
import static com.badlogic.gdx.Input.Keys.N;
import static com.badlogic.gdx.Input.Keys.Q;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.V;
import static com.badlogic.gdx.Input.Keys.W;
import static com.badlogic.gdx.Input.Keys.E;
import static com.badlogic.gdx.Input.Keys.R;
import static com.badlogic.gdx.Input.Keys.T;
import static com.badlogic.gdx.Input.Keys.X;
import static com.badlogic.gdx.Input.Keys.Y;
import static com.badlogic.gdx.Input.Keys.U;
import static com.badlogic.gdx.Input.Keys.I;
import static com.badlogic.gdx.Input.Keys.O;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.Z;
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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

import ca.crit.hungryhamster.main.Food;

public class GameHandler {
    //Global definitions for debug
    public static final int DEBUG_GAME = 0;
    public static final int DEBUG_MENU = 1;
    public static final int DEBUG_DB = 2;
    public static final int DEBUG_NONE = 3;
    public static final int DEBUG_MODE = DEBUG_NONE; //Debug constant
    //Global Constants
    public static final int LADDER_MAX_STEPS = 32;
    public static final int DESKTOP_ENV = 0;
    public static final int MOBILE_ENV = 1;
    public static final int LEFT_HAND = 2;
    public static final int RIGHT_HAND = 3;
    public static final int BOTH_HANDS = 4;
    //Global variables
    public static int environment;
    public static int numHouseSteps = 11;
    public static int maxStep;
    public static int minStep;
    public static int extraStep = 0;
    public static float sessionTime;
    public static int sessionReps;
    public static final double animHysteresis = 0.30;
    public static int WORLD_WIDTH = 72;
    public static int WORLD_HEIGHT = 128;
    public static float musicVolume;
    public static float effectsVolume = musicVolume / 5;
    public static boolean wizardSpell  = false;
    public static float[] foodPositions;
    public static int countsToWin = numHouseSteps;
    public static boolean startGame = false;
    public static boolean[] touchPins = new boolean[LADDER_MAX_STEPS];
    public static String playerID;
    public static String playerName;
    public static String playerGender;
    public static int playerWorkingHand;
    public static int playerAge;
    public static final int[] key = {
            NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,
            Q, W, E, R, T, Y, U, I, O, P,
            A, S, D, F, G, H, J, K, L,
            Z, X, C, V, B, N, M
    };
    public static final String[] strReceptions = new String[numHouseSteps];
    
    public static void init(float musicVolume, int env) {
        //Setting global variables
        GameHandler.musicVolume = musicVolume;
        GameHandler.environment = env;
        Arrays.fill(GameHandler.touchPins, false);
        for(int i = 0; i < numHouseSteps; i++)
            strReceptions[i] = "T" + i;
    }
}
