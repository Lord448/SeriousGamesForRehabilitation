package ca.crit.hungryhamster.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import ca.crit.hungryhamster.GameHandler;

public class GameSounds {
    private final Music music;
    private static final Sound victory = Gdx.audio.newSound(Gdx.files.internal("Sounds/Effects/achievement.ogg"));;
    private static final Sound spell = Gdx.audio.newSound(Gdx.files.internal("Sounds/Effects/spell.ogg"));

    public GameSounds(){
        music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Music/happytown.ogg"));
        music.setVolume(GameHandler.musicVolume);
    }

    public void create(){
        music.setLooping(true);
        music.play();
    }

    public static void win() {
        long id = victory.play(GameHandler.musicVolume);
        victory.setPitch(id, 1);
        victory.setLooping(id, false);
    }

    public static void megaWin() {

    }

    public static void spell() {
        long id = spell.play(GameHandler.effectsVolume);
        spell.setPitch(id, 1);
        spell.setLooping(id, false);
    }

    public static void jump() {

    }

    public void dispose(){
        music.dispose();
    }
}

