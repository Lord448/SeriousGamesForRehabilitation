package ca.grasley.spaceshooter;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import jdk.javadoc.internal.doclets.toolkit.util.DocFinder;

public class KeybordInput extends InputAdapter {
    private VirtualControler controler;

    public KeybordInput(VirtualControler controler) {
        this.controler = controler;
    }

    /** keyDown = cuando se pulsó una tecla */
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                case Input.Keys.W:
                    if (!controler.downMovement)
                    controler.upMovement = true;
            return true;
            case Input.Keys.DOWN:
                case Input.Keys.S:
                    if (!controler.upMovement)
                    controler.downMovement = true;
            return true;
            default:
                return false;
        }
    }

    /** keyUp = cuando se suelta una tecla */
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                case Input.Keys.W:
                    controler.upMovement = false;
            return true;
            case Input.Keys.DOWN:
                case Input.Keys.S:
                controler.downMovement = false;
            return true;
            default:
                return false;
        }
    }
}
