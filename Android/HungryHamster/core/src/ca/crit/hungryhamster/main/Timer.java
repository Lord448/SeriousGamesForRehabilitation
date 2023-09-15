package ca.crit.hungryhamster.main;

import com.badlogic.gdx.Gdx;

public class Timer {
    public enum Modes{
        ONE_SHOT, //Calls one callback, then it stops
        PERIODIC, //Call a callback periodically
        TIME_MEASURE //Just measure the time
    }

    private Modes mode;
    private PeriodElapsedCallback periodElapsedCallback;
    private float timeMinutes = 0f;
    private float timeSeconds = 0f;
    private float desiredTime;
    private boolean timerStarted = false;

    public Timer(Modes mode, float desiredTime) {
        this.mode = mode;
        this.desiredTime = desiredTime;
    }

    public Timer(Modes mode) {
        this.mode = mode;
    }

    public void start(float time) {
        desiredTime = time;
        timerStarted = true;

        switch (mode) {
            case ONE_SHOT:

            break;
            case PERIODIC:
            break;
            case TIME_MEASURE:
            break;
        }
    }

    public void stop() {
        switch (mode) {
            case ONE_SHOT:
                break;
            case PERIODIC:
                break;
            case TIME_MEASURE:
                break;
        }
    }

    public void stopNotHandle() {

    }

    public void timerCheck() {
        int timeMinutes_int = 0;
        int timeMinutes_float = 0;
        if(timerStarted) {
            timeSeconds += Gdx.graphics.getDeltaTime();
            timeMinutes += Gdx.graphics.getDeltaTime()/10;
            if(timeSeconds <= 60) {
                timeMinutes += (int) timeMinutes + 1;
            }
        }
    }

    public interface PeriodElapsedCallback {
        void periodElapsedCallback();
    }

    public void setPeriodElapsedCallback(PeriodElapsedCallback eventHandler) {
        periodElapsedCallback = eventHandler;
    }
}
